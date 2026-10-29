### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T08:18:32Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages the core operations and data for a lending library system, including schema modeling, reporting, routine book management, and data migration.

#### Purpose
- Models the core schema for a lending library, including authors, books, members, and loans.
- Provides reporting views for overdue loans, member loan statistics, and popular books.
- Manages book loans, including checkout, return, late fees, and audit trails.
- Migrates the lending library schema to version 2, adding branches, managing book copies per branch, renaming a column, and retiring per-book copy counters.
- Seeds the lending library database with initial data for authors, books, and members.

#### Responsibilities
- **Schema Management**: Defines the database schema for the lending library.
- **Reporting**: Creates views and materialized views to provide insights into library operations.
- **Book Management**: Handles the checkout, return, and late fee calculations for books.
- **Data Migration**: Manages schema migrations to update the database structure.
- **Data Seeding**: Initializes the database with necessary data.

#### Key units
- **author**: Manages library authors.
- **book**: Manages library books, including their copies.
- **member**: Manages library members.
- **loan**: Manages book loans, including audit trails.
- **branch**: Manages library branches.
- **book_copy**: Manages individual copies of books across branches.
- **loan_audit**: Logs audit actions for book loans.
- **fn_checkout_book**: Function to checkout a book.
- **fn_late_fee**: Function to calculate late fees.
- **sp_return_book**: Stored procedure to return a book.
- **trg_loan_audit_fn**: Trigger to log loan audit actions.
- **overdue_loans**: View to show overdue loans.
- **member_loan_stats**: View to provide statistics on member loans.
- **popular_books**: Materialized view to show popular books.

#### Data flow
- **Inputs**: Member ID, Book ID, Loaned On, Due On, Returned On.
- **Process**: Checks if member is blocked, updates book copies, inserts loan records, calculates late fees, updates loan status.
- **Outputs**: New Loan ID, Updated Loan Record, Late Fee Amount.

#### Dependencies
- **author**: Manages authors.
- **book**: Manages books and their copies.
- **member**: Manages members.
- **loan**: Manages book loans.
- **branch**: Manages library branches.
- **book_copy**: Manages individual book copies across branches.
- **generate_series**: Used for generating series in migrations.

#### Cross-cutting
- **Shared Base Types**: `BIGSERIAL`, `VARCHAR`, `TIMESTAMPTZ`.
- **Common Exception/Handling**: Not explicitly mentioned in the summaries.
- **Threading/Concurrency**: Not explicitly mentioned in the summaries.
- **Configuration**: Not explicitly mentioned in the summaries.
