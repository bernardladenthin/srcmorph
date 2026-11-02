### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:51:47Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalog, branch-level copy inventory, memberships, borrowing and returns, operational reporting, late fees, and loan auditing.

#### Purpose
- Define and evolve the library database, populate it with sample records, and support lending workflows.
- Provide reports on overdue loans, member activity, and frequently borrowed books.

#### Responsibilities
- **Catalog and circulation schema:** Store authors, books, members, and loans with key relationships and integrity constraints; index author and loan lookups.
- **Branch and copy migration:** Add branches and individually identified book copies, migrate existing book counts into copy records, connect loans to copies and members to home branches, and rename the member join-date column.
- **Checkout and return operations:** Validate checkout eligibility and availability, create loans with due dates, process returns, and update copy availability.
- **Fees and audit:** Calculate capped overdue fees and record checkout and first-return events.
- **Reporting:** Expose overdue-loan details and member loan statistics; materialize counts for books borrowed at least five times in the past year.
- **Sample data:** Insert authors, books, members, and loan history in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and circulation tables.
- `branch`, `book_copy` — branch locations and individually barcoded copies with `AVAILABLE`, `ON_LOAN`, or `LOST` status.
- `fn_checkout_book` — checks member status and book availability, inserts a loan, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores copy availability.
- `fn_late_fee` — calculates overdue charges at 50 cents per day, capped at 2000.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — store and generate checkout and return audit events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — report overdue borrowing, member activity, and frequently loaned books.
- `idx_popular_books_id` — unique index on the materialized `popular_books` view.

#### Data flow
- Seed inserts populate `author`, `book`, `member`, and `loan`.
- The migration creates a default `Main` branch, expands each book’s existing total into `book_copy` records, assigns members without a home branch to branch 1, and adds loan-to-copy references.
- Checkout checks member eligibility and available inventory, writes a loan, and triggers a `CHECKOUT` audit event; return updates the loan and inventory, with a `RETURN` audit event on the first transition from unreturned to returned.
- Reporting views aggregate the core loan, member, book, and author records; `popular_books` considers the prior 365 days and requires at least five loans.

#### Dependencies
- `book` references `author`; `loan` references `book` and `member` (member deletion cascades to loans).
- `book_copy` references `book` and `branch`; `loan.copy_id` references `book_copy`; `member.home_branch_id` references `branch`.
- Reporting joins `loan`, `member`, `book`, and `author`; the migration uses PostgreSQL `generate_series`.
- Checkout, returns, and auditing collaborate across `member`, `book`, `loan`, and `loan_audit`.

#### Cross-cutting
- PostgreSQL DDL, DML, views, materialized views, indexes, PL/pgSQL routines, and triggers.
- Constraints enforce unique ISBNs, emails, branch names and barcodes; valid copy counts and statuses; and due dates after loan dates.
- The initial schema uses book-level `copies_total` and `copies_free`; the migration replaces those counters with branch-specific `book_copy` records.
