package edu.srmist.outpass.api.dto;

import java.time.Instant;

public class OutpassResponse {

    private Long processInstanceKey;
    private String bpmnProcessId;
    private String studentName;
    private String regNo;
    private String status;
    private String message;
    private Instant timestamp;

    public OutpassResponse() {
        this.timestamp = Instant.now();
    }

    public OutpassResponse(Long processInstanceKey, String bpmnProcessId, String studentName,
                           String regNo, String status, String message) {
        this.processInstanceKey = processInstanceKey;
        this.bpmnProcessId = bpmnProcessId;
        this.studentName = studentName;
        this.regNo = regNo;
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public Long getProcessInstanceKey() {
        return processInstanceKey;
    }

    public void setProcessInstanceKey(Long processInstanceKey) {
        this.processInstanceKey = processInstanceKey;
    }

    public String getBpmnProcessId() {
        return bpmnProcessId;
    }

    public void setBpmnProcessId(String bpmnProcessId) {
        this.bpmnProcessId = bpmnProcessId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
