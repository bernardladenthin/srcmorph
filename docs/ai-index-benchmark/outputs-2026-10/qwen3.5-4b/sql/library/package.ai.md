### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T00:06:39Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL-based lending library system managing book inventory, member loans, branch locations, and automated audit trails for checkout/return operations.

#### Purpose
*   Manages the full lifecycle of book loans including checkout validation, return processing, and late fee calculation.
*   Tracks physical book copies across multiple branches and enforces inventory constraints via database constraints and triggers.

#### Responsibilities
*   **Schema Definition**: Defines core tables for authors, books, members, loans, branches, and book copies with constraints for inventory tracking and lifecycle management.
*   **Reporting & Analytics**: Provides views for overdue loans, member statistics, and popular book titles via materialized views.
*   **Business Logic**: Implements PL/pgSQL routines for safe checkout/return transactions, late fee calculation, and automatic audit logging.
*   **Data Migration**: Handles schema evolution from v1 (per-book copy counts) to v2 (branch-based physical copy tracking).
*   **Initialization**: Seeds the database with initial authors, books, members, and loan records.

#### Key units
*   **Tables**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
*   **Functions**: `fn_checkout_book` (validates and creates loans), `sp_return_book` (processes returns), `fn_late_fee` (calculates overdue charges).
*   **Triggers**: `trg_loan_audit` (automatically logs loan events).
*   **Views**: `overdue_loans`, `member_loan_stats`, `popular_books`.
*   **SQL Objects**: `01_schema.sql` (core DDL), `02_views.sql` (reporting), `03_routines.sql` (logic), `04_migration_v2.sql` (schema evolution), `05_seed_data.sql` (initial data).

#### Data flow
*   **Checkout Flow**: User requests loan -> `fn_checkout_book` validates member status and book availability -> decrements `copies_free` -> inserts record into `loan` table -> triggers `trg_loan_audit` -> updates `book_copy` status.
*   **Return Flow**: User returns book -> `sp_return_book` updates `returned_on` in `loan` table -> increments `copies_free` in `book` or `book_copy` -> triggers `trg_loan_audit`.
*   **Reporting Flow**: `overdue_loans` and `member_loan_stats` views aggregate data from `loan`, `member`, and `book` tables; `popular_books` materialized view tracks loan frequency.
*   **Migration Flow**: `04_migration_v2.sql` replaces `book.copies_free` logic with `book_copy` table and links loans to specific physical copies via `copy_id`.

#### Dependencies
*   **Internal**: `member` (for blocking checks), `book` (for inventory), `loan` (for transaction records), `loan_audit` (for logging).
*   **External Modules**: PostgreSQL PL/pgSQL for stored procedures and triggers; `CURRENT_DATE`/`CURRENT_TIMESTAMP` for time calculations.
*   **Data Models**: Relational integrity between `author`->`book`->`loan` and `member`->`loan`.

#### Cross-cutting
*   **Shared Types**: `BIGSERIAL` for IDs, `TIMESTAMPTZ` for audit timestamps, `BOOLEAN` for member status.
*   **Constraints**: `CHECK` constraints on `copies_free` vs `copies_total`, `is_blocked` status, and `due_on` validity.
*   **Concurrency**: Routines use transactional logic to prevent double-checkouts and ensure atomic updates to inventory.
*   **Audit Trail**: All loan creation and return events are automatically captured in `loan_audit` via triggers.
*   **Migration Strategy**: Gradual schema change from per-book copy counts to branch-specific physical copy tracking.
