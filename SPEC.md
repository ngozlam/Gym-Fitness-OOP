# Spec: Gym/Fitness Membership System

## Status: APPROVED (2026-10-04) — proceeding with recommended defaults on open questions 2 & 3; questions 1 & 4 still need a team decision (see below)

## Context
- Course project for practicing OOP (inheritance, polymorphism, encapsulation, abstraction, and design patterns).
- Built by a team of 5 — the module breakdown below is sized so each person can own one module end-to-end (model + DAO + service + Swing UI for that module).
- Stack requested: Spring (for DI/config), Swing (desktop UI), MySQL (database), with hand-written SQL via Spring's JDBC support (`JdbcTemplate`) rather than an ORM — this is deliberate, so the team practices writing and understanding SQL directly.

## Assumptions
(Correct any of these now — the spec below is written assuming they hold.)

1. **Spring Boot**, not plain Spring Framework — less XML/boilerplate, matches your prior `contact-manager` project's convention (Spring Boot 4.x parent POM, Maven). Java version bumped to **21** per your call (see below).
2. **Desktop app, not a web app.** Spring Boot is used purely as a DI container + JDBC config; the `main()` method boots the `ApplicationContext` then shows a Swing `JFrame`. No embedded web server, no REST controllers.
3. **Plain JDBC via `JdbcTemplate`** (or `NamedParameterJdbcTemplate`), not Spring Data JPA/Hibernate — SQL is written by hand in DAO classes. This matches your explicit ask to "write the query using MySQL and use JDBC in Spring."
4. **MySQL 8.x**, running locally, one schema (e.g. `gym_db`), created via a single `schema.sql` checked into the repo (run manually once, or via Spring Boot's `schema.sql`/`data.sql` auto-init on startup in dev).
5. **Java 21** (LTS), **Maven** (not Gradle) — build tool consistent with your existing Spring Swing CRUD project.
6. **No separate frontend/backend split** — this is a single Maven module (one `pom.xml`), not a multi-service architecture. The "5 modules" below are *packages/feature-areas* within one app, not 5 deployable services.
7. Project will live in its own Git repository (recommended: a new GitHub repo for the 5-person team), separate from the `D:/` personal workspace repo. **Open question below** — needs a decision before anyone pushes code.

## Objective
Build a desktop Gym/Fitness Membership Management System that lets gym staff:
- Register and manage members and trainers.
- Define membership plans (e.g. Monthly/Quarterly/Annual, Student discount, Promotional) and enroll members into subscriptions.
- Record payments against subscriptions.
- Track daily attendance (check-in/check-out).
- Log in with a role (Admin, Trainer, Receptionist) and see a dashboard scoped to that role.

**Success looks like:** a runnable Swing application, backed by MySQL, where each of the 5 feature modules has working CRUD + at least one piece of real business logic (not just data entry), and the codebase visibly demonstrates the OOP concepts listed per-module below.

**Users:** gym front-desk staff (Receptionist), trainers, and an admin — played by the 5 team members during a demo.

## Tech Stack
- Java 21
- Spring Boot 3.x/4.x — `spring-boot-starter` (core/DI) + `spring-boot-starter-jdbc`
- MySQL 8.x + `mysql-connector-j` driver
- Java Swing (`javax.swing`) for UI — plain Swing, no third-party look-and-feel library unless the team wants to add one later (open question)
- Maven (project build + dependency management)
- JUnit 5 + Mockito for unit tests

## Feature Modules (one owner each, 5 team members)

| # | Module | Responsibility | Primary OOP focus |
|---|---|---|---|
| 1 | **Members** | Member CRUD, search/filter, member profile | Encapsulation (validated fields: email, phone), basic composition (Member has a current Subscription) |
| 2 | **Trainers** | Trainer CRUD, assign trainer to member | Composition/association (Trainer ↔ Members) |
| 3 | **Membership Plans & Subscriptions** | Define plans, enroll/renew/cancel a member's subscription, compute price | **Strategy pattern** — a `PricingStrategy` interface with `StandardPricing`, `StudentDiscountPricing`, `PromotionalPricing` implementations |
| 4 | **Payments** | Record payments against a subscription, payment history, outstanding balance | Encapsulation of money logic; simple validation rules |
| 5 | **Attendance** | Check-in/check-out, daily attendance log, basic stats (visits this month) | Simple aggregation logic |
| 6 (cross-cutting) | **Auth & Role Dashboards** | Login screen, session/current-user holder, role-specific main dashboard | **Inheritance/polymorphism** — abstract `Staff` class → `Admin`, `Trainer`, `Receptionist` subclasses, each overriding what menu/dashboard they see |

> Module 6 is cross-cutting (touches every screen's entry point) — suggest the strongest Spring/Swing-wiring person on the team owns it, or it's split as pairs across two people alongside their primary module.

## Data Model (MySQL — `gym_db`)

```sql
CREATE TABLE staff (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN','TRAINER','RECEPTIONIST') NOT NULL
);

CREATE TABLE members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    joined_date DATE NOT NULL,
    trainer_id BIGINT,
    FOREIGN KEY (trainer_id) REFERENCES trainers(id)
);

CREATE TABLE trainers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100),
    phone VARCHAR(20)
);

CREATE TABLE membership_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    duration_months INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    pricing_strategy VARCHAR(30) NOT NULL DEFAULT 'STANDARD'
);

CREATE TABLE subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status ENUM('ACTIVE','EXPIRED','CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    final_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members(id),
    FOREIGN KEY (plan_id) REFERENCES membership_plans(id)
);

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscription_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATETIME NOT NULL,
    method ENUM('CASH','CARD','TRANSFER') NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id)
);

CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    check_in TIMESTAMP NOT NULL,
    check_out TIMESTAMP NULL,
    FOREIGN KEY (member_id) REFERENCES members(id)
);
```

Note: `trainers` must be created before `members` (FK order) — the final `schema.sql` will declare tables in dependency order.

## Commands
```
Build:   mvn clean package
Run:     mvn spring-boot:run
Test:    mvn test
Package: mvn clean package  →  target/gym-fitness-membership-system-<version>.jar
```
MySQL setup (once, locally, by each dev):
```
mysql -u root -p -e "CREATE DATABASE gym_db;"
mysql -u root -p gym_db < src/main/resources/schema.sql
```

## Project Structure
```
Gym-Fitness-Membership-System/
├── pom.xml
├── SPEC.md
├── src/
│   ├── main/
│   │   ├── java/com/gym/
│   │   │   ├── GymApplication.java        → main(), boots Spring context + Swing EDT (shared, root)
│   │   │   ├── config/                     → AppConfig — PasswordEncoder bean etc. (shared, root)
│   │   │   ├── auth/                       → Foundation + M6: Staff (abstract), Admin,
│   │   │   │                                 TrainerStaff, Receptionist, StaffDao, AuthService,
│   │   │   │                                 CurrentSession, LoginFrame, MainFrame
│   │   │   ├── members/                    → M1: Member, MemberDao(+Jdbc), MemberService, MemberPanel
│   │   │   ├── trainers/                   → M2: Trainer, TrainerDao(+Jdbc), TrainerService, TrainerPanel
│   │   │   ├── membership/                 → M3: MembershipPlan, Subscription, their DAOs,
│   │   │   │   └── pricing/                  SubscriptionService, PlanPanel, SubscriptionPanel,
│   │   │   │                                 and the PricingStrategy family
│   │   │   ├── payments/                   → M4: Payment, PaymentDao(+Jdbc), PaymentService, PaymentPanel
│   │   │   └── attendance/                 → M5: AttendanceRecord, AttendanceDao(+Jdbc),
│   │   │                                     AttendanceService, AttendancePanel
│   │   └── resources/
│   │       ├── application.properties     → datasource config
│   │       ├── schema.sql
│   │       └── data.sql                   → seed data (sample plans, an admin user)
│   └── test/java/com/gym/                 → unit tests, mirrors main package structure
└── docs/                                  → ER diagram, class diagram (add as the team designs them)
```

## Code Style
Example — a DAO method, showing the hand-written SQL + `JdbcTemplate` convention:
```java
@Repository
public class JdbcMemberDao implements MemberDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Member> findById(long id) {
        String sql = "SELECT id, full_name, email, phone, date_of_birth, joined_date, trainer_id " +
                     "FROM members WHERE id = ?";
        List<Member> results = jdbcTemplate.query(sql, this::mapRow, id);
        return results.stream().findFirst();
    }

    private Member mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Member(
            rs.getLong("id"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getDate("date_of_birth").toLocalDate(),
            rs.getDate("joined_date").toLocalDate(),
            rs.getObject("trainer_id", Long.class)
        );
    }
}
```
Conventions:
- **Package-by-module** (`members/`, `trainers/`, `membership/`, `payments/`, `attendance/`, `auth/`), not package-by-layer — each of the 5 owners works entirely inside their own folder (model + dao + service + ui together), which keeps git diffs and merge conflicts contained to that folder. Shared/root-level code (`GymApplication`, `config/`) stays minimal and is only touched during Phase 0.
- Within a module folder, still separate by role via file suffix, not sub-packages: `Member.java` (model), `MemberDao.java` / `JdbcMemberDao.java` (data access), `MemberService.java` (business logic), `MemberPanel.java` (Swing UI). `membership/` is the one exception — its `pricing/` sub-package holds the `PricingStrategy` family since it's four small files that belong together.
- DAOs are interfaces (`MemberDao`) + one Jdbc implementation (`JdbcMemberDao`) — this is the seam that makes services unit-testable via Mockito.
- No business logic in DAOs or UI classes — DAOs only run SQL, panel classes only build/wire Swing components and delegate to the module's service.
- Cross-module references are expected and fine (e.g. `membership.SubscriptionService` reads a `members.Member`) — module folders are ownership boundaries, not hard dependency walls.
- Swing: one top-level `JFrame` per role dashboard, `JPanel` per feature (e.g. `MemberPanel`), use `CardLayout` or separate `JFrame`s for navigation (team's choice — pick one convention in Plan phase).

## Testing Strategy
- **Unit tests (JUnit 5 + Mockito):** one test class per `*Service`, DAO mocked. This is where the Strategy-pattern pricing logic (module 3) and any validation rules get verified.
- **DAO tests:** run manually against a local MySQL instance during development (not part of the automated `mvn test` run, to avoid requiring MySQL in CI). If the team later wants automated DAO tests, Testcontainers-MySQL is the natural next step — call out as a stretch goal, not required for this project.
- **Manual QA:** each module owner demos their panel's CRUD + business rule against the real MySQL database before merging.
- Coverage target: service-layer logic (not UI, not boilerplate getters/setters) — aim for the core business rules in each module having at least one passing/failing-case test pair.

## Boundaries
- **Always:** validate input in the service layer (not just the UI) before hitting the DAO; use `PreparedStatement`-style parameterized queries (`?` placeholders) everywhere — never string-concatenate SQL; run `mvn test` before merging a module's branch.
- **Ask first (raise in team chat/standup):** changing the shared `schema.sql`, renaming a package other members depend on, adding a new Maven dependency, changing the Swing navigation convention after it's been agreed.
- **Never:** commit real passwords or a populated `application.properties` with personal DB credentials (use a `.gitignore`d local override or env vars); store plaintext passwords in the `staff` table (hash with something simple like BCrypt via `spring-security-crypto`, even without full Spring Security).

## Success Criteria
- [ ] App launches, shows a login screen, authenticates against the `staff` table, and routes to the correct role dashboard.
- [ ] All 5 feature modules support Create/Read/Update/Delete (or the subset that's domain-appropriate — e.g. attendance has check-in/check-out, not "update/delete" in the traditional sense) against real MySQL data.
- [ ] Subscription pricing is computed via the Strategy pattern (at least `StandardPricing` + one discount strategy implemented and demoed).
- [ ] Each module has at least one JUnit test covering its core business rule.
- [ ] No SQL is string-concatenated from user input (parameterized queries only).
- [ ] The app runs end-to-end on a teammate's machine following only the README setup steps (fresh MySQL + `mvn spring-boot:run`).

## Open Questions
1. **Repo hosting — still open, needs a team decision before anyone pushes code.** Recommended: a new GitHub repo for the 5-person team, separate from the personal `D:/` workspace repo (which is a single large git repo covering unrelated projects — nesting this inside it would be messy).
2. ~~Swing navigation style~~ — **Resolved: single `JFrame` + `CardLayout`** panel-switching. Simpler state management, no juggling multiple windows.
3. ~~Password hashing~~ — **Resolved: `spring-security-crypto`'s `BCryptPasswordEncoder`.** Lightweight, no full Spring Security needed.
4. **Team member → module assignment — still open, needs team input.** The Plan/Tasks below use placeholder owners (`Owner 1`–`Owner 5`); swap in real names before work starts.
5. Stretch goals (not required for success criteria): CSV export of attendance, a simple revenue dashboard for Admin, a `FlatLaf` look-and-feel. Scheduled last in the Plan — only pick these up if the 6 core modules are done early.
