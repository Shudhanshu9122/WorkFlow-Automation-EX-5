package edu.srmist.leave.worker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LeaveWorkers Business Logic Tests")
class LeaveWorkersTest {

    private LeaveWorkers workers;

    @BeforeEach
    void setUp() {
        workers = new LeaveWorkers();
    }

    @Test
    @DisplayName("Short leave (<= 2 days) should be auto-approved")
    void testShortLeaveAutoApproved() {
        String fromDate = LocalDate.now().plusDays(2).toString();
        String toDate = LocalDate.now().plusDays(3).toString(); // 2 days inclusive

        Map<String, Object> result = workers.validateLeave("RA2311003010001", fromDate, toDate);

        assertEquals(2L, result.get("leaveDays"));
        assertEquals(true, result.get("valid"));
        assertEquals(true, result.get("approved"));
    }

    @Test
    @DisplayName("Long leave (> 2 days) requires Faculty Advisor approval")
    void testLongLeaveRequiresFA() {
        String fromDate = LocalDate.now().plusDays(2).toString();
        String toDate = LocalDate.now().plusDays(6).toString(); // 5 days inclusive

        Map<String, Object> result = workers.validateLeave("RA2311003010002", fromDate, toDate);

        assertEquals(5L, result.get("leaveDays"));
        assertEquals(true, result.get("valid"));
        assertEquals(false, result.get("approved"));
    }

    @Test
    @DisplayName("Leave dates in the past should be rejected")
    void testPastLeaveRejected() {
        String fromDate = LocalDate.now().minusDays(3).toString();
        String toDate = LocalDate.now().plusDays(1).toString();

        Map<String, Object> result = workers.validateLeave("RA2311003010003", fromDate, toDate);

        assertEquals(false, result.get("valid"));
        assertEquals(false, result.get("approved"));
    }

    @Test
    @DisplayName("Notify student when leave is approved")
    void testNotifyApproved() {
        Map<String, Object> result = workers.notifyStudent("Priya", "RA2311003010001", true, "Good standing");
        assertEquals("APPROVED", result.get("leaveStatus"));
    }

    @Test
    @DisplayName("Notify student when leave is rejected")
    void testNotifyRejected() {
        Map<String, Object> result = workers.notifyStudent("Rahul", "RA2311003010002", false, "Exceeded limit");
        assertEquals("REJECTED", result.get("leaveStatus"));
    }
}
