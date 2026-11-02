### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:22:05Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports checkout/return workflows, and provides analytical views for reporting.

#### Purpose
- Persist library catalog, member, and loan information.
- Enable checkout, return, and late‑fee calculations.
- Offer reporting views for overdue loans, member activity, and popular books.

#### Responsibilities
- **Data Definition** – Tables (`author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`) and indexes.
- **Business Logic** – Stored procedures/functions (`fn_checkout_book`, `sp_return_book`, `fn_late_fee`) and trigger (`trg_loan_audit`).
- **Reporting** – Views (`overdue_loans`, `member_loan_stats`, `popular_books`) and materialized view.
- **Migration & Seeding** – Scripts to add branches, migrate copy tracking, and seed initial data.

#### Key units
- `author`, `book`, `member`, `loan` tables – core catalog and loan tracking.
- `branch`, `book_copy` tables – branch hierarchy and per‑copy status.
- `loan_audit` table – audit trail of checkout/return actions.
- `fn_checkout_book(p_member_id, p_book_id, p_days)` – checkout logic.
- `sp_return_book(p_loan_id)` – return logic.
- `fn_late_fee(p_due_on, p_returned_on)` – late‑fee calculation.
- `trg_loan_audit` trigger – logs audit rows on loan changes.
- `overdue_loans`, `member_loan_stats`, `popular_books` views – analytical reporting.

#### Data flow
1. **Checkout**: `fn_checkout_book` checks member status, decrements `book.copies_free`, inserts a `loan`, and triggers `trg_loan_audit` to log `CHECKOUT`.
2. **Return**: `sp_return_book` sets `loan.returned_on`, increments `book.copies_free`, and trigger logs `RETURN`.
3. **Late fee**: `fn_late_fee` is called by application logic to compute fees based on `due_on` and `returned_on`.
4. **Reporting**: Views aggregate data from `loan`, `member`, `book`, and `author` to expose overdue counts, member loan stats, and popular books.

#### Dependencies
- **Internal**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- **External**: PostgreSQL built‑ins (`now()`, `CURRENT_DATE`, `generate_series`), standard SQL functions.
- **Indexes**: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id`, `idx_branch_name`, `idx_book_copy_book`.

#### Cross-cutting
- Consistent use of foreign keys and referential integrity across all tables.
- Constraints enforce business rules (e.g., `copies_free <= copies_total`, `due_on > loaned_on`, status checks on `book_copy`).
- Audit trail via `loan_audit` and trigger ensures traceability of loan events.
- Migration script (`04_migration_v2.sql`) demonstrates data transformation from aggregate copy counts to per‑copy rows, preserving historical data.
