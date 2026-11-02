### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:01:53Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalog, member borrowing, inventory, returns, overdue reporting, and loan auditing.

#### Purpose
- Defines and populates a PostgreSQL library database, with schema objects for authors, books, members, loans, and inventory.
- Supports checkout and return workflows, operational reporting, and migration from book-level counts to branch-based copy records.

#### Responsibilities
- **Catalog and lending schema:** `author`, `book`, `member`, and `loan` tables, with relationships, integrity checks, and indexes for author, member, and open-loan lookups.
- **Reports:** `overdue_loans` identifies overdue unreturned loans; `member_loan_stats` summarizes borrowing; `popular_books` materializes books loaned at least five times in the past year.
- **Checkout, returns, and audit:** `fn_checkout_book` validates membership and availability, creates loans, and updates inventory; `sp_return_book` records returns and restores inventory; `trg_loan_audit` records loan lifecycle events in `loan_audit`.
- **Fees:** `fn_late_fee` calculates a nonnegative overdue charge, capped at 2000.
- **Inventory migration:** `04_migration_v2.sql` creates `branch` and `book_copy`, assigns generated copies to the main branch, links members and loans to branches and copies, and removes book-level copy counters.
- **Development/test data:** `05_seed_data.sql` transactionally loads authors, books, members, and loans.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and lending records.
- `overdue_loans`, `member_loan_stats`, `popular_books` — overdue, member activity, and popular-title reports.
- `fn_checkout_book`, `sp_return_book` — checkout and return operations.
- `fn_late_fee` — capped late-fee calculation.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — audit storage and automatic checkout/return event recording.
- `branch`, `book_copy` — branch locations and individual copy inventory introduced by the migration.

#### Data flow
- Checkout checks member status and book availability, creates a loan with a due date, updates available-copy inventory, and triggers a `CHECKOUT` audit record.
- Return marks an open loan returned, restores inventory, and triggers a `RETURN` audit record when `returned_on` changes from NULL to non-NULL.
- Reporting views derive overdue loans and member activity from loan records; `popular_books` aggregates past-year loan counts and is indexed by book ID.
- The migration generates individual `book_copy` rows from existing book totals, assigns them to the main branch, and associates existing members with that branch.

#### Dependencies
- Reporting and routines use `loan`, `member`, `book`, and `author`; checkout and return also update `book` inventory.
- `loan_audit` records events for `loan`; its `loan_id` is not declared as a foreign key.
- The migration alters `book`, `loan`, and `member`, and uses PostgreSQL `generate_series()` to create copy records.

#### Cross-cutting
- PostgreSQL tables enforce primary keys, foreign keys, uniqueness, and checks for inventory counts, loan dates, and copy status.
- Checkout rejects blocked members and unavailable copies; return rejects unknown or already-returned loans.
- The initial schema tracks availability with `book.copies_total` and `book.copies_free`; the migration replaces these counters with branch-based `book_copy` records.
