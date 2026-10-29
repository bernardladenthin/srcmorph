### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:08:09Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> This package provides the complete relational database structure and operational logic for managing a lending library system, covering entity definition, reporting, and transaction processing.

#### Purpose
* Defines the core data model for authors, books, members, and loan transactions.
* Implements business logic for lending operations, including checkout, return, and fee calculation.

#### Responsibilities
* Schema Definition: Establishing core tables like `author`, `book`, `member`, and `loan` (01_schema.sql).
* Reporting and Analytics: Creating views and materialized views for loan statistics and overdue items (02_views.sql).
* Transaction Management: Handling the lifecycle of loans via functions, procedures, and triggers (03_routines.sql).
* Data Migration: Evolving the schema to support physical book locations via `branch` and `book_copy` (04_migration_v2.sql).
* Initialization: Populating the database with initial reference and transactional data (05_seed_data.sql).

#### Key units
* `author`, `book`, `member`, `loan`: Core entities defining library assets and users (01_schema.sql).
* `overdue_loans`, `member_loan_stats`, `popular_books`: Reporting views and materialized views for system metrics (02_views.sql).
* `fn_checkout_book`, `sp_return_book`, `fn_late_fee`: PL/pgSQL routines managing lending state changes (03_routines.sql).
* `loan_audit`: Table tracking immutable changes to loan events (03_routines.sql).
* `branch`, `book_copy`: Entities introduced in migration to track physical book locations (04_migration_v2.sql).

#### Data flow
Data flows from initial seeding (05_seed_data.sql) into the core tables (`author`, `book`, `member`). Transactions are managed by routines like `fn_checkout_book` which modifies `book.copies_free` and inserts into `loan`, triggering `trg_loan_audit_fn` to populate `loan_audit`. Reporting views (`overdue_loans`, `member_loan_stats`) read from these core tables to provide aggregated insights.

#### Dependencies
* Internal: `01_schema.sql` is the foundation for all other components. `03_routines.sql` depends heavily on `member`, `book`, and `loan`. `04_migration_v2.sql` modifies and depends on `book` and `loan`.
* External: PostgreSQL database features (tables, views, functions, triggers).

#### Cross-cutting
* Schema Evolution: The package supports schema evolution via migration scripts (`04_migration_v2.sql`).
* Auditing: A consistent pattern of logging state changes is enforced via the `loan_audit` table and `trg_loan_audit_fn`.
* Constraints: Strict data integrity is maintained using CHECK constraints (e.g., `book_free_le_total`, `loan_due_after_loan`).
