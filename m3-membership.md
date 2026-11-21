# M3: Membership Plans & Subscriptions

Folder: `src/main/java/com/gym/membership/` (+ `membership/pricing/`) · Owner: **Owner 3** (fill in name) · Phase 1 (parallel, after Foundation merges)

## Files

| File | Purpose |
|---|---|
| `membership/MembershipPlan.java` | Model — `id`, `name`, `durationMonths`, `basePrice`, `pricingStrategy` |
| `membership/Subscription.java` | Model — `id`, `memberId`, `planId`, `startDate`, `endDate`, `status`, `finalPrice` |
| `membership/PlanDao.java` / `membership/JdbcPlanDao.java` | CRUD for `membership_plans` |
| `membership/SubscriptionDao.java` / `membership/JdbcSubscriptionDao.java` | CRUD + `findByMemberId`, `findActiveByMemberId` for `subscriptions` |
| `membership/pricing/PricingStrategy.java` | Interface — `BigDecimal computePrice(MembershipPlan plan, Member member)` |
| `membership/pricing/StandardPricing.java` | Returns `plan.basePrice()` unchanged |
| `membership/pricing/StudentDiscountPricing.java` | Applies a fixed discount percentage to `basePrice` |
| `membership/pricing/PromotionalPricing.java` | Applies a promo rule (flat amount off, or first-month-free) |
| `membership/SubscriptionService.java` | `enroll(member, plan)` — picks `PricingStrategy` from `plan.pricingStrategy`, computes `finalPrice`, sets `endDate`; also `renew(subscription)`, `cancel(subscription)` |
| `membership/PlanPanel.java` | Swing `JPanel` — CRUD for plans, pricing strategy selectable |
| `membership/SubscriptionPanel.java` | Swing `JPanel` — enroll/renew/cancel a member's subscription, shows computed price |
| `src/test/java/com/gym/membership/SubscriptionServiceTest.java` | Unit tests — same plan produces different prices under different strategies; enroll/cancel happy paths |

## Tasks

- [ ] Task: `MembershipPlan` model + `PlanDao`; `Subscription` model + `SubscriptionDao`
  - Acceptance: CRUD works against seeded plans; subscriptions link member + plan with start/end dates
  - Verify: manual smoke test
  - Files: `membership/MembershipPlan.java`, `membership/Subscription.java`, `membership/PlanDao.java`, `membership/JdbcPlanDao.java`, `membership/SubscriptionDao.java`, `membership/JdbcSubscriptionDao.java`

- [ ] Task: `PricingStrategy` interface + `StandardPricing`, `StudentDiscountPricing`, `PromotionalPricing` implementations
  - Acceptance: unit test shows the same plan produces different final prices under different strategies
  - Verify: `mvn test`
  - Files: `membership/pricing/PricingStrategy.java`, `membership/pricing/StandardPricing.java`, `membership/pricing/StudentDiscountPricing.java`, `membership/pricing/PromotionalPricing.java`

- [ ] Task: `SubscriptionService` — enroll a member (computes `final_price` via the chosen strategy, sets `end_date` from `duration_months`), renew, cancel
  - Acceptance: enrolling a member creates a subscription row with correct computed price and end date; unit tests cover at least enroll + cancel
  - Verify: `mvn test`
  - Files: `membership/SubscriptionService.java`, `src/test/java/com/gym/membership/SubscriptionServiceTest.java`

- [ ] Task: `PlanPanel` + `SubscriptionPanel` (manage plans; enroll/renew/cancel a member's subscription, pricing strategy selectable)
  - Acceptance: full enroll flow works end-to-end through the UI against real MySQL
  - Verify: manual run
  - Files: `membership/PlanPanel.java`, `membership/SubscriptionPanel.java`

## Dependencies
- **Needs:** `schema.sql` merged (Foundation); reads `members.Member` (M1) to enroll — can build against seed data if M1 isn't finished yet.
- **Needed by:** M4 (Payments reads `Subscription`), M6 (dashboard wiring).

## Checkpoint B contribution
Demo enroll/renew/cancel flow standalone against real MySQL, with at least `StandardPricing` + one discount strategy working; `SubscriptionServiceTest` passes.
