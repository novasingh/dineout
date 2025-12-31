# Test Report — Restaurant Management System

This test report covers Test Design, Test Case Specification, Test Procedure Specification, Model Checking, User Acceptance, Test Log, Test Summary Report, and Test Completion Report for the Restaurant Management System app.

1. Test Objectives
- Verify correctness, reliability, usability and security of the app across Customer (ordering), Kitchen, Hall, Billing and Admin flows.
- Validate non-functional requirements: performance (response time), concurrency (multiple devices), reliability (order persistence), availability (integration with Firebase), and security of data in transit (email/SMTP TLS).

2. Test Levels
- Unit testing: core utility classes and models (e.g., `Order`, `MenuItem`, `Inventory`).
- Integration testing: activity transitions, Firebase interactions, adapters and listeners.
- System testing: end-to-end flows (Customer→Kitchen→Hall→Billing).
- Acceptance testing (UAT): executed by end users (≥30 respondents).

3. Test Techniques (Group lists; Use Case Testing is compulsory)
- Mandatory: Use Case Testing (derive test cases from end-to-end use cases such as Place Order, Prepare Order, Serve & Bill).

Black-box / Functional techniques (≥10):
  1. Use Case Testing (compulsory)
  2. Equivalence Partitioning
  3. Boundary Value Analysis
  4. Decision Table Testing
  5. State Transition Testing
  6. Exploratory Testing
  7. Error Guessing
  8. UI/GUI Testing
  9. CRUD Testing
 10. End-to-End (E2E) Flow Testing

White-box / Structural techniques (≥10):
  1. Unit testing (methods)
  2. Statement coverage
  3. Branch coverage
  4. Path coverage (representative paths)
  5. Loop testing (for menu iteration / cart totals)
  6. Data flow testing (inventory updates)
  7. Integration API testing (Firebase read/write)
  8. Mocking/stubbing external services (SMTP/Firebase)
  9. Concurrency testing (multi-device updates)
 10. Static code analysis (linters)

Non-functional techniques (≥10):
  1. Load testing (simulated multiple concurrent orders)
  2. Stress testing (beyond expected load)
  3. Soak / endurance testing
  4. Performance benchmarking (latency measurements)
  5. Usability testing (UAT questionnaire)
  6. Accessibility checks (UI contrast and touch targets)
  7. Security testing (SMTP/TLS, data at rest rules)
  8. Fault-injection testing (simulate Firebase disconnect)
  9. Recovery testing (reconnect/resend orders)
 10. Resource usage / battery impact analysis

Approach refinement and justification
- We combine Use Case (required) with state-transition and E2E testing to ensure core business flows are validated under realistic scenarios.
- White-box techniques (unit, branch, data-flow) focus on critical classes (Order, Inventory, adapters) to increase defect detection early.
- Non-functional tests target operational risks: the app depends on Firebase and SMTP; load/stress, fault-injection and recovery tests are required to evaluate availability and reliability.
- Concurrency & model checking: critical for order state transitions (placed → preparing → ready → served → billed) where multiple devices may update shared state; we use model checking to ensure absence of deadlocks and livelocks.

4. Test Case Specification (summary)
- See `docs/TEST_CASES.md` for the full set of test cases mapped to techniques and expected results. Test cases include use-case derived end-to-end tests, boundary tests for quantity inputs, invalid input tests, authentication tests, email-send failure scenarios, and billing rounding checks.

5. Test Procedure Specification
- Each test case lists preconditions, test steps, test data, expected result, actual result and postconditions (clearing DB writes where needed).
- Procedures for integration tests include instructions to use a Firebase test project (or local emulator), test account credentials, and mock SMTP configuration.

6. Model Checking
There are 4 state diagrams chosen for model checking. For each we describe the modelling approach and properties to verify.

