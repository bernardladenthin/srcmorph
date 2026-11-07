### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:12:04Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL‑based library management system that stores catalog, member, and loan data, tracks book availability, and provides reporting and audit trails for lending operations.

#### Purpose
- Persist library catalog, member, and loan information.
- Enforce borrowing rules and maintain audit logs.

#### Responsibilities
- **Data definition** – tables, indexes, and constraints for authors, books, members, loans, and audit.
- **Reporting** – views for overdue loans, member statistics, and popular books.
- **Business logic** – checkout, return, late‑fee calculation, and audit logging.
- **Schema evolution** – migration to support branches and per‑copy tracking.
- **Seeding** – initial data for authors, books, members, and loans.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- **Views**: `overdue_loans`, `member_loan_stats`, materialized `popular_books`.
- **Functions**: `fn_checkout_book`, `fn_late_fee`.
- **Procedure**: `sp_return_book`.
- **Trigger**: `trg_loan_audit` (fires `trg_loan_audit_fn` on loan changes).

#### Data flow
1. **Checkout** – `fn_checkout_book` validates member, decrements `book.copies_free`, inserts a `loan` row, and logs a `CHECKOUT` audit.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free`, and logs a `RETURN` audit.
3. **Late fee** – `fn_late_fee` computes fee based on `due_on` and `returned_on` (or current date).
4. **Reporting** – Views aggregate `loan`, `member`, `book`, and `author` data to expose overdue loans, member activity, and popular books.

#### Dependencies
- **Internal**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- **External**: PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`, date arithmetic).
- **Migration**: Adds `branch` and `book_copy` tables, removes copy counters from `book`, and links `loan` and `member` to branches.

#### Cross‑cutting
- **Constraints**: foreign keys, uniqueness, check constraints on dates, copies, and status.
- **Indexes**: on foreign keys and materialized view (`idx_popular_books_id`).
- **Audit trail**: `loan_audit` table records every checkout and return with timestamps.
- **Branching**: after migration, loans reference a specific `book_copy` and members have a `home_branch_id`.
