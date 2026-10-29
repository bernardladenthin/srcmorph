### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:06:02Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages the complete data lifecycle for a multi-branch lending library, from core inventory and patron modeling to transactional processing, audit logging, and historical schema evolution.

#### Purpose
- Defines the relational data model for tracking books, authors, members, and loan transactions across multiple physical branches.
- Orchestrates schema evolution from single-branch inventory to granular copy-level tracking while maintaining data integrity.

#### Responsibilities
- **Data Modeling**: Establishes core entities (`author`, `book`, `member`, `loan`) and multi-branch extensions (`branch`, `book_copy`).
- **Operational Logic**: Implements checkout, return, and late fee calculation procedures with automatic inventory updates.
- **Audit & Reporting**: Maintains immutable audit trails for loan state changes and provides materialized views for overdue and popularity analytics.
- **Migration Management**: Executes versioned DDL scripts to transition from global copy counters to specific physical copy tracking.
- **Data Seeding**: Populates initial reference data and transactional baselines for testing environments.

#### Key units
- `author`: Stores bibliographic metadata with birth year and country constraints.
- `book`: Tracks ISBN, title, and legacy inventory counts (migrated to `book_copy`).
- `member`: Manages patron identity, blocking status, and assigned home branch.
- `loan`: Records borrowing transactions with due dates and return timestamps.
- `branch`: Defines physical library locations for multi-branch inventory.
- `book_copy`: Represents individual physical items with barcodes and status states.
- `loan_audit`: Immutable log table capturing checkout and return actions.
- `fn_checkout_book`: Procedure to validate members, decrement inventory, and create loans.
- `sp_return_book`: Procedure to process returns, increment inventory, and validate state.
- `fn_late_fee`: Function to calculate overdue penalties capped at 2000 cents.
- `overdue_loans`: View aggregating unreturned items past their due date.
- `popular_books`: Materialized view ranking books by loan frequency over the last year.
- `04_migration_v2.sql`: DDL script transitioning schema to support `book_copy` and `branch`.

#### Data flow
- **Initialization**: `05_seed_data.sql` populates `author`, `book`, `member`, and `loan` tables.
- **Evolution**: `04_migration_v2.sql` reads legacy `book` counts, creates `branch` and `book_copy` records, and alters `loan`/`member` tables to reference new entities.
- **Transaction**: `fn_checkout_book` validates `member` status, decrements `book_copy` status, and inserts a `loan` record.
- **Audit**: `trg_loan_audit` triggers on `loan` insert/update to write actions to `loan_audit`.
- **Return**: `sp_return_book` updates `loan` return date, increments `book_copy` status, and triggers audit logging.
- **Reporting**: `overdue_loans` and `popular_books` views query `loan`, `book`, and `member` tables to aggregate statistics.

#### Dependencies
- **Internal**: `loan` depends on `book` and `member`; `book_copy` depends on `book` and `branch`; `loan_audit` depends on `loan`.
- **External**: PostgreSQL system functions (`CURRENT_DATE`, `now()`, `generate_series`) and SQL dialect features (CTEs, window functions implied in views).
- **Legacy**: Migration scripts depend on the existence of `copies_free` and `copies_total` columns in the `book` table.

#### Cross-cutting
- **Constraint Enforcement**: Heavy use of `CHECK` constraints for date validity, inventory limits, and status enumeration.
- **Foreign Keys**: Cascading deletes on `loan` to maintain referential integrity when members or books are removed.
- **Indexing**: Strategic indexes on `author_id`, `member_id`, `due_on`, and `book_copy.book_id` to optimize join and filter performance.
- **State Management**: `book_copy` status (`AVAILABLE`, `ON_LOAN`, `LOST`) drives inventory availability logic.
- **Audit Trail**: Trigger-based logging ensures all loan state changes are recorded immutably in `loan_audit`.
