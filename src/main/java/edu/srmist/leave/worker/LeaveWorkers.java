package edu.srmist.leave.worker;

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
 * Camunda 8 Job Workers for Student Leave Application Workflow.
 * Matches service task definitions: 'validate-leave' and 'notify-student'.
 */
@Component
public class LeaveWorkers {

    private static final Logger log = LoggerFactory.getLogger(LeaveWorkers.class);

    @JobWorker(type = "validate-leave")
    public Map<String, Object> validateLeave(
            @Variable(name = "regNo") String regNo,
            @Variable(name = "fromDate") String fromDate,
            @Variable(name = "toDate") String toDate) {

        log.info("[Worker: validate-leave] Validating leave request for regNo: {}, fromDate: {}, toDate: {}",
                regNo, fromDate, toDate);

        try {
            LocalDate from = LocalDate.parse(fromDate);
            LocalDate to = LocalDate.parse(toDate);
            long days = ChronoUnit.DAYS.between(from, to) + 1;
            boolean valid = days > 0 && !from.isBefore(LocalDate.now());
            boolean autoApproved = valid && (days <= 2);

            log.info("[Worker: validate-leave] Result -> leaveDays: {}, valid: {}, approved: {}",
                    days, valid, autoApproved);

            Map<String, Object> variables = new HashMap<>();
            variables.put("leaveDays", days);
            variables.put("valid", valid);
            variables.put("approved", autoApproved);
            return variables;

        } catch (DateTimeParseException | NullPointerException ex) {
            log.error("[Worker: validate-leave] Date parsing failure for fromDate={}, toDate={}: {}",
                    fromDate, toDate, ex.getMessage());
            Map<String, Object> variables = new HashMap<>();
            variables.put("leaveDays", 0L);
            variables.put("valid", false);
            variables.put("approved", false);
            return variables;
        }
    }

    @JobWorker(type = "notify-student")
    public Map<String, Object> notifyStudent(
            @Variable(name = "studentName") String studentName,
            @Variable(name = "regNo") String regNo,
            @Variable(name = "approved") Boolean approved,
            @Variable(name = "remarks") String remarks) {

        String status = Boolean.TRUE.equals(approved) ? "APPROVED" : "REJECTED";
        String effectiveRemarks = (remarks != null && !remarks.isBlank())
                ? remarks
                : (Boolean.TRUE.equals(approved) ? "Auto-approved leave request" : "Dates invalid or rejected by Faculty Advisor");

        log.info("Mail to {} ({}): your leave is {}. Remarks: {}", studentName, regNo, status, effectiveRemarks);

        Map<String, Object> result = new HashMap<>();
        result.put("leaveStatus", status);
        return result;
    }
}
