### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:08:58Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A relational database system for managing library inventory, member circulation, multi-branch operations, and automated loan lifecycles.

#### Purpose
* Provides a complete schema for tracking authors, books, members, and loan transactions.
* Supports evolving from a single-location model to a multi-branch system with individual item tracking.

#### Responsibilities
* **Core Inventory & Membership**: Manages the foundational data for authors, books, and library members.
* **Circulation Management**: Automates the checkout and return processes, including availability updates and late fee calculations.
* **Reporting & Analytics**: Aggregates data for overdue loan tracking, member activity statistics, and book popularity trends.
* **Audit & Compliance**: Maintains a historical log of all loan actions via automated triggers.
* **Schema Evolution**: Handles migrations from aggregate book counters to granular, branch-specific book copy tracking.

#### Key units
* `author`: Stores biographical information for book creators.
* `book`: Contains bibliographic metadata (ISBN, title, author link).
* `book_copy`: Represents individual physical items assigned to specific branches with unique barcodes.
* `branch`: Defines library locations and cities.
* `member`: Tracks user profiles, membership status, and home branch affiliation.
* `loan`: Records the transaction history between members and specific book copies.
* `loan_audit`: An automated log of 'CHECKOUT' and 'RETURN' actions.
* `fn_checkout_book`: Validates member status and manages book availability during checkout.
* `sp_return_book`: Processes book returns and updates inventory status.
* `fn_late_fee`: Calculates monetary penalties based on overdue days.
* `overdue_loans`: A view identifying delinquent loans and calculated days overdue.
* `popular_books`: A materialized view tracking high-demand titles.

#### Data flow
* **Checkout Pipeline**: `fn_checkout_book` validates a `member`, checks `book_copy` availability, creates a `loan` record, and triggers a `loan_audit` entry.
* **Return Pipeline**: `sp_return_book` updates the `loan` record with a return date, updates the `book_copy` status, and triggers a `loan_audit` entry.
* **Reporting Pipeline**: Operational views (`overdue_loans`, `member_loan_stats`) aggregate data from `loan`, `member`, and `book` tables for real-time monitoring.

#### Dependencies
* **Internal**: Relies on a hierarchical relationship between `author` -> `book` -> `book_copy` and `branch` -> `member`/`book_copy`.
* **External**: Uses PostgreSQL-specific features including `BIGSERIAL`, `PL/pgSQL` routines, materialized views, and triggers.

#### Cross-cutting
* **Automated Auditing**: Uses `trg_loan_audit_fn` to ensure every change to a loan's lifecycle is recorded in `loan_audit`.
* **Inventory Integrity**: Employs constraints (e.g., `book_free_le_total`) and status checks (`AVAILABLE`, `ON_LOAN`, `LOST`) to maintain data consistency.
* **Migration Logic**: Uses `generate_series` and pattern-based string construction (e.g., `isbn-n`) to transform legacy aggregate data into normalized relational records.
