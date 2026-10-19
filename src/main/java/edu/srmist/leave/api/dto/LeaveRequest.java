package edu.srmist.leave.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LeaveRequest {

    @NotBlank(message = "studentName is required")
    private String studentName;

    @NotBlank(message = "regNo is required")
    private String regNo;

    @NotBlank(message = "fromDate is required (yyyy-MM-dd)")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "fromDate must be in yyyy-MM-dd format")
    private String fromDate;

    @NotBlank(message = "toDate is required (yyyy-MM-dd)")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "toDate must be in yyyy-MM-dd format")
    private String toDate;

    @NotBlank(message = "reason is required")
    private String reason;

    public LeaveRequest() {
    }

    public LeaveRequest(String studentName, String regNo, String fromDate, String toDate, String reason) {
        this.studentName = studentName;
        this.regNo = regNo;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
