### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T00:27:15Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages lending library operations, including data management, views, and procedural logic for book checkouts, returns, and audits.

#### Purpose
- Manages lending library data and operations
- Provides reporting and analytics views
- Implements procedural logic for library functions

#### Responsibilities
- Data structure management
- Reporting and analytics
- Book lending operations
- Audit trails and logging

#### Key units
- **PostgreSQL schema (tables, indexes)**
- **Views (overdue_loans, member_loan_stats, popular_books)**
- **Functions (fn_checkout_book, fn_late_fee)**
- **Procedures (sp_return_book)**
- **Triggers (trg_loan_audit)**
- **Audit table (loan_audit)**
- **Branch management (branch, book_copy)**

#### Data flow
Inputs flow through the schema to perform operations like book checkouts and returns. Views aggregate data for reporting. Audit tables log all lending activities.

#### Dependencies
- Core tables: author, book, member, loan
- Supporting tables: branch, book_copy
- Views depend on loan, member, book, author tables
- Functions and procedures interact with loan, book, member tables
- Audit trigger depends on loan table

#### Cross-cutting
- **Shared base types**: No explicit shared types
- **Common error handling**: No specific error handling mentioned
- **Concurrency**: No explicit concurrency handling
- **Configuration**: No specific configuration details provided
