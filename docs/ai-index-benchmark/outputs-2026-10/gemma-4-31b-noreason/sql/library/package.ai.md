### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T09:55:53Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL-based lending library system for managing book inventories, member registrations, loan lifecycles, and multi-branch distribution.

#### Purpose
* Maintain a relational registry of authors, books, and members.
* Automate the borrowing and return process including inventory tracking and penalty calculations.

#### Responsibilities
* **Core Data Modeling**: Defines the foundational schema for authors, books, members, and loans.
* **Loan Lifecycle Management**: Handles checkouts, returns, and audit logging via stored procedures and triggers.
* **Reporting & Analytics**: Provides views for overdue loans, member statistics, and book popularity.
* **Schema Evolution**: Manages the transition from aggregate book counts to a granular branch-and-copy system.
* **Environment Initialization**: Seeds the database with baseline test data.

#### Key units
* `author`, `book`, `member`, `loan`: Core entity tables for library operations.
* `branch`, `book_copy`: Tables introduced in v2 to support physical locations and unique barcodes.
* `loan_audit`: Table recording 'CHECKOUT' and 'RETURN' events.
* `fn_checkout_book`: Function to validate member status, decrement inventory, and create loans.
* `sp_return_book`: Procedure to mark loans as returned and increment book availability.
* `fn_late_fee`: Function to calculate financial penalties based on overdue days.
* `overdue_loans`, `member_loan_stats`, `popular_books`: Views for operational reporting.
* `trg_loan_audit_fn`: Trigger automating the population of the `loan_audit` log.

#### Data flow
1. **Checkout**: `fn_checkout_book` $\rightarrow$ validates `member` $\rightarrow$ updates `book` $\rightarrow$ inserts `loan` $\rightarrow$ triggers `loan_audit`.
2. **Return**: `sp_return_book` $\rightarrow$ updates `loan` $\rightarrow$ updates `book` $\rightarrow$ triggers `loan_audit`.
3. **Reporting**: `loan`, `member`, `book`, and `author` tables $\rightarrow$ aggregated into `overdue_loans` and `popular_books` views.

#### Dependencies
* **Internal**: Routines and views depend on the core schema tables (`loan`, `book`, `member`, `author`).
* **External**: PostgreSQL (PL/pgSQL).

#### Cross-cutting
* **Integrity Constraints**: Use of `CHECK` constraints for birth years, copy counts, and loan dates.
* **Indexing**: Performance optimization via B-tree indexes on foreign keys and a partial index for open loans.
* **Audit Pattern**: Automated state tracking using a dedicated audit table and database triggers.
* **Migration Strategy**: Transition from aggregate counters (`copies_free`) to individual entity tracking (`book_copy`).
