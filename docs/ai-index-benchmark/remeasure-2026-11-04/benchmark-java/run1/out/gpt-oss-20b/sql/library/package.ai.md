### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:19:12Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL library system that stores catalog, member, and loan data, enforces lending rules, tracks overdue status, and supports branch‑level copy management.

#### Purpose
- Persist core library entities (authors, books, members, loans).
- Enforce checkout/return business logic and audit trail.
- Provide reporting views for overdue loans, member activity, and popular books.
- Support multi‑branch operations with dedicated book copies.

#### Responsibilities
- **Data Modeling** – tables for authors, books, members, loans, and later branches and book copies.
- **Business Logic** – functions and procedures for checkout, return, late fee calculation, and audit logging.
- **Reporting** – read‑only views and a materialized view summarizing overdue loans, member statistics, and popular books.
- **Migration & Seeding** – scripts to evolve schema to branch‑aware design and populate initial data.

#### Key units
- `author`, `book`, `member`, `loan` tables – core catalog and patron data.
- `branch`, `book_copy` tables – branch hierarchy and copy tracking.
- `loan_audit` table – audit trail of checkout/return actions.
- `fn_checkout_book(p_member_id, p_book_id, p_days)` – checkout logic.
- `sp_return_book(p_loan_id)` – return logic.
- `fn_late_fee(p_due_on, p_returned_on)` – late fee calculation.
- `trg_loan_audit_fn()` – trigger function logging audit events.
- `overdue_loans`, `member_loan_stats`, `popular_books` views – reporting surfaces.

#### Data flow
1. **Checkout**: `fn_checkout_book` checks member status, decrements `book.copies_free`, inserts a `loan`, and triggers `loan_audit` with action `CHECKOUT`.
2. **Return**: `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and triggers `loan_audit` with action `RETURN`.
3. **Late fee**: `fn_late_fee` is called by application logic (or could be used in reports) to compute fees based on due and return dates.
4. **Reporting**: Views aggregate data from `loan`, `member`, `book`, `author`, and `book_copy` to expose overdue loans, member loan stats, and popular books.
5. **Branch migration**: `04_migration_v2.sql` introduces `branch` and `book_copy`, linking loans to specific copies and members to a home branch.

#### Dependencies
- Internal: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- External: PostgreSQL built‑in functions (`now()`, `CURRENT_DATE`), standard SQL constraints and indexes.
- Views depend on the base tables and foreign key relationships.

#### Cross-cutting
- **Constraints & Indexes**: Enforce data integrity (e.g., `copies_free <= copies_total`, `due_on > loaned_on`) and performance (`idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`).
- **Error handling**: Functions raise errors when business rules are violated (e.g., blocked member, no free copies).
- **Audit trail**: Trigger `trg_loan_audit` ensures every checkout and return is logged in `loan_audit`.
- **Branch normalization**: Migration splits copies into `book_copy` with status and barcode, linking loans to specific copies and members to a home branch.
