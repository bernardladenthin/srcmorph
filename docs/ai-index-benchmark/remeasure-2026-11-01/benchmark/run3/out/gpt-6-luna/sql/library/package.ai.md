### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:03:10Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalog, member borrowing, copy inventory, loan lifecycle, overdue reporting, and audit history.

#### Purpose
- Defines and evolves the relational data model for authors, books, members, branches, physical copies, and loans.
- Provides checkout and return operations, late-fee calculation, circulation reports, and sample data.

#### Responsibilities
- **Catalog and circulation schema:** `author`, `book`, `member`, and `loan` tables; keys, constraints, and indexes enforce catalog relationships, copy counts, and valid loan dates.
- **Inventory migration:** adds `branch` and barcode-tracked `book_copy` records, associates members with home branches and loans with copies, and removes legacy book-level copy counters.
- **Loan operations and audit:** checks out available books to unblocked members, records returns, maintains available-copy counts, calculates capped late fees, and audits checkout and return events.
- **Circulation reporting:** exposes overdue loans and member borrowing statistics; materializes popular-book counts for loans in the past year.
- **Sample data:** seeds authors, books, members, and loan history in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and borrowing tables.
- `branch`, `book_copy` — branch locations and individual inventory, with copy barcode and status.
- `overdue_loans` — unreturned loans past due, including borrower, title, and days overdue.
- `member_loan_stats` — per-member total and open loan counts and most recent loan date.
- `popular_books` — materialized report of books loaned at least five times in the past year; indexed by `idx_popular_books_id`.
- `fn_checkout_book` — validates member and copy availability, creates a loan, and returns its ID.
- `sp_return_book` — marks a loan returned and restores book availability.
- `fn_late_fee` — computes overdue fees at 50 cents per day, capped at 2000 cents.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — store and record checkout and return events.

#### Data flow
- Catalog and member records provide the references used by loans; reporting views join loan activity with member, book, and author data.
- `fn_checkout_book` checks member status and book availability, updates available-copy counts, then inserts a loan; the audit trigger records checkout.
- `sp_return_book` marks an open loan returned and restores availability; the audit trigger records the return.
- The migration creates copy inventory from existing book copy totals, assigns it to a seeded branch, and replaces book-level copy counts with copy-level tracking.

#### Dependencies
- Views and routines depend on `author`, `book`, `member`, and `loan`; the audit trigger writes to `loan_audit`.
- `book_copy` references `book` and `branch`; `loan.copy_id` references `book_copy`; `member.home_branch_id` references `branch`.
- Migration uses PostgreSQL `generate_series`; routines use `CURRENT_DATE`, `now()`, `COALESCE`, `GREATEST`, and `LEAST`.

#### Cross-cutting
- PostgreSQL primary keys, foreign keys, uniqueness, checks, and indexes enforce identity, relationships, valid statuses, and date/count constraints.
- Checkout rejects blocked members or unavailable books; return rejects unknown or already-returned loans.
- The initial schema uses `copies_total` and `copies_free`; the migration replaces those counters with `book_copy.status` inventory tracking.
