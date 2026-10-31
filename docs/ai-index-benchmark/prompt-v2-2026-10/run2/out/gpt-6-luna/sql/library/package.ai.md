### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:47:51Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalog, member borrowing, inventory, loan reporting, and checkout-to-return lifecycle.

#### Purpose
- Defines and populates the PostgreSQL library database for authors, books, members, loans, and inventory.
- Supports loan operations, audit tracking, overdue and borrowing reports, and migration to branch-based copy inventory.

#### Responsibilities
- **Core schema and sample data:** `author`, `book`, `member`, and `loan` tables, their constraints and indexes, and transactional seed records.
- **Borrowing reports:** Views for overdue loans and member activity, plus a materialized report of popular books.
- **Loan operations and audit:** Checkout, return, late-fee calculation, and auditing checkout and return events.
- **Branch inventory migration:** Adds branches and individually tracked book copies, backfills existing inventory, and updates member and loan relationships.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and borrowing tables.
- `overdue_loans` — lists unreturned loans past their due date with days overdue.
- `member_loan_stats` — summarizes total and open loans and last borrowing date per member.
- `popular_books` — materialized report of books loaned at least five times in the past year; uniquely indexed by `book_id`.
- `fn_checkout_book` — validates member status and copy availability, creates a loan, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores book availability.
- `fn_late_fee` — calculates a nonnegative per-day late fee capped at 2000 cents.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return events through a loan-table trigger.
- `branch`, `book_copy` — branch locations and individually tracked copies with availability, on-loan, or lost status.

#### Data flow
- Seed inserts populate authors and books, then members and loans against the core schema.
- Checkout checks member blocking and copy availability, adjusts availability, and inserts a loan; the trigger records a checkout audit event.
- Return updates an open loan and restores availability; the trigger records the return transition. Late fees can be calculated from due and return dates.
- Reporting views derive overdue and member activity from loan, member, and book records; `popular_books` aggregates recent loan activity.
- The migration creates a default branch, converts existing book copy counts into `book_copy` rows, assigns members to the branch when needed, and adds `loan.copy_id`.

#### Dependencies
- Views and routines depend on the core `loan`, `member`, `book`, and `author` tables; checkout and return also update book availability.
- `loan_audit` records corresponding loan IDs; `trg_loan_audit` invokes its trigger function on loan inserts and return-date updates.
- Migration references `book`, `loan`, and `member`, and uses PostgreSQL `generate_series` to create copy rows.
- Seed relationships follow `book.author_id -> author.author_id`, `loan.book_id -> book.book_id`, and `loan.member_id -> member.member_id`; the migration adds branch and copy foreign-key relationships.

#### Cross-cutting
- PostgreSQL DDL, DML, views, materialized view, and PL/pgSQL routines; seed data and migration use transactions.
- Constraints enforce unique identifiers, valid copy counts and statuses, and valid loan due dates.
- The migration replaces book-level copy counters with per-copy inventory; the initial schema, routines, and seed data describe the counter-based model.
