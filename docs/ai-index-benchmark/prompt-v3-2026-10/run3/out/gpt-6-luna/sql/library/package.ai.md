### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:38:44Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalog, member borrowing, loan lifecycle and fees, audit history, reporting, and branch-level copy inventory.

#### Purpose
- Defines and populates the lending-library database, with constraints for catalog records, members, loans, and copy availability.
- Supports checkout and return operations, operational reporting, loan auditing, and migration from book-level counts to branch-based inventory.

#### Responsibilities
- **Core records and seed data:** `author`, `book`, `member`, and `loan` tables hold catalog, account, and borrowing data; seed SQL loads authors, books, members, and loan histories.
- **Borrowing workflows and audit:** checkout validates member and availability, return updates loan and inventory, late fees are calculated, and triggers record checkout and return events in `loan_audit`.
- **Library reporting:** views expose overdue loans and member loan statistics; `popular_books` materializes books with at least five loans in the past year.
- **Branch inventory migration:** adds `branch` and `book_copy`, migrates existing book counts into copies at a main branch, links loans and members to copy and branch records, and removes book-level copy counters.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, borrower, and borrowing tables.
- `overdue_loans` — view of unreturned loans past their due dates, with member and book details.
- `member_loan_stats` — view of total and open loans and most recent loan date per member.
- `popular_books` — materialized view of books meeting the recent-loan threshold.
- `fn_checkout_book` — validates a member and available copy count, creates a loan, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores a free-copy count.
- `fn_late_fee` — calculates capped late fees based on overdue days.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — store and record checkout and return events.
- `branch`, `book_copy` — represent branch locations and individually identified copies.
- `04_migration_v2.sql` — migrates inventory and member/loan relationships to branch-aware copy tracking.

#### Data flow
- Seed SQL inserts authors and books, then members and loans into the core tables.
- Checkout checks the member and book’s free-copy count, decrements availability, and inserts a loan; the loan trigger records `CHECKOUT`.
- Return marks an open loan returned and increments the book’s free-copy count; the loan trigger records `RETURN`. Late-fee calculation uses the due date and either the return date or current date.
- Reporting views read loan, member, book, and author records; the migration creates branch and copy records from existing book counts and connects loans and members to the new inventory model.

#### Dependencies
- Views and routines depend on `loan`, `member`, `book`, and `author`; checkout and return routines update `book` and `loan`.
- `trg_loan_audit` observes `loan` inserts and changes to `returned_on`, writing to `loan_audit`.
- `book_copy` references `book` and `branch`; migrated `loan.copy_id` and `member.home_branch_id` reference `book_copy` and `branch`.
- PostgreSQL built-ins include `CURRENT_DATE`, `now()`, and `generate_series`.

#### Cross-cutting
- PostgreSQL schema constraints enforce key uniqueness, foreign-key relationships, valid copy counts, and due dates later than loan dates.
- Checkout and return routines raise exceptions for blocked members, unavailable copies, and unknown or already returned loans.
- `loan_audit` records checkout and return transitions; the migration is transaction-wrapped, as is the seed script.
