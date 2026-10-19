# Variable Dictionary & Process Data Flow Note
**Course Lab:** Camunda 8 SaaS + Spring Boot Workflow Automation (EX-5)  
**Author / Student:** Sudhanshu Kumar (SRMIST)  
**Process Models:** `hostel-outpass` & `student-leave`  

---

## 1. Primary Exercise: Hostel Out-Pass Request Workflow (`hostel-outpass`)

### A. Process Inputs (REST Body: `POST /outpass`)
When a student triggers the out-pass process via HTTP POST, the following JSON payload is deserialized and injected into the process instance as initial process variables:

| Variable Name | Type | Description | Sample Value |
| :--- | :--- | :--- | :--- |
| `studentName` | `String` | Full name of the student | `"Sudhanshu Kumar"` |
| `regNo` | `String` | SRM registration number | `"RA2311003010001"` |
| `hostelBlock` | `String` | Hostel block and room number | `"M-Block, Room 412"` |
| `outDate` | `String` (ISO) | Departure date (`yyyy-MM-dd`) | `"2026-10-25"` |
| `returnDate` | `String` (ISO) | Return date (`yyyy-MM-dd`) | `"2026-10-25"` |
| `parentPhone` | `String` | Registered contact number of parent | `"+919876543210"` |
| `purpose` | `String` | Purpose of leaving campus | `"Medical checkup"` |

---

### B. Worker Variable Read/Write Matrix

```
                      ┌──────────────────────────────────────────┐
                      │          POST /outpass (REST)            │
                      └────────────────────┬─────────────────────┘
                                           │ (studentName, regNo, outDate, returnDate, ...)
                                           ▼
                      ┌──────────────────────────────────────────┐
                      │    Service Task: Check request           │
                      │    Worker: check-outpass                 │
                      └────────────────────┬─────────────────────┘
                                           │ (Writes: nights, valid, approved)
                                           ▼
                                 [Exclusive Gateway]
                             /             |             \
          =valid and nights=0       =valid and nights>=1   =not(valid)
                 │                         │                    │
                 │                 ┌───────▼────────┐           │
                 │                 │ Warden Form    │           │
                 │                 │ (Tasklist)     │           │
                 │                 └───────┬────────┘           │
                 │                         │ (approved,         │
                 │                         │  wardenRemarks)    │
                 ▼                         ▼                    ▼
             ┌──────────────────────────────────────────────────────┐
             │                  Merge Gateway                       │
             └─────────────────────────┬────────────────────────────┘
                                       │
                                       ▼
             ┌──────────────────────────────────────────────────────┐
             │    Service Task: Issue out-pass                      │
             │    Worker: issue-outpass                             │
             └─────────────────────────┬────────────────────────────┘
                                       │ (Writes: passStatus, passNumber)
                                       ▼
                                  [End Event]
```

#### 1. Worker: `check-outpass`
* **Task Type:** `check-outpass`
* **Java Method:** `OutpassWorkers#checkOutpass`
* **Variables Read:**
  * `regNo` (`String`): Registration number of the student.
  * `outDate` (`String`): Date leaving campus (`yyyy-MM-dd`).
  * `returnDate` (`String`): Date returning to campus (`yyyy-MM-dd`).
* **Variables Written:**
  * `nights` (`Long`): Number of overnight days computed as `ChronoUnit.DAYS.between(outDate, returnDate)`.
  * `valid` (`Boolean`): `true` if `nights >= 0` and `outDate >= LocalDate.now()`; otherwise `false`.
  * `approved` (`Boolean`): `true` if `valid && nights == 0` (day out is auto-approved); `false` if `nights >= 1` (requires human review) or invalid.

#### 2. Human Task: `Warden approval` (`warden-approval-form`)
* **Task Type:** Camunda User Task
* **Form ID:** `warden-approval-form`
* **Variables Read (Read-Only Context):**
  * `{{studentName}}`, `{{regNo}}`, `{{hostelBlock}}`, `{{outDate}}`, `{{returnDate}}`, `{{nights}}`, `{{parentPhone}}`, `{{purpose}}`.
* **Variables Written:**
  * `approved` (`Boolean`): Set to `true` when the warden ticks the approval checkbox, `false` otherwise.
  * `wardenRemarks` (`String`): Justification or conditions recorded by the warden.

#### 3. Worker: `issue-outpass`
* **Task Type:** `issue-outpass`
* **Java Method:** `OutpassWorkers#issueOutpass`
* **Variables Read:**
  * `regNo` (`String`): Student registration number.
  * `outDate` (`String`): Departure date.
  * `studentName` (`String`): Student name for simulated parent notification.
  * `parentPhone` (`String`): Parent phone number to receive simulated SMS.
  * `approved` (`Boolean`): Final approval decision from either auto-approval or warden.
  * `wardenRemarks` (`String`, nullable): Remarks entered by warden or defaulted.
* **Variables Written:**
  * `passStatus` (`String`): `"ISSUED"` if `approved == true`, otherwise `"REJECTED"`.
  * `passNumber` (`String`): `"OP-<regNo>-<outDate>"` if approved (e.g. `OP-RA2311003010001-2026-10-25`), or `"NOT-ISSUED"`.

---

## 2. Reference Exercise: Student Leave Workflow (`student-leave`)

| Task / Worker | Reads | Writes | Purpose |
| :--- | :--- | :--- | :--- |
| `POST /leave` (REST) | HTTP Request Body | `studentName`, `regNo`, `fromDate`, `toDate`, `reason` | Injects student leave application into Camunda 8 SaaS. |
| `validate-leave` | `regNo`, `fromDate`, `toDate` | `leaveDays` (`Long`), `valid` (`Boolean`), `approved` (`Boolean`) | Computes days `[from, to]` inclusive, checks dates not in past. Auto-approves `<= 2` days. |
| `Faculty Advisor approval` (Form: `fa-approval-form`) | `studentName`, `regNo`, `fromDate`, `toDate`, `leaveDays`, `reason` | `approved` (`Boolean`), `remarks` (`String`) | Human review in Camunda Tasklist for leaves `> 2` days. |
| `notify-student` | `studentName`, `regNo`, `approved`, `remarks` | `leaveStatus` (`String`: `"APPROVED"` / `"REJECTED"`) | Logs simulated email notification with final verdict and remarks. |
