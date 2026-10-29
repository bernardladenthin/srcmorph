### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:00:15Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL-based database system for managing a lending library's inventory, member registrations, and book loan lifecycles.

#### Purpose
* Manage the lifecycle of book loans, including checkouts, returns, and overdue penalty calculations.
* Track library inventory across authors, books, and physical copies.
* Provide reporting on member activity and book popularity.

#### Responsibilities
* **Core Data Modeling**: Defines the relational structure for authors, books, members, and loans.
* **Inventory & Loan Management**: Automates book availability updates and loan record creation via stored procedures and functions.
* **Reporting & Analytics**: Provides views for identifying overdue loans and calculating borrowing statistics.
* **Audit & Compliance**: Maintains a history of loan actions (checkout/return) via triggers.
* **Schema Evolution**: Handles migrations from aggregate book counts to a multi-branch, individual copy tracking system.

#### Key units
* `author`, `book`, `member`, `loan`: Primary tables for library entity management.
* `branch`, `book_copy`: Tables introduced in v2 to support multi-location inventory and specific copy tracking.
* `loan_audit`: Table recording all checkout and return events.
* `fn_checkout_book`: Function to validate member status, decrement inventory, and create a loan.
* `sp_return_book`: Procedure to mark loans as returned and increment inventory.
* `fn_late_fee`: Function to calculate monetary penalties for overdue books.
* `trg_loan_audit_fn`: Trigger that automatically logs loan lifecycle changes to `loan_audit`.
* `overdue_loans`, `member_loan_stats`: Views for operational reporting.
* `popular_books`: Materialized view for tracking high-demand titles.

#### Data flow
1. **Checkout**: `fn_checkout_book` $\rightarrow$ validates `member` $\rightarrow$ updates `book` $\rightarrow$ inserts `loan` $\rightarrow$ triggers `trg_loan_audit_fn` $\rightarrow$ inserts `loan_audit`.
2. **Return**: `sp_return_book` $\rightarrow$ updates `loan` $\rightarrow$ updates `book` $\rightarrow$ triggers `trg_loan_audit_fn` $\rightarrow$ inserts `loan_audit`.
3. **Reporting**: `loan`, `member`, `book`, and `author` tables $\rightarrow$ aggregated via Views/Materialized Views $\rightarrow$ output for overdue or popularity reports.

#### Dependencies
* **Internal**: All routines and views depend on the core schema (`author`, `book`, `member`, `loan`).
* **External**: PostgreSQL / PL/pgSQL.

#### Cross-cutting
* **Constraints**: Strict data integrity via `CHECK` constraints (e.g., birth year ranges, `copies_free <= copies_total`, and `due_on > loaned_on`).
* **Indexing**: Performance optimization using B-tree indexes on foreign keys and a partial index on open loans (`returned_on IS NULL`).
* **Migration Pattern**: Transition from aggregate counters (`copies_total`) to a normalized `book_copy` entity linked to a `branch`.
