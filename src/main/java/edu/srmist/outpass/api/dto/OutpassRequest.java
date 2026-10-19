package edu.srmist.outpass.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OutpassRequest {

    @NotBlank(message = "studentName is required")
    private String studentName;

    @NotBlank(message = "regNo is required")
    private String regNo;

    @NotBlank(message = "hostelBlock is required")
    private String hostelBlock;

    @NotBlank(message = "outDate is required (yyyy-MM-dd)")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "outDate must be in yyyy-MM-dd format")
    private String outDate;

    @NotBlank(message = "returnDate is required (yyyy-MM-dd)")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "returnDate must be in yyyy-MM-dd format")
    private String returnDate;

    @NotBlank(message = "parentPhone is required")
    private String parentPhone;

    @NotBlank(message = "purpose is required")
    private String purpose;

    public OutpassRequest() {
    }

    public OutpassRequest(String studentName, String regNo, String hostelBlock,
                          String outDate, String returnDate, String parentPhone, String purpose) {
        this.studentName = studentName;
        this.regNo = regNo;
        this.hostelBlock = hostelBlock;
        this.outDate = outDate;
        this.returnDate = returnDate;
        this.parentPhone = parentPhone;
        this.purpose = purpose;
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

    public String getHostelBlock() {
        return hostelBlock;
    }

    public void setHostelBlock(String hostelBlock) {
        this.hostelBlock = hostelBlock;
    }

    public String getOutDate() {
        return outDate;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
