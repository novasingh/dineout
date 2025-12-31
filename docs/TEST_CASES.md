# Test Case Specification — Restaurant Management System

This file contains representative test cases mapped to techniques and test levels. Each test case includes: ID, Title, Preconditions, Steps, Test Data, Expected Result, Technique, Level.

TC-UC-01 | Place order - happy path
- Preconditions: Customer on `WelcomePage`/`MainActivity`; test Firebase cleared for test order.
- Steps:
  1. Open menu and select a dish.
  2. Set valid quantity (e.g., 2) using `setQuantity`.
  3. Add to cart.
  4. Go to `cart` and `confirmOrder` selecting a table/tablet.
  5. Submit order.
- Test Data: MenuItem A, quantity 2, table 5.
- Expected: Order created in Firebase with status `placed`; notification emitted; cart cleared.
- Technique: Use Case Testing, E2E
- Level: System

TC-BV-01 | Quantity boundary values
- Preconditions: Menu item present.
- Steps: enter quantity 0, 1, maximum allowed (e.g., 999), negative -1.
- Expected: 0/negative rejected (display `quantityInvalidPrompt`); 1 accepted; extremely large quantity either capped or warned.
- Technique: Boundary Value Analysis
- Level: Integration

TC-INV-01 | Concurrent orders consuming inventory
- Preconditions: Inventory count for Ingredient X = 2.
- Steps: Simulate two customers placing orders concurrently each requiring Ingredient X of quantity 2.
- Expected: Inventory should not go negative; either second order rejected or reserved until inventory replenished.
- Technique: Concurrency testing, Data-flow testing
- Level: Integration/System

TC-PAY-01 | Payment atomicity
- Preconditions: Order pending payment.
- Steps: Start payment; simulate network interruption mid-transaction; resume.
- Expected: Order marked billed only once on confirmed payment; retries are idempotent.
- Technique: State Transition, Error Guessing
- Level: System

TC-EMAIL-01 | Email report sending failure
- Steps: Configure SMTP with wrong TLS settings; trigger `GMailSender` to send end-of-day report.
- Expected: Email attempt logged as failed, queued for retry, system not crashing.
- Technique: Fault-injection
- Level: Integration

TC-UI-01 | Tablet reassignment race
- Steps: On `Tablets` screen, reassign table T from tablet A to B while another user tries to assign it to C.
- Expected: No two tablets claim same table; conflict resolution strategy applied.
- Technique: State Transition, Exploratory
- Level: System

TC-SEC-01 | JSSE/TLS handshake failure handling
- Steps: Force TLS handshake error when sending email; verify app handles exceptions and logs secure error.
- Expected: Exception captured; user/admin notified; no sensitive logs persisted.
- Technique: Security testing
- Level: Integration

... Additional test cases: include unit tests for `Order.calculateTotal()`, adapters null-checking, menu parsing, and back-end transaction atomicity. Full list should be expanded to cover all classes in `app/src/main/java`.

Procedure specification
- For each test case: set test environment (Firebase emulator or test project), ensure test accounts are used, record steps and evidence (screenshots, DB records), and revert DB changes after the test.
