### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:53:55Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> This package manages the complete lifecycle of a lending library, handling data modeling, transactional logic, reporting, and schema evolution for book and member management.

#### Purpose
* Defines and evolves the core data model for tracking authors, books, members, and loan transactions.
* Provides transactional routines and reporting views to manage lending operations and analyze library usage.

#### Responsibilities
* Schema Definition and Evolution: Establishing initial tables (`author`, `book`, `member`, `loan`) and managing structural changes via migrations (`01_schema.sql`, `04_migration_v2.sql`).
* Transactional Logic: Implementing checkout, return, and fee calculation logic via stored procedures and functions (`03_routines.sql`).
* Data Reporting: Creating views and materialized views to summarize loan activity and identify overdue items (`02_views.sql`).
* Data Initialization: Populating the database with initial reference data (`05_seed_data.sql`).

#### Key units
* `author`: Stores author details including name and birth year.
* `book`: Tracks book metadata, ISBN, and copy availability (pre-migration).
* `member`: Stores patron information, including email and block status.
* `loan`: Records individual borrowing events linking books and members.
* `loan_audit`: Logs all significant loan lifecycle events (CHECKOUT/RETURN).
* `fn_checkout_book`: Function to execute the lending process, checking member status and updating book availability.
* `sp_return_book`: Procedure to finalize a loan, updating return dates and book availability.
* `overdue_loans` (View): Reports loans past their scheduled due date.
* `member_loan_stats` (View): Summarizes total and open loans per member.
* `popular_books` (Materialized View): Tracks the most frequently loaned books over the last year.
* `branch` / `book_copy`: Entities introduced in migration to geographically track individual book copies.

#### Data flow
Data flows from initial seeding (`05_seed_data.sql`) into the core tables (`author`, `book`, `member`, `loan`). Transactions are managed by calling routines like `fn_checkout_book` and `sp_return_book`, which modify `book` and `loan` states, simultaneously logging changes to `loan_audit`. Reporting views (`02_views.sql`) read from these core tables to generate aggregated insights. Schema evolution (`04_migration_v2.sql`) modifies the structure, replacing simple book counters with granular `book_copy` tracking.

#### Dependencies
* Internal: `01_schema.sql` defines the base tables relied upon by `02_views.sql` and `03_routines.sql`.
* Internal: `04_migration_v2.sql` modifies `book`, `member`, and `loan` structures, introducing `branch` and `book_copy`.
* External: Relies on PostgreSQL features like triggers, functions, and materialized views.

#### Cross-cutting
* Schema Management: Uses sequential SQL scripts for schema definition, views, routines, and migrations.
* Data Integrity: Enforces constraints like `UNIQUE` ISBNs, `CHECK` constraints on loan dates, and foreign key relationships across all core entities.
* Auditing: The `trg_loan_audit_fn` provides automatic, event-driven logging of loan state changes into `loan_audit`.
