### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T23:41:10Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages library inventory, member circulation, and loan lifecycle operations within a PostgreSQL relational database.

#### Purpose
*   Establish persistent storage for library inventory and circulation data.
*   Implement core lending workflow including checkout, returns, and audit logging.

#### Responsibilities
*   Define relational schema for authors, books, members, and loans.
*   Provide reporting views for overdue items, member statistics, and popular books.
*   Execute business logic for book checkout, returns, and late fee calculation.
*   Migrate schema to support multi-branch inventory tracking.
*   Seed initial catalog and transaction data for testing.

#### Key units
*   `author`: Stores author metadata with country codes and birth years.
*   `book`: Tracks ISBN, title, and copy availability counters.
*   `member`: Manages member profiles, email, and block status.
*   `loan`: Records transaction details including loaned dates and due dates.
*   `overdue_loans`: View filtering active loans past due date.
*   `popular_books`: Materialized view aggregating loan counts per book.
*   `fn_checkout_book`: Function validating member status and decrementing copies.
*   `sp_return_book`: Procedure updating loan return date and incrementing copies.
*   `branch`: New table for multi-branch inventory support.
*   `book_copy`: Tracks individual physical copies with status and barcode.
*   `loan_audit`: Table logging checkout and return actions.

#### Data flow
*   `01_schema.sql` creates base tables (`author`, `book`, `member`, `loan`).
*   `05_seed_data.sql` populates base tables with sample records.
*   `03_routines.sql` functions modify `loan` and `book` tables during circulation.
*   `02_views.sql` aggregates `loan`, `book`, `member` data for reporting.
*   `04_migration_v2.sql` transforms `book` and `loan` structures to support `branch` and `book_copy`.

#### Dependencies
*   Internal: `loan` table depends on `book` and `member` tables.
*   Internal: `loan_audit` depends on `loan` table.
*   Internal: Views depend on `loan`, `book`, `member`, `author` tables.
*   Internal: Migration depends on legacy `book` and `member` tables.
*   External: PostgreSQL database engine (DDL, PL/pgSQL).

#### Cross-cutting
*   PostgreSQL DDL/DML syntax used across all files.
*   Foreign key constraints enforce referential integrity between `loan`, `book`, `member`.
*   Transaction blocks wrap seed data insertion.
*   Status constraints applied to `book_copy` (AVAILABLE, ON_LOAN, LOST).
*   Audit trigger `trg_loan_audit_fn` logs state changes on `loan` table.
