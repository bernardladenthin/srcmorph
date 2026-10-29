### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T22:22:30Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages the full lifecycle of a lending library system, from data modeling and reporting to book checkout, return, and overdue fee calculation.

#### Purpose
- Defines and maintains the core data model for library operations
- Provides tools for managing book loans, member accounts, and overdue items
- Supports reporting and analytics for library performance and usage

#### Responsibilities
- Core data modeling and integrity enforcement for authors, books, members, and loans
- Loan processing including checkouts, returns, and late fee calculations
- Reporting and analytics views for overdue items, member activity, and book popularity
- Schema migration and data seeding for library system initialization

#### Key units
- `author` table: stores author information including name, birth year, and country
- `book` table: manages book details such as ISBN, title, author, and copy counts
- `member` table: tracks library members' contact info, membership status, and borrowing history
- `loan` table: records borrowing transactions with due dates and return status
- `overdue_loans` view: identifies loans past their due date for reporting
- `member_loan_stats` view: summarizes member loan activity and history
- `popular_books` materialized view: ranks books by borrowing frequency
- `fn_checkout_book` function: processes book checkout with availability checks
- `sp_return_book` procedure: handles book return and updates inventory
- `fn_late_fee` function: calculates late fees for overdue loans
- `branch` table: supports multi-branch library operations
- `book_copy` table: tracks individual book copies across library branches

#### Data flow
Members request books through checkout functions, which validate availability and update book copy counts. Loans are recorded in the loan table, and returns update copy counts and mark loans as completed. Overdue items are tracked through views that join loan, member, and book data. Late fees are calculated based on return dates and due dates, and all loan actions are audited.

#### Dependencies
- Internal: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`
- External: `CURRENT_DATE`, `INTERVAL '365 days'`, `generate_series`
- Functions depend on `member`, `book`, `loan`, `loan_audit` tables
- Views depend on `loan`, `member`, `book`, `author` tables
- Migration script depends on `book`, `branch`, `loan`, `member` tables

#### Cross-cutting
- Data integrity enforced through foreign key constraints and check constraints
- Audit logging via `loan_audit` table for all loan actions
- Consistent date handling with `CURRENT_DATE` and `TIMESTAMPTZ` types
- Multi-branch support introduced through `branch` and `book_copy` tables
- Schema evolution patterns with migration scripts for version control
- Standardized naming conventions for tables, views, and functions
