### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:32:50Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL-based lending library system for managing book inventory, member registrations, loan lifecycles, and multi-branch distribution.

#### Purpose
* Manage the lifecycle of book loans, including checkout, returns, and overdue tracking.
* Maintain a relational catalog of authors, books, and library members.
* Support evolution from a centralized inventory to a multi-branch system with individual copy tracking.

#### Responsibilities
* **Core Data Modeling**: Defines the foundational schema for authors, books, members, and loans.
* **Loan Lifecycle Management**: Automates inventory updates and audit logging during checkout and return processes.
* **Reporting & Analytics**: Provides views for overdue loans, member activity statistics, and book popularity.
* **Schema Evolution**: Handles migrations to introduce physical branches and granular book copy tracking.
* **Environment Setup**: Provides seed data for development and testing.

#### Key units
* `author`, `book`, `member`, `loan`: Core tables storing the primary entities and their relationships.
* `branch`, `book_copy`: Tables introduced in v2 to manage physical locations and specific item barcodes.
* `loan_audit`: Table recording 'CHECKOUT' and 'RETURN' events for every loan.
* `fn_checkout_book`: Function that validates member status, decrements inventory, and creates loan records.
* `sp_return_book`: Procedure that marks loans as returned and increments available book copies.
* `fn_late_fee`: Function calculating monetary penalties based on days overdue.
* `overdue_loans`, `member_loan_stats`: Views for operational monitoring of late returns and member usage.
* `popular_books`: Materialized view tracking high-demand titles.
* `trg_loan_audit_fn`: Trigger that automatically populates the `loan_audit` log.

#### Data flow
1. **Checkout**: `fn_checkout_book` $\rightarrow$ validates `member` $\rightarrow$ updates `book` $\rightarrow$ inserts `loan` $\rightarrow$ triggers `loan_audit`.
2. **Return**: `sp_return_book` $\rightarrow$ updates `loan` $\rightarrow$ updates `book` $\rightarrow$ triggers `loan_audit`.
3. **Reporting**: `loan`/`member`/`book` tables $\rightarrow$ `overdue_loans` / `member_loan_stats` / `popular_books` views.
4. **Migration**: `book` (aggregate counts) $\rightarrow$ `book_copy` (individual records) $\rightarrow$ `loan` (linked to specific copy).

#### Dependencies
* **Internal**: Routines and views depend heavily on the core `loan`, `book`, and `member` tables.
* **External**: PostgreSQL / PL/pgSQL for functions, triggers, and materialized views.

#### Cross-cutting
* **Integrity Constraints**: Extensive use of `CHECK` constraints (e.g., `copies_free <= copies_total`, `due_on > loaned_on`) and `UNIQUE` constraints on ISBNs and emails.
* **Indexing Strategy**: Use of B-tree indexes for foreign keys and a partial index (`idx_loan_open`) to optimize queries for active loans.
* **Audit Pattern**: Use of a dedicated audit table (`loan_audit`) driven by database triggers to ensure a permanent record of transactions.
