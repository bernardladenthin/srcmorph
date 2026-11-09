### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:39:10Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Supports library lending by managing catalog inventory, member borrowing and returns, loan reporting, auditing, and schema evolution to branch-assigned copies.

#### Purpose
- Define and operate a relational lending-library system covering authors, books, members, inventory, and loans.
- Supply reporting views, lifecycle routines, migration to individually tracked copies, and deterministic development/test data.

#### Responsibilities
- **Core schema and integrity:** `author`, `book`, `member`, and `loan` tables enforce identifiers, uniqueness, foreign keys, inventory limits, and valid loan dates.
- **Reporting:** `overdue_loans` identifies unreturned past-due loans; `member_loan_stats` summarizes borrowing; `popular_books` ranks recent lending activity.
- **Loan lifecycle and audit:** Checkout and return routines validate eligibility, update inventory and loans, calculate late fees, and record lifecycle events in `loan_audit`.
- **Branch and copy migration:** Adds `branch` and per-item `book_copy` records, links loans to copies and members to home branches, and retires book-level copy counters.
- **Seed data:** Inserts fixed author, book, member, and loan records in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, inventory, and borrowing tables.
- `overdue_loans` — view of open loans past their due dates, including days overdue.
- `member_loan_stats` — per-member loan totals, open-loan counts, and most recent loan date.
- `popular_books` — materialized view of book loan counts and author names, with unique index `idx_popular_books_id`.
- `fn_checkout_book(p_member_id, p_book_id, p_days)` — checks out an available book for an eligible member and returns the new loan ID.
- `sp_return_book(p_loan_id)` — marks an open loan returned and restores the book’s free-copy count.
- `fn_late_fee(p_due_on, p_returned_on)` — computes a nonnegative, capped overdue fee.
- `loan_audit`, `trg_loan_audit_fn()`, `trg_loan_audit` — store and capture checkout and return events.
- `branch`, `book_copy` — represent library locations and individually barcoded copies with availability status.
- `04_migration_v2.sql` — migrates copy counts to individual copies and adds member and loan branch/copy associations.

#### Data flow
- Checkout validates member and book availability, inserts a `loan`, and decrements `book.copies_free`; the loan trigger records a `CHECKOUT` event.
- Return updates an open loan’s `returned_on`, increments the associated book’s free-copy count, and the trigger records a `RETURN` event.
- Views derive overdue loans, member statistics, and book popularity from `loan`, `member`, `book`, and `author`; `popular_books` stores its query result.
- Migration generates `book_copy` rows from existing book copy counts, assigns availability from free-copy counts, and assigns existing members a home branch.

#### Dependencies
- Views and routines depend on `author`, `book`, `member`, and `loan`; routines also use `loan_audit`.
- Migration adds `branch` and `book_copy`, references existing `book`, `loan`, and `member` records, and uses PostgreSQL `generate_series`.
- `loan_audit.loan_id` is associated with `loan.loan_id` without a declared foreign-key constraint.

#### Cross-cutting
- PostgreSQL DDL, views, materialized views, PL/pgSQL and SQL routines, triggers, migration, and seed DML.
- Primary keys, unique constraints, checks, foreign keys, and targeted indexes enforce integrity and support common lookups.
- Checkout and return routines raise exceptions for blocked members, unavailable inventory, and unknown or already-returned loans.
- The migration replaces aggregate book-level inventory counters with copy-level `AVAILABLE`, `ON_LOAN`, and `LOST` status.
