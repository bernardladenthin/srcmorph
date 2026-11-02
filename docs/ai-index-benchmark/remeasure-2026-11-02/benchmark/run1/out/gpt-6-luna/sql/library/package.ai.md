### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:29:53Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages library cataloging and circulation, including member borrowing, copy inventory, overdue reporting, and loan auditing.

#### Purpose
- Defines the PostgreSQL data model and workflows for authors, books, members, branches, physical copies, and loans.
- Supports checkout and return operations, late-fee calculation, circulation reports, and sample library data.

#### Responsibilities
- **Catalog and circulation schema:** `author`, `book`, `member`, and `loan` tables with keys, relationships, validation checks, and lookup indexes.
- **Branch inventory migration:** Adds `branch` and per-copy `book_copy` records; migrates existing book counts into copy rows and links loans and members to branches or copies.
- **Loan workflows and auditing:** Checks out available books for eligible members, processes returns, calculates overdue fees, and records checkout and return events.
- **Reporting:** Provides views for overdue loans and member borrowing activity, plus a materialized ranking of popular books.
- **Sample data:** Seeds authors, books, members, and loans in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and lending records.
- `branch`, `book_copy` — branch locations and individually identified copies with availability status.
- `fn_checkout_book` — validates member and copy availability, creates a loan, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores the book’s available-copy count.
- `fn_late_fee` — calculates a capped overdue charge using the return date or current date.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — store and generate audit events for checkout and first-time return.
- `overdue_loans`, `member_loan_stats`, `popular_books` — expose overdue, member activity, and popular-book reports; `popular_books` is materialized.
- `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id`, `idx_book_copy_book` — indexes supporting joins, open-loan lookup, and popular-book uniqueness.

#### Data flow
- Seed data populates authors, books, members, and loans.
- Checkout checks member eligibility and book availability, then decrements availability and inserts a loan; the loan trigger records a `CHECKOUT` audit event.
- Return updates an unreturned loan and restores availability; the trigger records a `RETURN` event on the first transition to returned.
- Reporting views derive overdue status, member loan statistics, and loan counts from `loan`, `member`, `book`, and `author`.
- The v2 migration creates a main branch and generates `book_copy` rows from existing book copy counts, then associates members and loans with branch/copy inventory and removes book-level copy counters.

#### Dependencies
- Reporting and loan workflows use the `author`, `book`, `member`, and `loan` tables; the migration alters `book`, `member`, and `loan`.
- `book` references `author`; `loan` references `book` and `member`; migrated copy and branch relationships connect `book_copy` to `book` and `branch`, `loan` to `book_copy`, and `member` to `branch`.
- The migration uses PostgreSQL `generate_series`; routines and reports use built-ins including `CURRENT_DATE`, `COUNT`, and `MAX`.

#### Cross-cutting
- PostgreSQL constraints enforce unique identifiers such as ISBN, member email, and copy barcode; checks validate copy status, copy counts, and loan dates.
- Loan audit actions are constrained to `CHECKOUT` and `RETURN`; the audit table’s loan association is logical rather than enforced by a declared foreign key.
- The migration replaces book-level copy counters with per-copy inventory and renames `member.joined_on` to `member.member_since`.
