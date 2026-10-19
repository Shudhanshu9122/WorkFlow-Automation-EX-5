# System Architecture & Technical Specifications

## 1. High-Level Architecture Overview

This project implements an enterprise-grade cloud workflow orchestration system combining **Camunda 8 SaaS** with a **Spring Boot 3.4.3 / Java 21** microservice running on the student/developer environment.

```
+-----------------------------------------------------------------------------------+
|                            DEVELOPER / LAPTOP ENVIRONMENT                         |
|                                                                                   |
|   +-----------------------+               +-----------------------------------+   |
|   |   Client / Student    |               |         Spring Boot App           |   |
|   |   (curl / Postman)    |               |     (edu.srmist.outpass)          |   |
|   +-----------+-----------+               +-----------------+-----------------+   |
|               |                                             |                     |
|               | POST /outpass                               |                     |
|               | (JSON payload)                              |                     |
|               v                                             |                     |
|   +-----------------------+                                 |                     |
|   |   OutpassController   | ──[ CreateInstanceCommand ]────>|                     |
|   +-----------------------+                                 |                     |
|                                                             |                     |
|   +-----------------------+                                 |                     |
|   |  Job Workers:         | <────[ Long-polling Jobs ]──────+                     |
|   |   - check-outpass     | ─────[ CompleteJobCommand ]────>|                     |
|   |   - issue-outpass     |                                 |                     |
|   +-----------------------+                                 |                     |
+-------------------------------------------------------------|---------------------+
                                                              | Outbound TLS (gRPC / HTTPS)
                                                              | (No inbound port required)
                                                              v
+-----------------------------------------------------------------------------------+
|                                CAMUNDA 8 SAAS CLUSTER                             |
|                                                                                   |
|   +--------------------+     +---------------------+     +--------------------+   |
|   |       Zeebe        |     |       Operate       |     |      Tasklist      |   |
|   |  Process Engine    |     |  Process Monitoring |     |  User Tasks Forms  |   |
|   +--------------------+     +---------------------+     +--------------------+   |
+-----------------------------------------------------------------------------------+
```

### Key Architectural Highlights
1. **Outbound-Only Communication:**
   * The laptop/local environment initiates all connections via outbound gRPC and HTTPS.
   * No static IP, ngrok tunnel, or router port-forwarding is needed, making it suitable for campus Wi-Fi networks and secure enterprise proxies.
2. **Auto-Deployment on Startup:**
   * The Spring Boot application leverages `@Deployment` to automatically synchronize BPMN diagrams (`hostel-outpass.bpmn`, `student-leave.bpmn`) and Camunda Form JSON schemas (`warden-approval.form`, `fa-approval.form`) upon boot.
3. **Reactive Job Streaming:**
   * Enabled via `camunda.client.worker.defaults.stream-enabled=true`. Camunda 8 Zeebe streams work items to workers with sub-millisecond response latency.
4. **Idempotence & Safety:**
   * Process instances generate deterministic pass numbers: `OP-<regNo>-<outDate>`.

---

## 2. BPMN Sequence & Routing Logic

### Hostel Out-Pass Decision Gateway Conditions (FEEL Expressions)

| Outgoing Flow | Condition Expression (FEEL) | Routing Destination | Business Rule |
| :--- | :--- | :--- | :--- |
| **Day Out** | `=valid and nights = 0` | Merge Gateway &rarr; `issue-outpass` | Same-day leave auto-approved without manual intervention. |
| **Overnight** | `=valid and nights >= 1` | User Task `Warden approval` | Requires hostel warden review via Tasklist form. |
| **Invalid Dates** | `=not(valid)` | Merge Gateway &rarr; `issue-outpass` | Return date before out date or dates in past &rarr; Rejected. |

### Bonus Escalation Mechanism
* An **Interrupting Timer Boundary Event** (`PT24H`) is attached to the `Warden approval` user task.
* If the resident warden does not complete the task within 24 hours, the execution token moves to the `Chief Warden approval (Escalated)` user task, ensuring requests are never stalled indefinitely.
