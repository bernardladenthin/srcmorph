### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:39:40Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Tracks library catalog inventory, member borrowing, loan returns, overdue charges, and operational reporting across branches.

#### Purpose
- Defines the PostgreSQL data model and routines for managing authors, books, members, copies, and loans.
- Supplies borrowing reports, loan audit records, a branch-based inventory migration, and sample data.

#### Responsibilities
- **Catalog and lending schema:** Models authors, books, members, loans, constraints, and indexes; migration v2 replaces book-level copy counters with branch-assigned `book_copy` records.
- **Borrowing operations and audit:** Checks out available books to eligible members, processes returns, and records checkout and return events.
- **Fees and reporting:** Calculates capped overdue fees and exposes overdue-loan, member-activity, and popular-book reports.
- **Bootstrap data:** Seeds authors, books, members, and loan history in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, inventory, and borrowing records.
- `branch`, `book_copy` — branch locations and individually identified copies with availability status.
- `loan_audit` — records checkout and return events by loan.
- `overdue_loans` — reports unreturned loans past their due date with member and book details.
- `member_loan_stats` — summarizes each member’s total and open loans and latest loan date.
- `popular_books` — materialized report of books loaned at least five times in the past 365 days.
- `fn_checkout_book` — validates member and availability, creates a loan, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores copy availability.
- `fn_late_fee` — computes overdue charges at 50 cents per day, floored at zero and capped at 2000 cents.
- `trg_loan_audit_fn`, `trg_loan_audit` — insert audit entries after checkout and when a loan is returned.

#### Data flow
- Seed inserts populate `author`, `book`, `member`, and `loan` records.
- `fn_checkout_book` checks member status and book availability, updates inventory, then inserts a loan; the audit trigger records the checkout.
- `sp_return_book` updates an open loan and restores inventory; the trigger records the return transition.
- Views and `popular_books` derive reports from `loan`, `member`, `book`, and `author`.
- Migration v2 creates the main branch, converts existing book totals into `book_copy` rows, assigns members to the branch, adds `loan.copy_id`, and removes book copy counters.

#### Dependencies
- `book.author_id` references `author`; `loan.book_id` and `loan.member_id` reference `book` and `member`.
- `book_copy` references `book` and `branch`; `loan.copy_id` references `book_copy`; `member.home_branch_id` references `branch`.
- Reporting objects read from `loan`, `member`, `book`, and `author`; routines operate on `member`, `book`, `loan`, and `loan_audit`.
- PostgreSQL built-ins include `CURRENT_DATE`, `INTERVAL`, `generate_series`, and date/value functions used by the routines.

#### Cross-cutting
- Constraints enforce identifiers, required values, valid dates, inventory limits, and allowed copy and audit statuses.
- `fn_checkout_book` and `sp_return_book` raise exceptions for blocked or ineligible operations; audit events are trigger-driven.
- Initial inventory is represented by `book.copies_total` and `book.copies_free`; migration v2 replaces those counters with branch-specific `book_copy` records.
