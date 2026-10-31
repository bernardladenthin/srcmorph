### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:55:48Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL data model and set of routines for a multi‑branch lending library, covering catalog, members, loans, reporting, and inventory management.

#### Purpose
- Store and manage library catalog, member accounts, and loan transactions.
- Provide reporting views for overdue loans, member activity, and popular books.
- Enforce checkout/return business rules and audit trail.

#### Responsibilities
- **Catalog & Inventory** – tables for authors, books, branches, and book copies; migration to branch‑based copies.
- **Member Management** – member records, blocking status, and branch affiliation.
- **Loan Processing** – checkout, return, late‑fee calculation, and audit logging.
- **Reporting** – views for overdue loans, member loan statistics, and popular books.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- **Views**: `overdue_loans`, `member_loan_stats`, materialized view `popular_books`.
- **Functions**: `fn_checkout_book`, `fn_late_fee`.
- **Procedure**: `sp_return_book`.
- **Trigger**: `trg_loan_audit` (fires `trg_loan_audit_fn`).

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, book availability, decrements `book_copy.status`, inserts a `loan` record, and logs `CHECKOUT` in `loan_audit`.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book_copy.status`, and logs `RETURN` via trigger.
3. **Late fee** – `fn_late_fee` calculates fee based on `due_on` and `returned_on` (or current date).
4. **Reporting** – Views join `loan`, `member`, `book`, `author`, and `book_copy` to expose aggregated data; `popular_books` materialized view aggregates loan counts per book.

#### Dependencies
- **Internal**: `member`, `book`, `book_copy`, `loan`, `author`, `branch`.
- **External**: PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`, `COALESCE`).
- **Migration**: `04_migration_v2.sql` alters schema to introduce branches and book copies, updating foreign keys and removing legacy copy counters.

#### Cross-cutting
- **Constraints & Indexes**: foreign keys, unique constraints on ISBN and barcode, checks on copy counts and loan dates, indexes on foreign keys and overdue loans.
- **Error handling**: functions raise exceptions for blocked members, unavailable copies, or invalid loan states.
- **Audit trail**: `loan_audit` records every checkout and return with timestamp.
- **Branch normalization**: `book_copy` links books to specific branches, enabling branch‑specific inventory and loan tracking.
