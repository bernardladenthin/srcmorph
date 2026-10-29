### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T04:39:31Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> This package provides a relational database schema and business logic for a multi-branch lending library system, managing inventory, member accounts, and loan lifecycles.

#### Purpose
* Defines the core data model and operational logic for a library management system.
* Manages book inventory, member status, and automated loan tracking across multiple physical locations.

#### Responsibilities
* **Core Schema Management**: Defines entities for authors, books, members, and loan records.
* **Inventory & Branching**: Manages physical book copies (`book_copy`) across different locations (`branch`).
* **Business Logic & Automation**: Handles checkouts, returns, late fee calculations, and automated audit logging via stored procedures and triggers.
* **Reporting & Analytics**: Provides views for tracking overdue items, member statistics, and popular book trends.
* **Data Seeding**: Provides initial datasets for development and testing.

#### Key units
* `author`, `book`, `member`, `loan`: Core entities for library assets and users.
* `branch`, `book_copy`: Entities for multi-location inventory management.
* `loan_audit`: Table for tracking status changes of book loans.
* `overdue_loans`, `member_loan_stats`, `popular_books`: Reporting views for operational insights.
* `fn_checkout_book`: Procedure to validate members and decrement inventory.
* `sp_return_book`: Procedure to update loan status and increment inventory.
* `fn_late_fee`: Function to calculate capped daily late fees.
* `trg_loan_audit_fn`: Trigger to automatically log checkout and return actions.

#### Data flow
1. **Checkout**: `fn_checkout_book` validates `member` status, updates `book` (or `book_copy`) availability, creates a `loan` record, and triggers `loan_audit`.
2. **Return**: `sp_return_book` updates the `loan` record, restores `book` availability, and triggers `loan_audit`.
3. **Reporting**: Views aggregate data from `loan`, `member`, `book`, and `author` to produce real-time statistics.

#### Dependencies
* Internal: `loan` depends on `book` and `member`; `book_copy` depends on `book` and `branch`.
* External: PostgreSQL (SQL dialect).

#### Cross-cutting
* **Audit Trail**: Automated logging of all loan state changes via `loan_audit`.
* **Constraints**: Strict validation on `copies_free` vs `copies_total` and `due_on` dates.
* **Indexing**: Optimized lookups for member loans, open loans, and popular book IDs.
* **Migration Path**: Transition from aggregate book counts to individual `book_copy` tracking per `branch`.
