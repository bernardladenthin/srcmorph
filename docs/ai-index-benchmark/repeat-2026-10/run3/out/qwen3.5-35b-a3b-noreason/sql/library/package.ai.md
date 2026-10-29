### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:45:31Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages the complete lifecycle of a multi-branch lending library system, from core inventory and member data modeling to transactional lending logic, audit trails, and analytical reporting.

#### Purpose
- Establishes the relational data model for authors, books, members, and loans, evolving to support multi-branch operations and per-copy tracking.
- Implements business logic for checkout/return operations, late fee calculations, and immutable audit logging.

#### Responsibilities
- **Data Modeling & Evolution**: Defines core entities (`author`, `book`, `member`, `loan`) and executes schema migrations to introduce `branch` and `book_copy` entities for multi-location support.
- **Operational Logic**: Encapsulates lending workflows via functions (`fn_checkout_book`, `fn_late_fee`) and procedures (`sp_return_book`) with validation and state updates.
- **Audit & Integrity**: Maintains an immutable history of transactions via the `loan_audit` table and triggers (`trg_loan_audit`).
- **Reporting & Analytics**: Provides read-optimized views (`overdue_loans`, `member_loan_stats`) and materialized views (`popular_books`) for business intelligence.
- **Initialization**: Seeds the database with reference data for testing and benchmarking.

#### Key units
- `author`, `book`, `member`, `loan`: Core relational tables defining library inventory and transaction history.
- `branch`, `book_copy`: New entities introduced in v2 to manage physical locations and individual item copies.
- `loan_audit`: Immutable log table recording `CHECKOUT` and `RETURN` actions with timestamps.
- `fn_checkout_book`: Function validating member status and availability before inserting a new loan record.
- `sp_return_book`: Procedure updating loan status and restoring book availability upon return.
- `fn_late_fee`: Function calculating overdue charges capped at a maximum threshold.
- `overdue_loans`, `member_loan_stats`: SQL views aggregating loan data for operational monitoring.
- `popular_books`: Materialized view indexing frequently borrowed titles for fast retrieval.
- `trg_loan_audit_fn`: Trigger function automatically logging state changes to the audit table.

#### Data flow
- **Initialization**: `05_seed_data.sql` populates `author`, `book`, `member`, and `loan` tables with initial reference data.
- **Schema Evolution**: `04_migration_v2.sql` transforms the single-branch model by creating `branch` and `book_copy`, migrating `loan` to reference specific copies, and retiring per-book counters.
- **Transaction Execution**: `fn_checkout_book` validates inputs against `member` and `book` tables, decrements availability, and inserts a `loan` record; `sp_return_book` reverses this by updating `loan` and incrementing availability.
- **Audit Logging**: The `trg_loan_audit_fn` trigger intercepts `INSERT` and `UPDATE` events on `loan` to write immutable records to `loan_audit`.
- **Reporting**: Views and materialized views read from the core tables (`loan`, `member`, `book`, `author`) to calculate metrics like `days_overdue` and `times_loaned`.

#### Dependencies
- **Internal**: `loan` table is central, referenced by `loan_audit`, `overdue_loans`, `member_loan_stats`, and `popular_books`.
- **External/Standard**: PostgreSQL native functions (`CURRENT_DATE`, `now()`) and types (`BIGSERIAL`, `TIMESTAMPTZ`).
- **Migration**: `04_migration_v2.sql` depends on the pre-existing schema defined in `01_schema.sql` to perform alterations.

#### Cross-cutting
- **Constraint Enforcement**: Heavy use of `CHECK` constraints (e.g., `copies_free <= copies_total`, `due_on > loaned_on`) and `UNIQUE` constraints to enforce business rules at the database level.
- **Cascade Deletes**: `loan.member_id` includes `ON DELETE CASCADE` to maintain referential integrity when members are removed.
- **State Management**: `book_copy` uses a `status` column with a `CHECK` constraint to enforce states (`AVAILABLE`, `ON_LOAN`, `LOST`).
- **Audit Pattern**: A dedicated `loan_audit` table and trigger mechanism ensure all state changes to loans are recorded immutably.
