### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:22:37Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL data model and procedural toolkit for a multi‑branch lending library, covering catalog, members, loans, reporting, and audit trails.

#### Purpose
- Persist library catalog, member, and loan data.
- Provide reporting views and materialized data for analytics.
- Enforce business rules for checkout, return, and late fees with audit logging.

#### Responsibilities
- **Schema definition** – core tables (`author`, `book`, `member`, `loan`) and later branch‑aware tables (`branch`, `book_copy`).
- **Reporting** – views (`overdue_loans`, `member_loan_stats`) and materialized view (`popular_books`) with supporting index.
- **Business logic** – functions (`fn_checkout_book`, `fn_late_fee`), procedure (`sp_return_book`), audit trigger (`trg_loan_audit`) and audit table (`loan_audit`).
- **Data migration** – `04_migration_v2.sql` introduces branches and explicit book copies, updating foreign keys and removing aggregate copy counts.
- **Seeding** – `05_seed_data.sql` populates authors, books, members, and loans for development/testing.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- **Views**: `overdue_loans`, `member_loan_stats`; **Materialized view**: `popular_books`.
- **Functions**: `fn_checkout_book(member_id, book_id, days) → loan_id`; `fn_late_fee(due_on, returned_on) → fee`.
- **Procedure**: `sp_return_book(loan_id) → book_id`.
- **Trigger**: `trg_loan_audit` on `loan` invoking `trg_loan_audit_fn`.
- **Index**: `idx_popular_books_id` on `popular_books(book_id)`; `idx_book_copy_book` on `book_copy(book_id)`.

#### Data flow
1. **Checkout** – `fn_checkout_book` validates member, decrements `book_copy.status`, inserts a `loan`, and logs `CHECKOUT` in `loan_audit`.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book_copy.status`, and logs `RETURN` via trigger.
3. **Late fee** – `fn_late_fee` calculates overdue days and caps fee.
4. **Reporting** – Views aggregate `loan`, `member`, `book`, and `author` data to expose overdue counts, member statistics, and popular books.

#### Dependencies
- **Internal**: `member.is_blocked`, `book_copy.status`, `loan` foreign keys to `book_copy`, `member`, `branch`.
- **External**: PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`, date arithmetic).
- **DDL**: Indexes and constraints enforce referential integrity and business rules.

#### Cross‑cutting
- **Audit trail** – `loan_audit` records every checkout and return via trigger.
- **Branch awareness** – `branch` and `book_copy` tables replace aggregate copy counts, enabling per‑location inventory.
- **Consistency checks** – Constraints on `book_copy.status`, `loan.due_on > loaned_on`, and `loan.returned_on` updates ensure data integrity.
- **Seeding** – `05_seed_data.sql` provides a realistic dataset for testing and demos.
