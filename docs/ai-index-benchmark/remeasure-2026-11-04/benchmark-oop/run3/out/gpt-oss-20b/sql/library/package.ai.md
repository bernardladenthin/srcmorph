### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:57:02Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL library management system that stores catalog, member, and loan data, enforces business rules, and provides reporting and audit capabilities.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce lending rules (availability, member status, due dates).
- Offer read‑only reporting and audit trails.

#### Responsibilities
- **Data definition** – tables, indexes, and constraints for authors, books, members, loans, and audit.
- **Business logic** – checkout, return, late‑fee calculation, and audit triggers.
- **Reporting** – overdue loans, member statistics, and popular books views.
- **Schema evolution** – branch and book‑copy normalization in migration v2.
- **Seeding** – initial test data for authors, books, members, and loans.

#### Key units
- `author`, `book`, `member`, `loan` tables – core entities.
- `loan_audit` table – audit trail for checkout/return actions.
- `fn_checkout_book(p_member_id, p_book_id, p_days)` – checkout logic.
- `sp_return_book(p_loan_id)` – return logic.
- `fn_late_fee(p_due_on, p_returned_on)` – late‑fee calculation.
- `trg_loan_audit_fn()` – audit trigger function.
- `overdue_loans`, `member_loan_stats`, `popular_books` views – reporting objects.
- `branch`, `book_copy` tables – branch and copy dimension added in migration v2.

#### Data flow
1. **Checkout**: `fn_checkout_book` verifies member and copy availability, decrements `book.copies_free`, inserts a `loan` record, and triggers `loan_audit` with action `CHECKOUT`.
2. **Return**: `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and triggers `loan_audit` with action `RETURN`.
3. **Late fee**: `fn_late_fee` computes overdue days and caps the fee.
4. **Reporting**: Views join `loan`, `member`, `book`, and `author` (or `book_copy` after migration) to expose overdue loans, member loan stats, and popular books.
5. **Migration**: `04_migration_v2.sql` introduces `branch` and `book_copy`, removes aggregate copy columns from `book`, and adds foreign keys to `loan` and `member`.

#### Dependencies
- **Internal**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- **External**: PostgreSQL built‑in types, sequences, and standard functions (`now()`, `CURRENT_DATE`).
- **Triggers**: `trg_loan_audit` on `loan` after insert or update of `returned_on`.

#### Cross-cutting
- **Constraints**: foreign keys, uniqueness, check constraints (e.g., `copies_free <= copies_total`, `status` enum, `birth_year` range).
- **Indexes**: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.
- **Audit pattern**: `loan_audit` table and trigger function log every checkout and return.
- **Branch normalization**: migration replaces total/free copy counts with per‑copy status, enabling multi‑branch support.
