package edu.srmist.outpass.worker;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Camunda 8 Job Workers for Hostel Out-Pass Request Workflow.
 * Matches service task definitions: 'check-outpass' and 'issue-outpass'.
 */
@Component
public class OutpassWorkers {

    private static final Logger log = LoggerFactory.getLogger(OutpassWorkers.class);

    /**
     * Job Worker: check-outpass
     * Reads: regNo, outDate, returnDate
     * Writes: nights, valid, approved (true for a day out)
     */
    @JobWorker(type = "check-outpass")
    public Map<String, Object> checkOutpass(
            @Variable(name = "regNo") String regNo,
            @Variable(name = "outDate") String outDate,
            @Variable(name = "returnDate") String returnDate) {

        log.info("[Worker: check-outpass] Evaluating out-pass request for regNo: {}, outDate: {}, returnDate: {}",
                regNo, outDate, returnDate);

        try {
            LocalDate out = LocalDate.parse(outDate);
            LocalDate ret = LocalDate.parse(returnDate);

            long nights = ChronoUnit.DAYS.between(out, ret);

            // Return date cannot be before out date, and out date cannot be in the past
            boolean notInPast = !out.isBefore(LocalDate.now());
            boolean nonNegativeNights = nights >= 0;
            boolean valid = notInPast && nonNegativeNights;

            // Day out (nights = 0) is auto-approved; overnight requires warden approval
            boolean autoApproved = valid && (nights == 0);

            log.info("[Worker: check-outpass] Result -> regNo: {}, nights: {}, valid: {}, approved: {}",
                    regNo, nights, valid, autoApproved);

            Map<String, Object> variables = new HashMap<>();
            variables.put("nights", nights);
            variables.put("valid", valid);
            variables.put("approved", autoApproved);
            return variables;

        } catch (DateTimeParseException | NullPointerException ex) {
            log.error("[Worker: check-outpass] Invalid date format encountered: outDate={}, returnDate={}. Error: {}",
                    outDate, returnDate, ex.getMessage());
            Map<String, Object> variables = new HashMap<>();
            variables.put("nights", -1L);
            variables.put("valid", false);
            variables.put("approved", false);
            return variables;
        }
    }

    /**
     * Job Worker: issue-outpass
     * Reads: regNo, outDate, studentName, parentPhone, approved, wardenRemarks
     * Writes: passStatus, passNumber
     */
    @JobWorker(type = "issue-outpass")
    public Map<String, Object> issueOutpass(
            @Variable(name = "regNo") String regNo,
            @Variable(name = "outDate") String outDate,
            @Variable(name = "studentName") String studentName,
            @Variable(name = "parentPhone") String parentPhone,
            @Variable(name = "approved") Boolean approved,
            @Variable(name = "wardenRemarks") String wardenRemarks) {

        boolean isApproved = Boolean.TRUE.equals(approved);
        String passStatus = isApproved ? "ISSUED" : "REJECTED";
        String passNumber = isApproved ? "OP-" + regNo + "-" + outDate : "NOT-ISSUED";

        String remarks = (wardenRemarks != null && !wardenRemarks.isBlank())
                ? wardenRemarks
                : (isApproved ? "Auto-approved day out pass" : "Request rejected based on criteria or invalid dates");

        if (isApproved) {
            log.info("[Worker: issue-outpass] [SMS to parent {}]: Dear Parent, out-pass {} has been ISSUED for {} (Reg: {}). Out Date: {}. Remarks: {}",
                    parentPhone, passNumber, studentName, regNo, outDate, remarks);
        } else {
            log.info("[Worker: issue-outpass] [SMS to parent {}]: Dear Parent, out-pass request REJECTED for {} (Reg: {}). Remarks: {}",
                    parentPhone, studentName, regNo, remarks);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("passStatus", passStatus);
        result.put("passNumber", passNumber);
        return result;
    }
}
