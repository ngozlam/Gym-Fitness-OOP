# Foundation (shared/root — Phase 0, sequential, blocks everyone)

Owner: 1-2 people. Not one of the 6 feature modules — these are the shared root files that every module depends on. Touched only here; feature owners don't edit these afterward (see Boundaries in `../SPEC.md`).

## Files

| File | Purpose |
|---|---|
| `pom.xml` | Maven build file — Spring Boot parent, Java 21, `spring-boot-starter`, `spring-boot-starter-jdbc`, `mysql-connector-j`, `spring-security-crypto`, JUnit 5 + Mockito |
| `.gitignore` | Excludes `target/`, local `application.properties` overrides, IDE files |
| `src/main/java/com/gym/GymApplication.java` | `main()` — boots the Spring `ApplicationContext`, then launches the Swing UI on the EDT |
| `src/main/java/com/gym/config/AppConfig.java` | `@Configuration` — declares the `PasswordEncoder` (`BCryptPasswordEncoder`) bean |
| `src/main/resources/application.properties` | Datasource URL/username/password (via env vars, not committed plaintext) |
| `src/main/resources/schema.sql` | `CREATE TABLE` for all 6 tables, in FK-safe order: `staff`, `trainers`, `members`, `membership_plans`, `subscriptions`, `payments`, `attendance` |
| `src/main/resources/data.sql` | Seed rows: 1 admin + 1 trainer + 1 receptionist in `staff` (bcrypt-hashed), 2-3 trainers, members, plans |

## Tasks

- [ ] Task: Create Maven project skeleton (Spring Boot parent, Java 21, `spring-boot-starter`, `spring-boot-starter-jdbc`, `mysql-connector-j`, `spring-security-crypto`, JUnit 5 + Mockito test deps)
  - Acceptance: `mvn clean package` succeeds with no source files beyond a placeholder `GymApplication` class
  - Verify: `mvn clean package`
  - Files: `pom.xml`

- [ ] Task: `GymApplication` boots Spring context then shows Swing UI on the EDT (no embedded web server)
  - Acceptance: running `mvn spring-boot:run` opens a blank `JFrame` titled "Gym Membership System"; closing it shuts down cleanly
  - Verify: manual run
  - Files: `src/main/java/com/gym/GymApplication.java`

- [ ] Task: Write `schema.sql` for all 6 tables in correct FK dependency order
  - Acceptance: `mysql -u root -p gym_db < src/main/resources/schema.sql` runs with no errors on a fresh database
  - Verify: manual run against local MySQL
  - Files: `src/main/resources/schema.sql`

- [ ] Task: Write `data.sql` seed data — at least 1 admin, 1 trainer, 1 receptionist in `staff`; 2-3 sample trainers, members, and plans
  - Acceptance: after seeding, `SELECT * FROM staff` shows 3 rows with bcrypt-looking password hashes (not plaintext)
  - Verify: manual query
  - Files: `src/main/resources/data.sql`

- [ ] Task: Configure `application.properties` for the MySQL datasource (via env vars or a gitignored local override, not committed plaintext)
  - Acceptance: app connects to local MySQL on startup with no credential committed to git
  - Verify: `git diff` shows no password; app logs successful datasource init
  - Files: `src/main/resources/application.properties`, `.gitignore`

## Checkpoint A (end of this file's work)
All 5 teammates pull this branch, run `mvn clean package` and `mvn spring-boot:run` successfully on their own machine before Phase 1 (M1-M5) starts. See also `m6-auth-dashboards.md`, whose Phase 0 section (`Staff` hierarchy, login) runs alongside this and is also a Checkpoint A blocker.
