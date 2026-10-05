# M1: Members

Folder: `src/main/java/com/gym/members/` · Owner: **Owner 1** (fill in name) · Phase 1 (parallel, after Foundation merges)

## Files

| File | Purpose |
|---|---|
| `members/Member.java` | Model — `id`, `fullName`, `email`, `phone`, `dateOfBirth`, `joinedDate`, `trainerId` |
| `members/MemberDao.java` | Interface — `findAll()`, `findById(long)`, `search(String)`, `save(Member)`, `update(Member)`, `delete(long)` |
| `members/JdbcMemberDao.java` | `JdbcTemplate` implementation — hand-written SQL for each method above |
| `members/MemberService.java` | Validates required fields + unique email before delegating to `MemberDao` |
| `members/MemberPanel.java` | Swing `JPanel` — table of members, add/edit/delete form, search box |
| `src/test/java/com/gym/members/MemberServiceTest.java` | Unit tests — duplicate email rejected, valid member saved, `MemberDao` mocked |

## Tasks

- [ ] Task: `Member` model + `MemberDao` (interface + `JdbcMemberDao`: `findAll`, `findById`, `save`, `update`, `delete`, `search(nameOrEmail)`)
  - Acceptance: DAO methods work against seeded data
  - Verify: manual run / simple main-method smoke test
  - Files: `members/Member.java`, `members/MemberDao.java`, `members/JdbcMemberDao.java`

- [ ] Task: `MemberService` (validation: required fields, unique email) + unit tests (DAO mocked)
  - Acceptance: saving a member with a duplicate email is rejected with a clear error
  - Verify: `mvn test`
  - Files: `members/MemberService.java`, `src/test/java/com/gym/members/MemberServiceTest.java`

- [ ] Task: `MemberPanel` (Swing `JPanel`: table of members + add/edit/delete/search form)
  - Acceptance: can add, edit, delete, and search a member through the UI against real MySQL
  - Verify: manual run
  - Files: `members/MemberPanel.java`

## Dependencies
- **Needs:** `schema.sql` merged (Foundation). Nothing else — this is the module with no upstream module dependency, safe to start first in Phase 1.
- **Needed by:** M2 (assign trainer), M3 (enroll member), M5 (attendance), M6 (dashboard wiring).

## Checkpoint B contribution
Demo `MemberPanel` CRUD standalone against real MySQL; `MemberServiceTest` passes.
