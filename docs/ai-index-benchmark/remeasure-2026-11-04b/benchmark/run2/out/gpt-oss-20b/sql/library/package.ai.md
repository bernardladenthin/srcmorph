### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:04:07Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, enforces business rules, and provides reporting and audit capabilities.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce loan rules and track book availability.
- Offer reporting views and audit trails for operations.

#### Responsibilities
- **Data Model** – tables `author`, `book`, `member`, `loan`; indexes and constraints enforce referential integrity and business rules.
- **Reporting** – read‑only views `overdue_loans`, `member_loan_stats`, and materialized view `popular_books` expose aggregated loan data.
- **Business Logic** – functions `fn_checkout_book`, `fn_late_fee`, procedure `sp_return_book`, and trigger `trg_loan_audit` manage checkout, return, late fee calculation, and audit logging.
- **Migration** – script `04_migration_v2.sql` introduces branch hierarchy, per‑branch book copies, and removes legacy copy counters.
- **Seeding** – `05_seed_data.sql` populates initial authors, books, members, and loans for demo/testing.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- **Views**: `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Functions**: `fn_checkout_book(p_member_id, p_book_id, p_days)`, `fn_late_fee(p_due_on, p_returned_on)`.
- **Procedure**: `sp_return_book(p_loan_id)`.
- **Trigger**: `trg_loan_audit` on `loan`; handler `trg_loan_audit_fn`.

#### Data flow
1. **Checkout** – `fn_checkout_book` validates member, decrements `book.copies_free`, inserts a `loan` row, and logs a `CHECKOUT` audit.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and the trigger logs a `RETURN` audit.
3. **Late fee** – `fn_late_fee` calculates fee based on `due_on` and `returned_on`.
4. **Reporting** – views join `loan`, `member`, `book`, and `author` to expose overdue loans, member statistics, and popular books.
5. **Migration** – `04_migration_v2.sql` creates `branch` and `book_copy`, populates copies, updates `loan` to reference copies, and adjusts `member` home branch.

#### Dependencies
- Internal: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- External: PostgreSQL core, PL/pgSQL runtime.
- Indexes: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id`, `idx_book_copy_book`.

#### Cross-cutting
- **Constraints**: foreign keys, uniqueness, check constraints (e.g., `copies_free <= copies_total`, `due_on > loaned_on`).
- **Audit Trail**: `loan_audit` records every checkout and return with timestamp.
- **Error handling**: functions raise exceptions when members are blocked or no copies are available.
- **Concurrency**: updates to `copies_free` and loan inserts rely on row‑level locking inherent to PostgreSQL.
- **Configuration**: migration script sets default branch and barcode generation logic.
