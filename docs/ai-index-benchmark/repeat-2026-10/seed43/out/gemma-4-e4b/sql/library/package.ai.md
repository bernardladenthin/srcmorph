### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:01:01Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> This package manages the complete lifecycle of a lending library, from defining core entities and tracking inventory to executing lending transactions and generating analytical reports.

#### Purpose
* Defines and manages the persistent data model for library resources, members, and lending activities.
* Implements transactional logic for book checkouts, returns, and associated fee calculations.

#### Responsibilities
* Schema Definition and Data Modeling (author, book, member, loan, branch, book_copy).
* Data Population (seeding initial authors, books, and members).
* Transactional Logic Execution (checkout, return, late fee calculation via functions/procedures).
* Reporting and Analytics (generating views for overdue loans and member statistics).
* Schema Evolution (migrations introducing branches and copy tracking).

#### Key units
* author: Defines book creators.
* book: Core entity for titles, linked to authors.
* member: Represents library patrons.
* loan: Tracks the borrowing event between a member and a book.
* branch: Defines physical library locations (introduced in migration).
* book_copy: Tracks individual, trackable copies of a book at a specific branch.
* loan_audit: Provides an immutable log of loan status changes.
* fn_checkout_book: Function handling the logic for lending a book.
* sp_return_book: Procedure handling the return of a borrowed book.
* fn_late_fee: Function calculating penalties for overdue items.
* overdue_loans (view): Reports on loans past their due date.
* member_loan_stats (view): Summarizes borrowing activity per member.
* popular_books (materialized view): Tracks frequently loaned books over time.

#### Data flow
Data flows from initial seeding (05_seed_data.sql) into the core schema (01_schema.sql). Transactions are managed by routines (03_routines.sql), which modify the `loan`, `book`, and `member` tables, logging changes to `loan_audit`. Reporting layers (02_views.sql) read from these core tables to produce aggregated views. Schema evolution (04_migration_v2.sql) modifies the structure to incorporate granular copy tracking (`book_copy`).

#### Dependencies
* Internal: `author` $\rightarrow$ `book` $\rightarrow$ `loan` $\rightarrow$ `member`.
* Internal: `book` $\rightarrow$ `book_copy` $\rightarrow$ `branch`.
* External/System: PostgreSQL features (PL/pgSQL, Triggers, Views, Materialized Views).

#### Cross-cutting
* Schema Management: Defined by `01_schema.sql` and evolved by `04_migration_v2.sql`.
* Transactional Integrity: Enforced via constraints (e.g., `book_free_le_total`) and triggers (`trg_loan_audit_fn`).
* Auditing: Centralized logging of state changes via the `loan_audit` table.
* Data Access: Views (`overdue_loans`, `member_loan_stats`) abstract complex joins across `loan`, `member`, and `book`.
