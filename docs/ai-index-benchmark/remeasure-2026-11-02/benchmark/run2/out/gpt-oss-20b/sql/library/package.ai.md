### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:49:41Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports book lending, returns, late fee calculation, and provides reporting views for overdue loans and popular books.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce lending rules and track book availability.
- Offer reporting on loan activity and book popularity.

#### Responsibilities
- **Data Modeling** – Core tables (`author`, `book`, `member`, `loan`) and indexes.
- **Reporting** – Views (`overdue_loans`, `member_loan_stats`) and materialized view (`popular_books`).
- **Business Logic** – Functions and procedures for checkout, return, late fee calculation, and audit trail (`fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `loan_audit` table, trigger).
- **Migration & Expansion** – Branch and per‑copy tracking (`branch`, `book_copy`), schema alterations to `loan` and `member`.
- **Seeding** – Sample data for authors, books, members, and loans.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- **Views**: `overdue_loans`, `member_loan_stats`, materialized view `popular_books`.
- **Functions**: `fn_checkout_book(p_member_id, p_book_id, p_days)`, `fn_late_fee(p_due_on, p_returned_on)`.
- **Procedure**: `sp_return_book(p_loan_id)`.
- **Trigger**: `trg_loan_audit` on `loan` invoking `trg_loan_audit_fn()`.

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, decrements `book.copies_free`, inserts a `loan` row, and returns the new `loan_id`.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and the trigger logs the action in `loan_audit`.
3. **Late fee** – `fn_late_fee` calculates overdue days and fee, capped at 2000.
4. **Reporting** – Views aggregate `loan`, `member`, `book`, and `author` data to expose overdue counts, member loan stats, and popular books.

#### Dependencies
- **Internal**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- **External**: PostgreSQL core features (tables, indexes, triggers, PL/pgSQL functions, materialized views).

#### Cross-cutting
- **Constraints & Checks**: `copies_free <= copies_total`, `due_on > loaned_on`, status checks on `book_copy`.
- **Indexes**: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.
- **Audit Trail**: `loan_audit` records every checkout and return via trigger.
- **Migration**: `04_migration_v2.sql` adds branch and copy tracking, preserving existing data through `book_copy` rows.
