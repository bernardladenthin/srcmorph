### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T21:44:57Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A lending library system for managing book inventory, member registration, and the borrowing lifecycle.

#### Purpose
* Manage library inventory, member registration, and loan transactions.
* Provide reporting on overdue items, member activity, and book popularity.

#### Responsibilities
* **Core Data Modeling**: Defining relational structures for authors, books, members, and loans.
* **Loan Lifecycle Management**: Implementing business logic for checkouts, returns, and penalty calculations.
* **Reporting & Analytics**: Generating views for overdue loans, member statistics, and high-demand books.
* **Schema Evolution**: Transitioning from aggregate book counters to branch-based individual copy tracking.

#### Key units
* `author`, `book`, `member`, `loan`: Core entity tables for the library system.
* `branch`, `book_copy`: Tables introduced in v2 to support multi-branch organization and individual copy tracking.
* `loan_audit`: Table recording 'CHECKOUT' and 'RETURN' actions for loans.
* `overdue_loans`, `member_loan_stats`, `popular_books`: Views and materialized views for operational reporting.
* `fn_checkout_book`: Function to validate member status, decrement book availability, and create loan records.
* `sp_return_book`: Procedure to mark loans as returned and increment book availability.
* `fn_late_fee`: Function to calculate overdue penalties based on days past due.
* `trg_loan_audit_fn`: Trigger that automatically populates `loan_audit` on loan insertion or return.

#### Data flow
* **Checkout Process**: `fn_checkout_book` $\rightarrow$ validates `member.is_blocked` $\rightarrow$ updates `book.copies_free` $\rightarrow$ inserts `loan` $\rightarrow$ triggers `loan_audit`.
* **Return Process**: `sp_return_book` $\rightarrow$ updates `loan.returned_on` $\rightarrow$ updates `book.copies_free` $\rightarrow$ triggers `loan_audit`.
* **Reporting Pipeline**: `loan`, `member`, `book`, and `author` tables $\rightarrow$ `overdue_loans`, `member_loan_stats`, and `popular_books` views.

#### Dependencies
* Internal: Routines and views depend on the core schema (`author`, `book`, `member`, `loan`).
* External: PostgreSQL.

#### Cross-cutting
* **Auditability**: Use of `loan_audit` and triggers to track all loan state changes.
* **Data Integrity**: Extensive use of `CHECK` constraints (e.g., `copies_free <= copies_total`, `due_on > loaned_on`) and `NOT NULL` requirements.
* **Performance**: Use of indexes on foreign keys (`idx_book_author`, `idx_loan_member`) and partial indexes for open loans (`idx_loan_open`).
