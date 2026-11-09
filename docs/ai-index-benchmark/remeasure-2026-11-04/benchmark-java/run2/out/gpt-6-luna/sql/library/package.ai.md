### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:31:57Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalogue, memberships, physical copy inventory, book loans, overdue reporting, and loan auditing.

#### Purpose
- Defines and populates a PostgreSQL lending-library database, with schema migration from book-level copy counts to branch-aware physical copies.
- Supports checkout and return operations, late-fee calculation, audit records, and borrowing reports.

#### Responsibilities
- **Catalogue and membership:** Store authors, books, members, and their relationships; enforce identifying fields and data constraints.
- **Loan operations and auditing:** Validate checkout eligibility and availability, process returns, update inventory, calculate late fees, and record checkout/return events.
- **Inventory migration:** Add branches and individually barcoded `book_copy` records; associate members with a home branch and loans with copies; retire book-level copy counters.
- **Reporting:** Provide overdue-loan and member-activity views, plus a materialized report of books loaned at least five times in the past year.
- **Sample data:** Seed authors, books, members, and historical loans in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalogue, membership, and lending tables.
- `branch`, `book_copy` — branch locations and physical copies with barcode and availability status.
- `loan_audit` — records checkout and return actions with timestamps.
- `fn_checkout_book(p_member_id, p_book_id, p_days)` — rejects blocked members or unavailable books, decrements available inventory, creates a loan, and returns its ID.
- `sp_return_book(p_loan_id)` — marks an open loan returned and restores inventory; errors for unknown or already-returned loans.
- `fn_late_fee(p_due_on, p_returned_on)` — calculates overdue fees at 50 cents per day, capped at 2000 cents.
- `trg_loan_audit_fn`, `trg_loan_audit` — create audit records for loan insertion and first transition to returned.
- `overdue_loans` — lists open overdue loans and calculates days overdue.
- `member_loan_stats` — summarizes total/open loans and most recent loan date per member.
- `popular_books` — materialized report of books with at least five loans in the past 365 days.

#### Data flow
- Seeded authors and books supply the catalogue; members and loans reference those records.
- Checkout checks member and inventory state, then inserts a loan; the audit trigger records checkout. Return updates the loan, restores inventory, and triggers a return audit record.
- Reporting views derive overdue and member activity from `loan`, `member`, and `book`; `popular_books` aggregates recent loan activity with book and author details.
- Migration creates a `Main` branch, generates per-book `book_copy` rows from existing copy counts, assigns members to that branch, adds loan-to-copy references, and removes book-level copy counters.

#### Dependencies
- Internal relationships connect `book` to `author`, `loan` to `book` and `member`, and migrated `book_copy` and `member` records to `branch`.
- Reporting reads `loan`, `member`, `book`, and `author`; routines operate on `member`, `book`, `loan`, and `loan_audit`.
- PostgreSQL built-ins include `CURRENT_DATE`, `now()`, and migration function `generate_series`.

#### Cross-cutting
- Primary keys, foreign keys, uniqueness, and checks constrain identifiers, copy status/counts, author birth years, and loan due dates.
- `member` deletion cascades to associated loans; `idx_loan_open` supports lookup of unreturned loans by due date.
- `popular_books` is materialized and has a unique index on `book_id`; `fn_late_fee` is immutable.
- Checkout and return inventory behavior is represented in the routines against book-level counters; the migration later removes those counters in favor of physical copies.
