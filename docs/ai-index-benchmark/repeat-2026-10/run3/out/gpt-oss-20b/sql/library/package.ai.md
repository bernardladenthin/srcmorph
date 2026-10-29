### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:58:46Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports book lending, returns, late fees, and provides reporting views for overdue loans, member activity, and popular titles.

#### Purpose
- Persist library catalog, member, and loan information.
- Enable checkout, return, and late‑fee processing.
- Offer reporting on overdue items, member usage, and book popularity.

#### Responsibilities
- **Data definition** – tables for authors, books, members, loans, branches, book copies, and audit trail.
- **Business logic** – functions and procedures for checkout, return, late‑fee calculation, and audit logging.
- **Reporting** – views for overdue loans, member loan statistics, and popular books.
- **Migration & seeding** – scripts to add branch and copy structures and populate initial data.

#### Key units
- `author`, `book`, `member`, `loan` – core catalog and transaction tables.
- `branch`, `book_copy` – branch hierarchy and per‑copy tracking (migration v2).
- `loan_audit` – audit trail for checkout/return events.
- `fn_checkout_book(p_member_id, p_book_id, p_days)` – checkout logic.
- `sp_return_book(p_loan_id)` – return logic.
- `fn_late_fee(p_due_on, p_returned_on)` – late‑fee calculation.
- `trg_loan_audit` / `trg_loan_audit_fn` – audit trigger.
- Views: `overdue_loans`, `member_loan_stats`, `popular_books`.

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, decrements `copies_free`, inserts a `loan`, and triggers `loan_audit` with `CHECKOUT`.
2. **Return** – `sp_return_book` updates `returned_on`, increments `copies_free`, and triggers `loan_audit` with `RETURN`.
3. **Late fee** – `fn_late_fee` is called by application logic to compute fees based on `due_on` and `returned_on`.
4. **Reporting** – Views aggregate data from `loan`, `member`, `book`, and `author` to expose overdue items, member activity, and popular books.

#### Dependencies
- **Internal** – `author`, `book`, `member`, `loan`, `book_copy`, `branch`, `loan_audit`.
- **External** – PostgreSQL functions, triggers, and materialized view refresh mechanisms.
- **Migration** – `04_migration_v2.sql` removes `copies_total`/`copies_free` from `book` and introduces `book_copy` and `branch` tables.

#### Cross‑cutting
- Foreign‑key constraints enforce referential integrity across all tables.
- Indexes (`idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id`) support query performance.
- Consistent use of `BIGSERIAL` primary keys and `TIMESTAMPTZ` timestamps for auditability.
- Business rules (e.g., member blocked, no available copies, late‑fee cap) are encapsulated in stored procedures and triggers.