State diagram A — Order lifecycle (Customer→Kitchen→Hall→Billing)
- Reason: This diagram is the core of the system where race conditions between kitchen updates and hall collection could cause incorrect order states.
- Technique: Model in Promela (SPIN) to check LTL properties: safety (an order cannot be both 'ready' and 'preparing' simultaneously), liveness (an order eventually reaches a terminal state when processed), and absence of deadlock.
- Properties to check: mutual exclusion on status updates; eventual delivery of status change; no circular waiting between components.

State diagram B — Table/tablet assignment
- Reason: Table-to-tablet mapping must avoid double assignment and allow reassignment safely.
- Technique: Use NuSMV model checking for CTL properties: check invariants (no two tablets claim same table) and transition consistency on reassignments.

State diagram C — Payment flow
- Reason: Payment state has external dependencies (payment confirmation) and must ensure atomic update (order marked billed only when payment confirmed).
- Technique: Use SPIN/Promela to model synchronous confirmation event and verify atomicity; check that a payment failure returns order to unpaid state and triggers retry logic.

State diagram D — Inventory/ingredient consumption
- Reason: Concurrent orders may drain ingredients; must ensure inventory does not go negative and triggers restock notifications.
- Technique: Use bounded model checking (CBMC or NuSMV) to verify all inventory update transitions maintain non-negative invariants and that low-stock notifications get emitted when thresholds cross.

7. Test Log
- The test execution and results are recorded in `docs/TEST_LOG.csv` (sample simulated log with test case IDs, status, defects, severity and remarks). This includes identified defects and assigned severities. Inspect the CSV for a detailed log.

8. User Acceptance Testing (UAT)
- Questionnaire: uses a 5-point Likert scale for usability, performance, reliability, clarity of UI, overall satisfaction, and willingness to use/recommend. Full questionnaire in `docs/UAT_QUESTIONNAIRE.md`.
- Respondents: simulated 35 respondent dataset saved in `docs/UAT_RESULTS.csv` to illustrate analysis (≥30 required). Use the included script `docs/uat_plot.py` to generate visual graphs.

9. Test Summary Report
- Execution summary: See `docs/TEST_LOG.csv` for individual results. Simulated execution found a mix of minor and critical issues focused on: quantity input validation (boundary), concurrency over order status with rapid updates, occasional email send failures (SMTP auth/timeouts), and missing inventory low-stock notification under some concurrent orders.
- Defects summary (simulated):
  - Critical: 2 (payment atomicity issue; order duplication under concurrent writes)
  - Major: 4 (inventory miscount under concurrency; email failures when TLS negotiation times out; tablet reassignment race)
  - Minor: 8 (UI text/feedback, rounding on bills, missing null checks)

10. Test Completion Report & Recommendations
- Status: System tested across unit, integration, system and acceptance levels (simulated); remaining items for production readiness:
  - Fix concurrency issues: implement transaction-like updates or server-side atomic operations (use Firebase transactions or Cloud Functions to serialize status updates).
  - Harden payment flow: ensure payment confirmation callback is the single source of truth and implement retry/backoff and idempotency keys.
  - Improve error handling for SMTP: use exponential backoff, alternate email provider, and queue unsent notifications.
  - Add inventory locking or server-side reservation when an order is confirmed, preventing negative stock.
  - Add automated tests for the critical flows (unit+integration) in CI with Firebase emulator and mocked SMTP.
  - Increase test coverage for branch and path coverage around order lifecycle and inventory modules.

Appendices and details are saved as separate artifacts in `docs/`:
- `docs/TEST_CASES.md` — full test case list and steps.
- `docs/TEST_LOG.csv` — test log (simulated execution).
- `docs/UAT_QUESTIONNAIRE.md` — UAT questionnaire and sampling plan.
- `docs/UAT_RESULTS.csv` — simulated respondent data.
- `docs/uat_plot.py` — script to generate graphs from the UAT results.

If you want, I can now (pick one): expand any specific module's test cases to method-level tests, implement sample Promela/NuSMV models for the four state machines, run the Python script to generate graphs (I can simulate here), or adapt the UAT questionnaire for a real data collection form (Google Forms/Excel template).
