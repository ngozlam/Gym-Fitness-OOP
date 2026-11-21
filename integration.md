# Phase 3: Integration & Demo Prep (whole team)

No single owner — everyone participates once M1-M6 are done (Checkpoint C passed).

## Tasks

- [ ] Task: Fresh-machine test — one teammate who hasn't touched the code clones the repo, creates a fresh MySQL DB, follows only the README, and gets the app running
  - Acceptance: succeeds with zero help from the rest of the team
  - Verify: manual
  - Files: `README.md` (write/update setup steps if missing)

- [ ] Task: Walk the full Success Criteria checklist in `../SPEC.md` top to bottom, check off each item or file a follow-up task
  - Acceptance: every box checked, or a known gap is explicitly logged
  - Verify: manual
  - Files: `../SPEC.md`

- [ ] Task: Rehearse a demo script covering all 3 roles and all 5 modules
  - Acceptance: a dry run completes without crashes in under the allotted demo time
  - Verify: manual rehearsal
  - Files: none (process task)

## Checkpoint D (final)
Every item in `../SPEC.md`'s Success Criteria checked off; fresh clone + fresh MySQL + `mvn spring-boot:run` works end-to-end on a machine that didn't have the project before.

---

## Stretch Goals (only if Phase 3 finishes early)
- [ ] CSV export of attendance
- [ ] Simple revenue dashboard for Admin (sum of payments by month/plan)
- [ ] `FlatLaf` look-and-feel for nicer UI
