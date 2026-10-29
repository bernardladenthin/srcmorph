### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:18:03Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, exposes reporting views, and provides stored routines for checkout, return, and audit.

#### Purpose
- Persist library catalog, member, and loan information.
- Offer reporting views for overdue loans, member activity, and popular books.
- Encapsulate business logic for book checkout, return, late fee calculation, and audit trail.

#### Responsibilities
- **Data Model** – Tables for authors, books, members, loans, branches, book copies, and audit events.
- **Reporting** – Views (`overdue_loans`, `member_loan_stats`, `popular_books`) that aggregate loan data.
- **Business Logic** – Functions and procedures (`fn_checkout_book`, `sp_return_book`, `fn_late_fee`) and triggers (`trg_loan_audit`) that enforce rules and maintain audit records.
- **Migration & Seeding** – Scripts to evolve the schema (branch & copy tables) and populate initial data for demos or tests.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- **Views**: `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Functions**: `fn_checkout_book(p_member_id, p_book_id, p_days)`, `fn_late_fee(p_due_on, p_returned_on)`.
- **Procedure**: `sp_return_book(p_loan_id)`.
- **Trigger**: `trg_loan_audit` on `loan` (calls `trg_loan_audit_fn`).

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, decrements `book.copies_free`, inserts a `loan` row, and triggers `trg_loan_audit` to log `CHECKOUT`.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and triggers `trg_loan_audit` to log `RETURN`.
3. **Reporting** – Views join `loan`, `member`, `book`, `author` (and `book_copy`/`branch` after migration) to compute overdue counts, member statistics, and popular books.

#### Dependencies
- **Internal**: `member`, `book`, `loan`, `author`, `branch`, `book_copy`, `loan_audit`.
- **External**: PostgreSQL system catalogs, standard SQL functions (`now()`, `CURRENT_DATE`).
- **Migration**: Relies on existing `book`, `loan`, `member` tables before adding `branch` and `book_copy`.

#### Cross-cutting
- **Constraints & Indexes**: Primary keys, foreign keys, checks (`copies_free <= copies_total`, `due_on > loaned_on`), and indexes (`idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id`).
- **Audit Trail**: `loan_audit` records every checkout and return with timestamps.
- **Late Fee Logic**: `fn_late_fee` calculates a capped fee of 50¢ per overdue day, capped at $20.00.
- **Branch & Copy Model**: Migration replaces aggregate copy counters with explicit `book_copy` rows linked to `branch`, enabling per‑branch inventory tracking.
