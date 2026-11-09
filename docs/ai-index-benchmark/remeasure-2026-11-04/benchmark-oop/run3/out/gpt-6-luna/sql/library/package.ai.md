### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:53:53Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalog, members, branch-specific copies, checkout and return workflows, overdue reporting, and loan history.

#### Purpose
- Define and evolve PostgreSQL data structures for authors, books, members, loans, branches, and individual book copies.
- Support lending operations, audit records, fee calculation, reporting, and representative seed data.

#### Responsibilities
- **Catalog and lending schema:** `author`, `book`, `member`, and `loan` tables with identifiers, relationships, validation constraints, and indexes.
- **Inventory migration:** Add `branch` and `book_copy`, generate branch-assigned copies from legacy book-level counters, connect loans and members to copies and branches, and remove the counters.
- **Checkout, return, and auditing:** Validate checkout eligibility and availability, record loans and returns, restore availability, and audit checkout and return events.
- **Fees and reporting:** Calculate capped late fees; expose overdue loans, member borrowing statistics, and recently popular books.
- **Seed data:** Populate authors, books, members, and loan-history records in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, inventory-count, and borrowing records.
- `branch`, `book_copy` — branch locations and individually identified copies with availability status.
- `fn_checkout_book` — checks member and copy availability, creates a loan, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores book availability.
- `fn_late_fee` — calculates overdue charges in cents, with a maximum of 2,000.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and newly completed return events.
- `overdue_loans` — reports unreturned loans past their due dates and days overdue.
- `member_loan_stats` — aggregates total and open loans and the latest loan date per member.
- `popular_books` — materialized report of books loaned at least five times in the past 365 days.
- `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id`, `idx_book_copy_book` — indexes supporting relationships and report or inventory lookups.

#### Data flow
- The schema establishes authors, books, members, and loans; seed inserts populate these entities and their borrowing history.
- Checkout validates a member and available book copy, decrements the legacy `book.copies_free` counter, and inserts a loan; the loan trigger records checkout.
- Return marks an open loan returned, increments the associated book’s free-copy count, and triggers a return audit entry.
- The migration creates a main branch and individual `book_copy` records from legacy book counters, assigns members to that branch, and adds loan-to-copy references.
- Views derive overdue, member-activity, and popular-book reports from loan, member, book, and author records; the fee function derives charges from due and return dates.

#### Dependencies
- Views and routines depend on the `author`, `book`, `member`, and `loan` tables; the migration also alters these tables and adds branch and copy relationships.
- `loan_audit` records refer to `loan` IDs without a declared foreign key; routine logic also uses `CURRENT_DATE`, `now()`, and standard SQL functions.
- The migration uses `generate_series` to generate book-copy records; the seed script targets the core catalog and lending tables.

#### Cross-cutting
- PostgreSQL constraints enforce identifiers, uniqueness, valid copy counts and statuses, valid loan dates, and declared foreign-key relationships.
- The migration and seed script use transactions.
- Loan auditing is trigger-based; checkout and return routines raise exceptions for blocked members, unavailable copies, unknown loans, or already-returned loans.
