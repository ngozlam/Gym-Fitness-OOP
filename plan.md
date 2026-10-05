# Implementation Plan: Gym/Fitness Membership System

Source spec: `../SPEC.md` (approved 2026-10-04)

## Components & Dependencies

```
Foundation (sequential, 1-2 people, blocks everyone)
    │
    ├── schema.sql / data.sql (frozen contract once merged)
    ├── Maven project skeleton + Spring Boot bootstrap (Swing on EDT)
    ├── Staff abstract class + Admin/Trainer/Receptionist
    └── AuthService + StaffDao + LoginFrame (basic, unstyled)
    │
    ▼
Feature modules (parallel, 1 owner each)
    ├── M1 Members          ──┐
    ├── M2 Trainers           │ M3 depends on M1 (enroll a member) + its own Plan CRUD
    ├── M3 Plans & Subscriptions (needs M1, M2 data to test against, but can stub)
    ├── M4 Payments (needs M3's Subscription model to exist, even if empty)
    └── M5 Attendance (needs M1 Members)
    │
    ▼
M6 Auth & Role Dashboards (cross-cutting, wires M1-M5 panels into CardLayout per role)
    │
    ▼
Integration & Demo Prep (whole team)
```

**Hard sequential dependency:** nothing meaningful can start until the Foundation phase is merged — it defines the DB schema, the package skeleton, and the `Staff`/login model every other module builds on. Keep this phase short (target: 1-2 people, done fast) so the other 3-4 people aren't blocked long.

**Soft dependencies between feature modules:** M3 (Subscriptions) reads Member + Plan data, M4 (Payments) reads Subscription data, M5 (Attendance) reads Member data. In practice each owner can build their DAO/service/UI against **seed data** (`data.sql`) without waiting on a teammate's module to be "done" — just on the Foundation's schema being frozen.

## Per-Module Files

Each module is one folder under `src/main/java/com/gym/` (package-by-module, per `SPEC.md`). File-by-file breakdown + task checklist now lives in its own file, one per module, so each owner has a single self-contained doc:

| Module | File | Folder |
|---|---|---|
| Foundation (shared/root) | `foundation.md` | — (`pom.xml`, `GymApplication.java`, `config/`, `src/main/resources/`) |
| M1 — Members | `m1-members.md` | `members/` |
| M2 — Trainers | `m2-trainers.md` | `trainers/` |
| M3 — Membership Plans & Subscriptions | `m3-membership.md` | `membership/` (+ `pricing/`) |
| M4 — Payments | `m4-payments.md` | `payments/` |
| M5 — Attendance | `m5-attendance.md` | `attendance/` |
| M6 — Auth & Role Dashboards | `m6-auth-dashboards.md` | `auth/` |
| Phase 3 — Integration & Demo Prep | `integration.md` | — (whole team) |

## Implementation Order

1. **Phase 0 — Foundation** (sequential). Must finish and merge before Phase 1 starts.
2. **Phase 1 — Feature modules M1-M5** (parallel, one owner each).
3. **Phase 2 — M6 Auth & Role Dashboards** (starts once Foundation's login skeleton exists for the login screen itself, but the *routing to real panels* part waits until M1-M5 panels exist — so this owner builds the login screen early alongside Phase 1, then does the dashboard wiring last).
4. **Phase 3 — Integration & Demo Prep** (whole team): wire everything into one running app, run through Success Criteria checklist, rehearse demo.

## Risks & Mitigations

| Risk | Mitigation |
|---|---|
| Schema changes after Phase 1 starts break multiple people's DAOs | Freeze `schema.sql` at the end of Phase 0; any change after that goes through the "Ask first" boundary (team chat) before merging |
| Merge conflicts on shared files (`pom.xml`, main `GymApplication.java`, `schema.sql`) | Only the Foundation owner(s) touch these after Phase 0; feature owners add new files in their own package, not edit shared ones |
| One module runs long and blocks M6/Integration | M6's owner builds the login screen + a stub dashboard early (Phase 1, in parallel), so Phase 2 isn't fully blocked; Integration phase has slack time built in by starting it as soon as 3+ modules are ready rather than waiting for all 5 |
| DAO boilerplate repetition across 5 owners (each writing similar `findById`/`findAll`/`save`) | Accepted as-is for this learning project — the point is practicing hand-written JDBC/SQL. Do not introduce a generic base DAO; keep each DAO explicit and simple |
| Someone doesn't know Swing/Spring well enough to start immediately | `contact-manager` in `D:/Spring Swing CRUD/` is a working reference for the Spring Boot + Swing + JDBC wiring pattern — point the team there first |

## Parallel vs. Sequential

- **Sequential, blocking:** Phase 0 (Foundation).
- **Parallel:** M1, M2, M3, M4, M5 (Phase 1) — 5 independent task tracks after Phase 0 merges.
- **Partially parallel:** M6's login UI can be built during Phase 1; M6's dashboard-wiring is sequential after Phase 1.
- **Sequential, whole-team:** Phase 3 (Integration).

## Verification Checkpoints

- **Checkpoint A (end of Phase 0):** `mvn clean package` succeeds; app boots to a blank Swing window; `schema.sql` applies cleanly to a fresh MySQL database; all 5 teammates have pulled and can build Phase 0 on their own machine.
- **Checkpoint B (mid Phase 1):** each module owner demos their panel's CRUD standalone (own branch, own machine) against real MySQL data; at least one JUnit test passes per module's service class.
- **Checkpoint C (Phase 2 done):** login screen authenticates against `staff` table (real bcrypt-hashed password); each of the 3 roles sees its dashboard; all 5 feature panels are reachable via `CardLayout` from at least one role.
- **Checkpoint D (Phase 3 / final):** every item in SPEC.md's Success Criteria checked off; fresh clone + fresh MySQL + `mvn spring-boot:run` works end-to-end on a machine that didn't have the project before (the real test of "no missing setup steps").

See the per-module files listed above (`foundation.md`, `m1-members.md` … `m6-auth-dashboards.md`, `integration.md`) for the discrete task breakdown per phase/module.
