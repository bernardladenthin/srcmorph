### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:45:17Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalog, members, book loans, inventory availability, borrowing reports, and loan audit history.

#### Purpose
- Defines the PostgreSQL data model and operations for lending books to members, processing returns, and tracking late fees.
- Supplies reporting views, a branch-and-copy inventory migration, and sample catalog and loan data.

#### Responsibilities
- **Catalog, membership, and loans:** `author`, `book`, `member`, and `loan` tables model library records, relationships, availability counters, and due dates.
- **Loan operations and audit:** checkout and return routines update loans and book availability; a trigger records checkout and return events in `loan_audit`; a function calculates capped late fees.
- **Borrowing reports:** views report overdue loans and member activity; materialized view `popular_books` identifies books with at least five loans in the last 365 days.
- **Inventory migration:** adds `branch` and `book_copy`, assigns existing copies to the main branch, adds loan-copy and member-home-branch references, and replaces book-level copy counters.
- **Sample data:** seeds authors, books, members, and loan history within a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, availability, and borrowing tables.
- `fn_checkout_book` — checks member eligibility and book availability, creates a loan, and decrements the available-copy count.
- `sp_return_book` — returns an eligible loan and restores book availability.
- `fn_late_fee` — computes a per-day late fee with a zero floor and 2000-cent cap.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — store and record checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — expose overdue, member activity, and recent book popularity reports.
- `branch`, `book_copy` — represent branch locations and individually identified copies in the migration schema.

#### Data flow
- `fn_checkout_book` validates a member and available book, decrements `book.copies_free`, inserts a `loan`, and returns its ID; the loan trigger records the checkout.
- `sp_return_book` marks an open loan returned and increments availability; the trigger records the return, while `fn_late_fee` can calculate the fee from due and return dates.
- Reporting views derive overdue and borrowing summaries from `loan`, `member`, `book`, and `author`; `popular_books` aggregates recent loans.
- The migration generates `book_copy` rows from existing book copy totals, assigns them to the main branch, and adds `loan.copy_id` and `member.home_branch_id`.

#### Dependencies
- SQL routines and reports depend on the `author`, `book`, `member`, and `loan` tables; the audit trigger writes to `loan_audit`.
- `book_copy` references `book` and `branch`; `loan.copy_id` references `book_copy`, and `member.home_branch_id` references `branch`.
- The migration uses PostgreSQL `generate_series`; routines use built-ins including `CURRENT_DATE`, `now()`, `COALESCE`, `GREATEST`, and `LEAST`.

#### Cross-cutting
- Primary keys and foreign keys link catalog, membership, loans, branches, and copies; constraints enforce unique identifiers, nonnegative availability, valid copy status, and due dates after loan dates.
- Open-loan and lookup indexes support loan and inventory access; `popular_books` has a unique index on `book_id`.
- Checkout and return reject blocked, unavailable, unknown, or already-returned cases with exceptions; seed inserts are grouped in a transaction.
