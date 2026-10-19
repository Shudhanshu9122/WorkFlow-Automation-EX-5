package edu.srmist.outpass.worker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OutpassWorkers Business Logic Tests")
class OutpassWorkersTest {

    private OutpassWorkers workers;

    @BeforeEach
    void setUp() {
        workers = new OutpassWorkers();
    }

    @Test
    @DisplayName("Case 1: Day out (same day, nights = 0) should be auto-approved")
    void testDayOutAutoApproved() {
        String futureDate = LocalDate.now().plusDays(2).toString();
        Map<String, Object> result = workers.checkOutpass("RA2311003010001", futureDate, futureDate);

        assertEquals(0L, result.get("nights"));
        assertEquals(true, result.get("valid"));
        assertEquals(true, result.get("approved"));
    }

    @Test
    @DisplayName("Case 2: Overnight stay (nights >= 1) requires Warden approval (approved = false)")
    void testOvernightRequiresWarden() {
        String outDate = LocalDate.now().plusDays(2).toString();
        String returnDate = LocalDate.now().plusDays(5).toString();

        Map<String, Object> result = workers.checkOutpass("RA2311003010002", outDate, returnDate);

        assertEquals(3L, result.get("nights"));
        assertEquals(true, result.get("valid"));
        assertEquals(false, result.get("approved"), "Overnight stay must not be auto-approved");
    }

    @Test
    @DisplayName("Case 3: Return date before out date should be marked invalid")
    void testReturnDateBeforeOutDate() {
        String outDate = LocalDate.now().plusDays(5).toString();
        String returnDate = LocalDate.now().plusDays(2).toString();

        Map<String, Object> result = workers.checkOutpass("RA2311003010003", outDate, returnDate);

        assertEquals(false, result.get("valid"));
        assertEquals(false, result.get("approved"));
        assertTrue((Long) result.get("nights") < 0);
    }

    @Test
    @DisplayName("Case 4: Out date in the past should be marked invalid")
    void testPastDateRejected() {
        String pastDate = LocalDate.now().minusDays(5).toString();
        String futureDate = LocalDate.now().plusDays(2).toString();

        Map<String, Object> result = workers.checkOutpass("RA2311003010004", pastDate, futureDate);

        assertEquals(false, result.get("valid"));
        assertEquals(false, result.get("approved"));
    }

    @Test
    @DisplayName("Case 5: Issue out-pass with approved = true generates OP-passNumber and ISSUED status")
    void testIssueOutpassApproved() {
        String outDate = LocalDate.now().plusDays(1).toString();
        Map<String, Object> result = workers.issueOutpass(
                "RA2311003010001",
                outDate,
                "Sudhanshu Kumar",
                "+919876543210",
                true,
                "Parent confirmed via phone"
        );

        assertEquals("ISSUED", result.get("passStatus"));
        assertEquals("OP-RA2311003010001-" + outDate, result.get("passNumber"));
    }

    @Test
    @DisplayName("Case 6: Issue out-pass with approved = false produces REJECTED status")
    void testIssueOutpassRejected() {
        String outDate = LocalDate.now().plusDays(1).toString();
        Map<String, Object> result = workers.issueOutpass(
                "RA2311003010001",
                outDate,
                "Sudhanshu Kumar",
                "+919876543210",
                false,
                "Warden rejected: disciplinary hold"
        );

        assertEquals("REJECTED", result.get("passStatus"));
        assertEquals("NOT-ISSUED", result.get("passNumber"));
    }
}
