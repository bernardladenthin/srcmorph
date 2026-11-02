### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:06:41Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports branch‑level inventory, and provides reporting and audit trails for book lending operations.

#### Purpose
- Persist library catalog, member, and loan information.
- Enable book checkout, return, and late‑fee calculation.
- Offer reporting views for overdue items, member activity, and popular books.
- Support multi‑branch inventory management.

#### Responsibilities
- **Data Modeling** – tables for authors, books, members, loans, branches, and book copies.
- **Business Logic** – functions and procedures for checkout, return, late‑fee calculation, and audit logging.
- **Reporting** – read‑only views and a materialized view for overdue loans, member statistics, and popular books.
- **Migration & Seeding** – scripts to evolve the schema to branch support and populate initial data.

#### Key units
- `author`, `book`, `member`, `loan` – core catalog and transaction tables.
- `branch`, `book_copy` – branch hierarchy and copy tracking.
- `loan_audit` – audit trail of checkout/return actions.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` – core business functions.
- `trg_loan_audit` – trigger that records audit rows.
- Views: `overdue_loans`, `member_loan_stats`, `popular_books` (materialized).

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, decrements `book.copies_free`, inserts a `loan` row, and logs a `CHECKOUT` audit record.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and triggers a `RETURN` audit record.
3. **Late fee** – `fn_late_fee` computes overdue days and caps the fee.
4. **Reporting** – Views join `loan`, `member`, `book`, `author`, and `book_copy` (post‑migration) to expose overdue counts, member loan totals, and loan frequency per book.

#### Dependencies
- **Internal** – `member`, `book`, `loan`, `book_copy`, `branch`, `loan_audit`.
- **External** – PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`, interval arithmetic).
- **Schema evolution** – `04_migration_v2.sql` adds `branch` and `book_copy`, altering `loan` and `member` to reference them.

#### Cross‑cutting
- Consistent use of foreign keys and referential integrity across all tables.
- Centralized audit via `loan_audit` and trigger `trg_loan_audit`.
- Late‑fee logic encapsulated in `fn_late_fee` for reuse.
- Reporting views rely on standard SQL joins and aggregate functions, with a materialized view for performance on popular books.
