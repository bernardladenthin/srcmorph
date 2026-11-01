### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:23:50Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalog, branch-based copy inventory, memberships, borrowing and returns, reporting, and loan-event auditing.

#### Purpose
- Defines and evolves the PostgreSQL data model for authors, books, members, branches, physical copies, and loans.
- Supports sample-data loading, lending operations, overdue-fee calculation, audit logging, and library reports.

#### Responsibilities
- **Catalog and lending schema:** Create `author`, `book`, `member`, and `loan`, with keys, relationships, validation constraints, and lookup indexes.
- **Inventory migration:** Add `branch` and barcode-tracked `book_copy` records, associate members and loans with branches and copies, and replace book-level copy counters with per-copy status.
- **Checkout, returns, and fees:** Enforce member and availability checks, update inventory and loan records, and compute capped overdue fees.
- **Reporting:** Provide overdue-loan, member-activity, and popular-book views.
- **Sample data:** Seed authors, books, members, and loan history in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and borrowing tables.
- `branch`, `book_copy` — branch locations and individual copies with barcode and `AVAILABLE` / `ON_LOAN` / `LOST` status.
- `fn_checkout_book` — checks member eligibility and copy availability, creates a loan, and returns its ID.
- `sp_return_book` — marks an active loan returned and restores copy availability.
- `fn_late_fee` — calculates overdue charges at 50 cents per day, with a 2000-cent cap.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return events through a row-level trigger.
- `overdue_loans`, `member_loan_stats`, `popular_books` — expose overdue records, member borrowing aggregates, and books loaned at least five times in the past year.
- `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id`, `idx_book_copy_book` — support catalog, loan, report, and copy lookups.

#### Data flow
- The initial schema defines book-level copy counts; the migration creates a `Main` branch, generates `book_copy` rows from those counts, assigns members to the branch, adds `loan.copy_id`, and drops the old counters.
- `fn_checkout_book` decrements a free-copy count and inserts a loan; `sp_return_book` marks the loan returned and increments that count. The trigger records checkout and return events in `loan_audit`.
- Views read catalog, member, and loan records to produce overdue, member-activity, and popular-book reports.
- `05_seed_data.sql` inserts sample authors, books, members, and loans in one transaction.

#### Dependencies
- `book.author_id` references `author`; `loan` references `book` and `member`; the migration adds `book_copy.book_id -> book`, `book_copy.branch_id -> branch`, `loan.copy_id -> book_copy`, and `member.home_branch_id -> branch`.
- Routines use `member`, `book`, and `loan`; the audit trigger writes to `loan_audit`.
- Reports read from `loan`, `member`, `book`, and `author`; the migration uses PostgreSQL `generate_series`.

#### Cross-cutting
- PostgreSQL schema constraints enforce uniqueness, valid ranges and statuses, copy-count consistency in the initial schema, and loan due dates later than loan dates.
- Checkout and return raise exceptions for blocked members, unavailable copies, unknown loans, or loans already returned.
- The seed script uses an explicit transaction; the audit trigger runs after loan inserts and updates to `returned_on`.
