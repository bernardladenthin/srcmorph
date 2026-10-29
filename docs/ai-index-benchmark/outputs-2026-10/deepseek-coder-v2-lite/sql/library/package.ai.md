### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T02:50:35Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Lending library: core schema (PostgreSQL) and reporting views for tracking overdue loans and member loan statistics, including a materialized view for popular books. Additionally, it includes routines for book checkout, return, late fee calculation, and audit trail.

#### Purpose
- Models authors, books, members, and loans in a lending library.
- Tracks book inventory, member details, and loan transactions.
- Provides reporting views for overdue loans and member loan statistics, including a materialized view for popular books.
- Includes routines for book checkout, return, late fee calculation, and audit trail.

#### Responsibilities
- **01_schema.sql**: Models authors, books, members, and loans in a lending library.
- **02_views.sql**: Provides reporting views for tracking overdue loans and member loan statistics, including a materialized view for popular books.
- **03_routines.sql**: Includes routines for book checkout, return, late fee calculation, and audit trail.
- **04_migration_v2.sql**: Adds branches, moves copies per branch, renames a column and retires the per-book copy counters.
- **05_seed_data.sql**: Seeds initial data into the database for a lending library system.

#### Key units
- **author**: Models authors with `author_id`, `full_name`, `birth_year`, and `country_code`.
- **book**: Models books with `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- **member**: Models members with `member_id`, `email`, `full_name`, `joined_on`, and `is_blocked`.
- **loan**: Tracks loan transactions with `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, and `returned_on`.
- **overdue_loans**: A view tracking loans that are overdue.
- **member_loan_stats**: A view providing statistics on member loans.
- **popular_books**: A materialized view listing books loaned multiple times within the last year.
- **fn_checkout_book**: Function for checking out a book.
- **sp_return_book**: Stored procedure for returning a book.
- **fn_late_fee**: Function for calculating late fees.
- **trg_loan_audit_fn**: Trigger for recording audit trail.

#### Data flow
- **Inputs**: Initial data from `05_seed_data.sql` into `author`, `book`, `member`, and `loan` tables.
- **Outputs**: Views `overdue_loans`, `member_loan_stats`, and materialized view `popular_books`.
- **Intermediates**: Routines `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, and `trg_loan_audit_fn` handle data manipulation.

#### Dependencies
- **Internal**: `01_schema.sql`, `02_views.sql`, `03_routines.sql`, `04_migration_v2.sql`, `05_seed_data.sql`.
- **External**: PostgreSQL database.

#### Cross-cutting
- **Shared interfaces**: SQL objects (`author`, `book`, `member`, `loan`, `overdue_loans`, `member_loan_stats`, `popular_books`, `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`).
- **Exception handling**: Routines handle exceptions and errors internally.
- **Concurrency**: Routines handle concurrent access to shared resources.
- **Configuration**: No specific configuration required beyond database setup.

This package provides a comprehensive solution for managing a lending library, including schema definition, reporting, and transactional routines, all integrated within a PostgreSQL database.
