# M4: Payments

Folder: `src/main/java/com/gym/payments/` · Owner: **Owner 4** (fill in name) · Phase 1 (parallel, after Foundation merges)

## Files

| File | Purpose |
|---|---|
| `payments/Payment.java` | Model — `id`, `subscriptionId`, `amount`, `paymentDate`, `method` |
| `payments/PaymentDao.java` | Interface — `save(Payment)`, `findBySubscriptionId(long)`, `findByMemberId(long)` |
| `payments/JdbcPaymentDao.java` | `JdbcTemplate` implementation |
| `payments/PaymentService.java` | `recordPayment(...)`, `getOutstandingBalance(subscriptionId)` — sums payments vs. `subscription.finalPrice` |
| `payments/PaymentPanel.java` | Swing `JPanel` — record a payment, view payment history + outstanding balance for a subscription |
| `src/test/java/com/gym/payments/PaymentServiceTest.java` | Unit tests — balance correct after 0/1/multiple partial payments |

## Tasks

- [ ] Task: `Payment` model + `PaymentDao` (interface + `JdbcPaymentDao`: record payment, list by subscription, list by member)
  - Acceptance: recording a payment against an existing subscription persists correctly
  - Verify: manual smoke test (depends on M3's `subscriptions` table having at least seed rows)
  - Files: `payments/Payment.java`, `payments/PaymentDao.java`, `payments/JdbcPaymentDao.java`

- [ ] Task: `PaymentService` — compute outstanding balance for a subscription (sum of payments vs. `final_price`) + unit tests
  - Acceptance: outstanding balance is correct after 0, 1, and multiple partial payments (test cases for each)
  - Verify: `mvn test`
  - Files: `payments/PaymentService.java`, `src/test/java/com/gym/payments/PaymentServiceTest.java`

- [ ] Task: `PaymentPanel` (record a payment, view payment history + outstanding balance for a subscription)
  - Acceptance: works end-to-end against real MySQL
  - Verify: manual run
  - Files: `payments/PaymentPanel.java`

## Dependencies
- **Needs:** `schema.sql` merged (Foundation); reads `membership.Subscription` (M3) — build against seeded subscription rows if M3 isn't finished yet.
- **Needed by:** M6 (dashboard wiring).

## Checkpoint B contribution
Demo recording a payment + viewing outstanding balance standalone against real MySQL; `PaymentServiceTest` passes.
