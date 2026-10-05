# M2: Trainers

Folder: `src/main/java/com/gym/trainers/` · Owner: **Owner 2** (fill in name) · Phase 1 (parallel, after Foundation merges)

## Files

| File | Purpose |
|---|---|
| `trainers/Trainer.java` | Business entity — `id`, `fullName`, `specialty`, `phone` (distinct from `auth/TrainerStaff.java`, the login role) |
| `trainers/TrainerDao.java` | Interface — `findAll()`, `findById(long)`, `save`, `update`, `delete` |
| `trainers/JdbcTrainerDao.java` | `JdbcTemplate` implementation |
| `trainers/TrainerService.java` | Validates required fields (name) |
| `trainers/TrainerPanel.java` | Swing `JPanel` — CRUD table/form + "assign trainer to member" control (dropdown sourced from `members.MemberDao`) |
| `src/test/java/com/gym/trainers/TrainerServiceTest.java` | Unit tests — valid/invalid trainer save |

## Tasks

- [ ] Task: `Trainer` model + `TrainerDao` (interface + `JdbcTrainerDao`)
  - Acceptance: CRUD works against seeded data
  - Verify: manual smoke test
  - Files: `trainers/Trainer.java`, `trainers/TrainerDao.java`, `trainers/JdbcTrainerDao.java`

- [ ] Task: `TrainerService` + unit tests
  - Acceptance: basic validation (name required) covered by a passing/failing test pair
  - Verify: `mvn test`
  - Files: `trainers/TrainerService.java`, `src/test/java/com/gym/trainers/TrainerServiceTest.java`

- [ ] Task: `TrainerPanel` (CRUD + assign trainer to a member, dropdown pulling from `members.MemberDao`)
  - Acceptance: assigning a trainer to a member persists and shows on the member's profile
  - Verify: manual run
  - Files: `trainers/TrainerPanel.java`

## Dependencies
- **Needs:** `schema.sql` merged (Foundation). Reads from `members.MemberDao` for the assign-trainer dropdown — coordinate with M1's owner on the `MemberDao` method signature, or build against seed data if M1 isn't done yet.
- **Needed by:** M6 (dashboard wiring; Admin/Receptionist menus link here).

## Checkpoint B contribution
Demo `TrainerPanel` CRUD + assign-to-member standalone against real MySQL; `TrainerServiceTest` passes.
