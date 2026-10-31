### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:47:03Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, supports book lending, returns, late‑fee calculation, and provides reporting views and audit trails.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce lending rules and track book availability.
- Offer reporting on overdue loans, member activity, and popular titles.

#### Responsibilities
- **Data Modeling** – tables for authors, books, members, loans, branches, and book copies.
- **Business Logic** – checkout, return, late‑fee calculation, and audit logging.
- **Reporting** – views for overdue loans, member statistics, and popular books.
- **Migration & Seeding** – schema evolution to branch‑based copies and initial data population.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- **Functions**: `fn_checkout_book`, `fn_late_fee`.
- **Procedures**: `sp_return_book`.
- **Triggers**: `trg_loan_audit` (AFTER INSERT/UPDATE on `loan`), `trg_loan_audit_fn`.
- **Views**: `overdue_loans`, `member_loan_stats`, materialized view `popular_books`.

#### Data flow
1. **Checkout** – `fn_checkout_book` validates member and copy availability, decrements `book_copy.status`, inserts a `loan` record, and logs `CHECKOUT` in `loan_audit`.
2. **Return** – `sp_return_book` updates `loan.returned_on`, sets `book_copy.status` to `AVAILABLE`, and logs `RETURN` via trigger.
3. **Late fee** – `fn_late_fee` computes fees based on `loan.due_on` and `loan.returned_on`.
4. **Reporting** – views join `loan`, `member`, `book`, `author`, and `book_copy` to surface overdue, statistical, and popularity data.

#### Dependencies
- **Internal**: `author`, `book`, `member`, `loan`, `book_copy`, `branch`, `loan_audit`.
- **External**: PostgreSQL built‑in functions (`now()`, `CURRENT_DATE`, `INTERVAL`).
- **Schema changes**: Migration `04_migration_v2.sql` adds `branch` and `book_copy`, alters `loan` and `member`, removes copy counts from `book`.

#### Cross‑cutting
- **Constraints & Indexes**: foreign keys, uniqueness on ISBN/barcode, check constraints on counts and status, indexes on foreign keys and overdue loans.
- **Error handling**: functions raise errors for blocked members, unavailable copies, or duplicate returns.
- **Audit trail**: `loan_audit` records every checkout and return with timestamp.
- **Materialized view**: `popular_books` refreshed to reflect recent loan activity.
