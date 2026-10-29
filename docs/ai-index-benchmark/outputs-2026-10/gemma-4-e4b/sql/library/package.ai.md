### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T00:00:41Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> This package manages the complete lifecycle of a lending library, handling data modeling, transaction processing, reporting, and schema evolution for books, members, and loans.

#### Purpose
* Defines and evolves the core relational data model for library assets (books, authors, members) and transactions (loans).
* Provides the operational logic (routines, views) necessary to manage lending workflows, including checkouts, returns, and fee calculation.

#### Responsibilities
* Data Modeling and Schema Definition: Defining core entities like `author`, `book`, `member`, and `loan` in `01_schema.sql`.
* Schema Evolution: Managing structural changes, such as introducing `branch` and `book_copy` entities and modifying existing tables in `04_migration_v2.sql`.
* Transactional Logic: Implementing business rules for lending via functions (`fn_checkout_book`) and procedures (`sp_return_book`) in `03_routines.sql`.
* Auditing: Tracking all state changes within the lending process using the `loan_audit` table and associated triggers in `03_routines.sql`.
* Reporting and Analytics: Generating summary and status reports using views like `overdue_loans` and materialized views like `popular_books` in `02_views.sql`.
* Initialization: Populating the database with baseline data using `05_seed_data.sql`.

#### Key units
* `author`, `book`, `member`, `loan`: Core entities defining library participants and items.
* `branch`, `book_copy`: Entities introduced in migrations to handle physical location and individual item tracking.
* `loan_audit`: Table logging the lifecycle events (CHECKOUT, RETURN) of a loan.
* `fn_checkout_book`: Function handling the logic for initiating a book checkout, including availability checks.
* `sp_return_book`: Procedure managing the return process, updating loan status and inventory.
* `overdue_loans` (View): Reports loans past their due date.
* `member_loan_stats` (View): Summarizes borrowing activity per member.
* `popular_books` (Materialized View): Tracks frequently loaned books over a period.

#### Data flow
Data flows from initial seeding (`05_seed_data.sql`) into the core schema (`01_schema.sql`). Operational workflows are driven by routines (`03_routines.sql`), which read from and write to the core tables (`member`, `book`, `loan`). These transactions are logged in `loan_audit`. Reporting layers (`02_views.sql`) then read the state of these core tables to provide aggregated insights. Migrations (`04_migration_v2.sql`) modify the structure upon which this flow operates.

#### Dependencies
* Internal: `01_schema.sql` is the foundation for all other components. `03_routines.sql` depends heavily on `member`, `book`, and `loan`. `02_views.sql` depends on `loan`, `member`, `book`, and `author`. `04_migration_v2.sql` modifies `book`, `member`, and `loan`.
* External: PostgreSQL database features (PL/pgSQL, triggers, materialized views).

#### Cross-cutting
* Schema Management: Utilizes sequential SQL scripts for schema definition, migration (`04_migration_v2.sql`), and data seeding (`05_seed_data.sql`).
* Transactional Integrity: Relies on database constraints (e.g., `CHECK`, `FOREIGN KEY`) and triggers (`trg_loan_audit_fn`) to maintain data consistency during lending operations.
* Data Modeling Evolution: The package demonstrates a pattern of evolving the data model from simple counters (`copies_total`/`copies_free`) to granular tracking (`book_copy` status) via migration scripts.
