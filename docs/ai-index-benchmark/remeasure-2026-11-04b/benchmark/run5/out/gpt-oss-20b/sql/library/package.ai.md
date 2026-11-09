### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:55:40Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL library management system that stores catalog, member, and loan data, tracks book availability, and provides reporting and audit trails.

#### Purpose
- Persist library catalog, members, and loan transactions.
- Enable checkout, return, late‑fee calculation, and audit logging.
- Offer reporting views for overdue loans, member activity, and popular books.

#### Responsibilities
- **Data Modeling** – tables for authors, books, members, loans, branches, and book copies.
- **Business Logic** – functions and procedures for checkout, return, late‑fee calculation, and audit trail.
- **Reporting** – views and a materialized view for overdue loans, member statistics, and popular books.
- **Migration & Seeding** – scripts to add branch and copy tracking, migrate existing data, and seed initial records.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- **Functions**: `fn_checkout_book`, `fn_late_fee`.
- **Procedure**: `sp_return_book`.
- **Trigger**: `trg_loan_audit` (calls `trg_loan_audit_fn`).
- **Views**: `overdue_loans`, `member_loan_stats`; **Materialized View**: `popular_books`.

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, decrements `book_copy.status`, inserts a `loan` record, and logs a `CHECKOUT` audit.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book_copy.status`, and logs a `RETURN` audit.
3. **Late fee** – `fn_late_fee` computes overdue days and caps the fee.
4. **Reporting** – Views aggregate `loan`, `member`, `book`, and `author` data to expose overdue loans, member activity, and popular books.

#### Dependencies
- **Internal**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- **External**: PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`, `INTERVAL`).
- **Migration**: Uses existing `book` and `loan` tables to populate `book_copy` and add branch references.

#### Cross-cutting
- **Constraints & Checks**: `copies_free <= copies_total`, status checks on `book_copy`, foreign‑key integrity across all tables.
- **Indexes**: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.
- **Audit Trail**: `loan_audit` records every checkout and return with timestamps.
- **Branch & Copy Tracking**: `book_copy` replaces aggregate copy counts, enabling per‑branch inventory management.
