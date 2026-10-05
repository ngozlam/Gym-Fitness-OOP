# M5: Attendance

Folder: `src/main/java/com/gym/attendance/` · Owner: **Owner 5** (fill in name) · Phase 1 (parallel, after Foundation merges)

## Files

| File | Purpose |
|---|---|
| `attendance/AttendanceRecord.java` | Model — `id`, `memberId`, `checkIn`, `checkOut` (nullable) |
| `attendance/AttendanceDao.java` | Interface — `checkIn(memberId)`, `checkOut(recordId)`, `findOpenByMemberId(memberId)`, `findByMemberAndDateRange(...)` |
| `attendance/JdbcAttendanceDao.java` | `JdbcTemplate` implementation |
| `attendance/AttendanceService.java` | Rejects check-in if member already has an open (no-`checkOut`) record; `visitsThisMonth(memberId)` |
| `attendance/AttendancePanel.java` | Swing `JPanel` — check-in/check-out by member search, daily log view, visit count |
| `src/test/java/com/gym/attendance/AttendanceServiceTest.java` | Unit tests — double check-in rejected, happy-path check-in/out |

## Tasks

- [ ] Task: `AttendanceRecord` model + `AttendanceDao` (check-in, check-out, list by member/date range)
  - Acceptance: check-in creates a row with `check_out = NULL`; check-out fills it in
  - Verify: manual smoke test
  - Files: `attendance/AttendanceRecord.java`, `attendance/AttendanceDao.java`, `attendance/JdbcAttendanceDao.java`

- [ ] Task: `AttendanceService` — prevent double check-in (member already checked in with no check-out), compute visit count this month + unit tests
  - Acceptance: attempting to check in an already-checked-in member is rejected; test covers both the rejection and the happy path
  - Verify: `mvn test`
  - Files: `attendance/AttendanceService.java`, `src/test/java/com/gym/attendance/AttendanceServiceTest.java`

- [ ] Task: `AttendancePanel` (check-in/check-out by member search, daily log view, visit count)
  - Acceptance: works end-to-end against real MySQL
  - Verify: manual run
  - Files: `attendance/AttendancePanel.java`

## Dependencies
- **Needs:** `schema.sql` merged (Foundation); reads `members.Member` (M1) for check-in search — build against seed data if M1 isn't finished yet.
- **Needed by:** M6 (dashboard wiring; `TrainerStaff` role's dashboard is mostly this module + their assigned members).

## Checkpoint B contribution
Demo check-in/check-out + visit count standalone against real MySQL; `AttendanceServiceTest` passes.
