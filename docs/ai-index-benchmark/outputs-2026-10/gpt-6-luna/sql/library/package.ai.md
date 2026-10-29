### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T21:23:11Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages lending-library catalog and circulation data, including inventory, member borrowing, returns, overdue reporting, and loan auditing.

#### Purpose
- Define and evolve the PostgreSQL data model for authors, books, members, branches, physical copies, and loans.
- Support circulation operations and reporting, with seed data for development or testing.

#### Responsibilities
- **Core schema and migration:** Establish author, book, member, and loan records with keys and constraints; migrate from aggregate copy counts to branch-based physical `book_copy` inventory.
- **Circulation and audit:** Check out available books, record returns, calculate capped late fees, and audit checkout and return events.
- **Reporting:** Expose overdue loans, member loan activity, and frequently borrowed books.
- **Seed data:** Populate authors, books, members, and dated loans in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and circulation tables.
- `branch`, `book_copy` — branch locations and individually identified inventory with availability status.
- `loan_audit` — records checkout and return actions with timestamps.
- `fn_checkout_book` — checks member and availability conditions, creates a loan, and decrements free-copy inventory.
- `sp_return_book` — marks an open loan returned and restores a free copy.
- `fn_late_fee` — calculates overdue charges at 50 cents per day, with a zero minimum and 2000-cent cap.
- `trg_loan_audit_fn`, `trg_loan_audit` — write audit records for loan creation and transition to returned.
- `overdue_loans` — reports unreturned loans past due, including days overdue.
- `member_loan_stats` — summarizes total and open loans and most recent loan date per member.
- `popular_books` — materialized ranking of books loaned at least five times in the past 365 days.
- `04_migration_v2.sql` — adds branch and copy tracking, assigns members a home branch, and migrates existing copies to Main in Springfield.

#### Data flow
- Seed rows populate `author`, `book`, `member`, and `loan`, linked by their identifiers.
- `fn_checkout_book` checks member eligibility and book availability, updates free-copy inventory, and inserts a loan; the trigger records the checkout.
- `sp_return_book` finds an open loan, sets its return date, and restores inventory; the trigger records the return.
- Reporting views derive overdue and member activity from loans and associated member/book records; `popular_books` aggregates loan history.
- The migration creates a Main branch, generates `book_copy` rows from existing book copy counts, assigns unassigned members to branch ID `1`, then removes aggregate availability columns from `book`.

#### Dependencies
- `book.author_id` references `author`; `loan.book_id` and `loan.member_id` reference `book` and `member`.
- After migration, `book_copy` references `book` and `branch`; `loan.copy_id` references `book_copy`; `member.home_branch_id` references `branch`.
- Views use `loan`, `member`, `book`, and `author`; `idx_popular_books_id` uniquely indexes `popular_books.book_id`.
- Circulation routines use PostgreSQL built-ins including `CURRENT_DATE`, `now()`, `COALESCE`, `GREATEST`, and `LEAST`.

#### Cross-cutting
- Primary keys, uniqueness, foreign keys, and checks enforce identifier integrity, valid copy counts/status, and loan due dates after loan dates.
- `fn_checkout_book` and `sp_return_book` raise exceptions for blocked members, unavailable books, or invalid/already-returned loans.
- `trg_loan_audit` runs after loan insertion or updates to `returned_on`, once per row.
