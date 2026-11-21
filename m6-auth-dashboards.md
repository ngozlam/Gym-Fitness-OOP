# M6: Auth & Role Dashboards

Folder: `src/main/java/com/gym/auth/` · Owner: **Owner 6 / cross-cutting pair** (fill in name) · Spans two phases: login/`Staff` hierarchy in **Phase 0** (alongside Foundation, blocks everyone), dashboard wiring in **Phase 2** (after M1-M5 panels exist).

> **Naming note:** the login-role class is `TrainerStaff`, not `Trainer` — `trainers/Trainer.java` (M2's business entity: specialty, phone) already uses that name in a different folder; importing both into the same file (e.g. `MainFrame.java`) would collide if they shared a name.

## Files

| File | Phase | Purpose |
|---|---|---|
| `auth/Staff.java` | 0 | Abstract base — `id`, `username`, `fullName`; abstract method each subclass overrides (e.g. `getAllowedModules()`) |
| `auth/Admin.java` | 0 | `extends Staff` — access to all modules |
| `auth/TrainerStaff.java` | 0 | `extends Staff` — access to assigned members + attendance only |
| `auth/Receptionist.java` | 0 | `extends Staff` — access to members/plans/payments/attendance, not trainer management |
| `auth/StaffDao.java` | 0 | Interface — `findByUsername(String)` |
| `auth/JdbcStaffDao.java` | 0 | `JdbcTemplate` impl — raw SQL, maps `role` column to the correct `Staff` subclass |
| `auth/AuthService.java` | 0 | `login(username, password)` — loads via `StaffDao`, checks password with `PasswordEncoder` |
| `auth/CurrentSession.java` | 0 | Holds the logged-in `Staff` instance for the app's lifetime |
| `auth/LoginFrame.java` | 0 | Swing `JFrame` — login form, calls `AuthService`, opens `MainFrame` on success |
| `auth/MainFrame.java` | 2 | `CardLayout` shell hosting all 5 feature panels, side menu built polymorphically from `CurrentSession`'s `Staff` subclass |
| `src/test/java/com/gym/auth/AuthServiceTest.java` | 0 | Unit test — correct/incorrect password, `StaffDao` mocked |

## Tasks — Phase 0 (blocks everyone, do alongside `foundation.md`)

- [ ] Task: `Staff` abstract class + `Admin`, `TrainerStaff`, `Receptionist` subclasses; `StaffDao` (interface + `JdbcStaffDao`) with `findByUsername`
  - Acceptance: unit test loads a seeded staff row and gets back the correct subclass based on `role`
  - Verify: `mvn test`
  - Files: `auth/Staff.java`, `auth/Admin.java`, `auth/TrainerStaff.java`, `auth/Receptionist.java`, `auth/StaffDao.java`, `auth/JdbcStaffDao.java`

- [ ] Task: `AuthService` + `CurrentSession` — verify username/password against `staff` table using `BCryptPasswordEncoder`, hold the logged-in `Staff` for the app's lifetime; `LoginFrame` (basic Swing form, no styling needed yet)
  - Acceptance: logging in with a seeded admin user succeeds and is readable from `CurrentSession`; wrong password shows an error, does not crash
  - Verify: manual run + `mvn test` (mocked `StaffDao`, correct/incorrect password)
  - Files: `auth/AuthService.java`, `auth/CurrentSession.java`, `auth/LoginFrame.java`, `src/test/java/com/gym/auth/AuthServiceTest.java`

## Tasks — Phase 2 (after M1-M5 panels exist)

- [ ] Task: `MainFrame` with `CardLayout` hosting all 5 feature panels + navigation menu/sidebar
  - Acceptance: can switch between all 5 panels from one window, no separate windows popping up
  - Verify: manual run
  - Files: `auth/MainFrame.java`

- [ ] Task: Role-based menu visibility — `Admin` sees everything; `Receptionist` sees Members/Plans/Payments/Attendance (no Trainer management); `TrainerStaff` sees their assigned members + Attendance only (read-mostly)
  - Acceptance: logging in as each of the 3 seeded roles shows a visibly different menu
  - Verify: manual run, once per role
  - Files: `auth/MainFrame.java` (menu-building logic branches on `Staff` subclass — this is the polymorphism payoff)

## Dependencies
- **Phase 0 part needs:** `schema.sql`'s `staff` table (Foundation) — build in lockstep with `foundation.md`.
- **Phase 2 part needs:** M1-M5 panels to exist (even partially) before wiring `MainFrame`.
- **Needed by:** everyone — this is the entry point of the whole app.

## Checkpoints
- **Checkpoint A:** Phase 0 part done — login works, `AuthServiceTest` passes.
- **Checkpoint C:** Phase 2 part done — all 3 roles log in and see correct, working dashboards wired to all 5 panels.
