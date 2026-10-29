### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:50:49Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages the data model, business logic, and reporting for a multi-branch lending library system, handling book inventory, member accounts, loan transactions, and late-fee calculations.

#### Purpose
- Defines the relational schema, stored procedures, and views for a PostgreSQL-based library management system.
- Supports the full lifecycle of book loans: checkout, return, inventory tracking, and audit logging.

#### Responsibilities
- **Schema Definition**: Establishes core entities (`author`, `book`, `member`, `loan`) and v2 extensions (`branch`, `book_copy`) for granular inventory management.
- **Business Logic**: Implements checkout/return workflows, late-fee calculation, and immutable audit trails via PL/pgSQL functions, procedures, and triggers.
- **Reporting**: Provides views for overdue loans, member statistics, and popular book rankings.
- **Data Initialization**: Seeds the database with deterministic test data and executes schema migrations from counter-based to row-per-copy inventory models.

#### Key units
- **Tables**: `author`, `book`, `member`, `loan` (core); `branch`, `book_copy`, `loan_audit` (v2/audit).
- **Functions**: `fn_checkout_book` (validates member/book, decrements inventory, creates loan), `fn_late_fee` (calculates capped overdue charges).
- **Procedures**: `sp_return_book` (updates loan status, increments inventory).
- **Triggers**: `trg_loan_audit` (logs `CHECKOUT`/`RETURN` events to `loan_audit`).
- **Views**: `overdue_loans` (active past-due loans), `member_loan_stats` (per-member aggregates), `popular_books` (materialized view of top-loaned titles).
- **Migration**: `04_migration_v2.sql` (introduces `branch`/`book_copy`, drops `book.copies_*` counters).

#### Data flow
- **Checkout**: `fn_checkout_book` validates `member.is_blocked` and `book` availability, inserts into `loan`, and triggers `trg_loan_audit` to log the event.
- **Return**: `sp_return_book` updates `loan.returned_on`, triggers `trg_loan_audit` for `RETURN` logging, and updates inventory status.
- **Reporting**: Views aggregate data from `loan`, `member`, `book`, and `author` to surface overdue items and usage statistics.
- **Migration**: `04_migration_v2.sql` transforms `book` counter data into individual `book_copy` rows linked to `branch`, updating `loan` and `member` to reference these new entities.

#### Dependencies
- **Internal**: All SQL objects depend on the core tables (`author`, `book`, `member`, `loan`).
- **External**: PostgreSQL dialect features (`BIGSERIAL`, `PL/pgSQL`, `generate_series`, partial indexes, materialized views).
- **Migration**: `04_migration_v2.sql` depends on pre-existing `book`, `loan`, and `member` tables to populate new `branch` and `book_copy` structures.

#### Cross-cutting
- **Inventory Model Shift**: Transition from aggregate counters (`copies_free`/`copies_total` in `book`) to individual row tracking (`book_copy.status`) in v2.
- **Audit Integrity**: Immutable `loan_audit` table ensures all state changes to `loan` are logged via triggers.
- **Constraint Enforcement**: CHECK constraints enforce data validity (e.g., `copies_free <= copies_total`, `due_on > loaned_on`, `action IN ('CHECKOUT', 'RETURN')`).
- **Deterministic Seeding**: `05_seed_data.sql` provides reproducible test data for development and testing.
