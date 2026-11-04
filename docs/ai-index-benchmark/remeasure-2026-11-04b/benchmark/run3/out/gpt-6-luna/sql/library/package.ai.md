### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:08:22Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Supports library cataloguing and circulation by managing books, members, loans, returns, inventory, and borrowing reports.

#### Purpose
- Defines a PostgreSQL lending-library database, its operational checkout and return logic, and reporting views.
- Includes sample data and a migration from book-level copy counts to branch-based, individually tracked copies.

#### Responsibilities
- **Core catalog and circulation schema:** `author`, `book`, `member`, and `loan` tables with relationships, constraints, and indexes.
- **Borrowing reports:** overdue loans, member loan activity, and popular books through views and a materialized view.
- **Loan operations and audit:** checkout, return, late-fee calculation, and trigger-based checkout/return audit records.
- **Inventory migration:** introduces `branch` and `book_copy`, migrates existing copy counts and member branch assignments, and removes book-level inventory counters.
- **Development and test fixtures:** seeds authors, books, members, and loans in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, inventory-count, and borrowing records.
- `overdue_loans` — lists open loans past their due dates with member contact details and days overdue.
- `member_loan_stats` — aggregates total and open loans and most recent loan date per member.
- `popular_books` — materialized report of books loaned at least five times in the past year.
- `fn_checkout_book` — checks member eligibility and book availability, creates a loan, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores its book’s free-copy count.
- `fn_late_fee` — calculates overdue charges at 50 cents per day, capped at 2000 cents.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — store and record checkout and return events.
- `branch`, `book_copy` — represent library locations and individually barcoded copies with lending status.

#### Data flow
- `fn_checkout_book` validates the member and book, decrements available inventory, inserts a loan, and triggers a checkout audit record.
- `sp_return_book` updates an unreturned loan and restores inventory; the loan trigger records the return.
- `fn_late_fee` derives a charge from due and return dates, using the current date for an unreturned loan.
- Reporting views aggregate `loan`, `member`, `book`, and `author` records into overdue, member-activity, and popular-book outputs.
- The migration creates a main branch, converts existing book counts into `book_copy` rows, assigns unassigned members to the branch, and removes `book.copies_free` and `book.copies_total`.

#### Dependencies
- Internal relations: `book` references `author`; `loan` references `book` and `member`; migrated `book_copy` references `book` and `branch`; `member.home_branch_id` references `branch`.
- Views read from `loan`, `member`, `book`, and `author`; routines read and update `member`, `book`, and `loan`, and write `loan_audit`.
- PostgreSQL built-ins include `CURRENT_DATE`, `now()`, and `generate_series`.

#### Cross-cutting
- PostgreSQL constraints enforce identifiers, uniqueness, inventory bounds, valid loan dates, and allowed copy and audit statuses.
- Checkout rejects blocked members or unavailable copies; return rejects unknown or already-returned loans.
- Loan audit is handled by a row-level AFTER trigger on loan insertion and return-date updates.
