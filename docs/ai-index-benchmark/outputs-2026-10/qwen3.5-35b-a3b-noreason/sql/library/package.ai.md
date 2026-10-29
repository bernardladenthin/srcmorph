### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T08:58:10Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages the complete data lifecycle of a multi-branch lending library system, from core inventory modeling and transactional logic to reporting analytics and schema evolution.

#### Purpose
- Defines the relational schema, business logic routines, and reporting views for tracking library inventory, members, and loan transactions.
- Orchestrates schema migrations to transition from global copy counts to granular, branch-specific physical copy tracking.

#### Responsibilities
- **Data Modeling**: Establishes core entities (`author`, `book`, `member`, `loan`) and enforces inventory constraints and referential integrity.
- **Operational Logic**: Implements stored procedures and functions for book checkout, returns, late fee calculations, and audit logging.
- **Reporting**: Provides read-only views for overdue loans, member statistics, and popular book trends via materialized views.
- **Schema Evolution**: Executes migration scripts to introduce multi-branch support, `book_copy` granularity, and legacy column retirement.
- **Initialization**: Seeds the database with initial authors, books, members, and loan records for testing environments.

#### Key units
- `author`: Stores author metadata including name, birth year, and country code.
- `book`: Defines book metadata with ISBN, title, and legacy copy count constraints.
- `member`: Tracks member profiles, email, join date, and block status.
- `loan`: Records borrowing transactions linking members to books with due and return dates.
- `branch`: Represents physical library locations introduced in schema v2.
- `book_copy`: Tracks individual physical inventory items with status and branch assignment.
- `loan_audit`: Logs chronological actions (CHECKOUT/RETURN) for transactional history.
- `fn_checkout_book`: Validates member status and decrements availability before creating a loan.
- `sp_return_book`: Updates loan return dates and increments book availability.
- `fn_late_fee`: Calculates overdue penalties capped at a maximum threshold.
- `overdue_loans`: View aggregating unreturned items past their due date.
- `member_loan_stats`: View summarizing total, open, and recent loan activity per member.
- `popular_books`: Materialized view ranking books by loan frequency over the last year.
- `04_migration_v2`: Script expanding global counts to `book_copy` rows and linking loans to specific copies.

#### Data flow
- **Initialization**: `05_seed_data.sql` populates base tables (`author`, `book`, `member`) with static records.
- **Transaction Execution**: `fn_checkout_book` reads `member` and `book` availability, writes to `loan`, and updates `book` counts; `sp_return_book` reverses this by updating `loan` and `book`.
- **Audit Trail**: `trg_loan_audit` triggers on `loan` inserts and updates to record actions in `loan_audit`.
- **Reporting**: Views (`overdue_loans`, `member_loan_stats`) read from `loan`, `member`, `book`, and `author` to aggregate statistics; `popular_books` materializes frequent loan patterns.
- **Schema Migration**: `04_migration_v2.sql` reads legacy `book` counts, generates `book_copy` rows, alters `loan` and `member` tables to reference new entities, and drops obsolete columns.

#### Dependencies
- **Internal**: `loan` depends on `book` and `member`; `loan_audit` depends on `loan`; views depend on all core tables.
- **External**: `generate_series` used in migration scripts to expand copy counts.
- **Tables**: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.

#### Cross-cutting
- **Constraint Enforcement**: Uses `CHECK` constraints for inventory logic (`copies_free <= copies_total`), date validity (`due_on > loaned_on`), and status values.
- **Concurrency**: Relies on PostgreSQL transactional integrity for checkout/return operations and migration scripts.
- **Auditability**: Automatic logging via triggers (`trg_loan_audit`) ensures every state change in `loan` is recorded.
- **Versioning**: Schema evolves from single-branch global counts to multi-branch specific copies via explicit migration scripts.
