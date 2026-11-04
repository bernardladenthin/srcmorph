### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:50:49Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Supports library cataloging and member borrowing, tracking loan availability, returns, overdue activity, and branch-level book copies.

#### Purpose
- Defines a PostgreSQL lending-library database for authors, books, members, loans, and borrowing reports.
- Provides checkout, return, late-fee, audit, migration, and sample-data operations.

#### Responsibilities
- **Catalog and lending schema:** `author`, `book`, `member`, and `loan` tables with keys, constraints, and indexes.
- **Borrowing reports:** overdue-loan and member-activity views, plus a materialized ranking of frequently borrowed books.
- **Loan workflows and audit:** checkout and return routines, late-fee calculation, and trigger-based `loan_audit` records.
- **Branch inventory migration:** adds `branch` and `book_copy`, links loans and members to copies and branches, and replaces book-level copy counters.
- **Sample data:** transactionally seeds authors, books, members, and historical loans.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and borrowing records.
- `overdue_loans` — lists open loans past their due dates with member and book details.
- `member_loan_stats` — aggregates total and open loans and most recent loan date per member.
- `popular_books` — materialized list of books loaned at least five times in the preceding 365 days.
- `fn_checkout_book` — validates member and copy availability, creates a loan, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores book availability.
- `fn_late_fee` — computes a capped fee of 50 cents per overdue day.
- `loan_audit` / `trg_loan_audit_fn` / `trg_loan_audit` — record checkout and return events.
- `branch` / `book_copy` — represent library locations and individually identified copies.
- `04_migration_v2.sql` — migrates from book-level inventory counts to branch-specific copy records.

#### Data flow
- `05_seed_data.sql` populates the core catalog, membership, and loan tables with deterministic records.
- `fn_checkout_book` checks member status and availability, updates inventory, and inserts a loan; the loan trigger records checkout.
- `sp_return_book` updates the loan and inventory; the trigger records a return when `returned_on` changes from NULL.
- Views derive overdue and member statistics from loan and member records; `popular_books` materializes recent borrowing counts.
- The v2 migration creates a default branch and generates `book_copy` records from existing book counts, then assigns members a home branch and adds loan copy references.

#### Dependencies
- Views and routines depend on the core `loan`, `member`, `book`, and `author` tables.
- Checkout and return workflows update `book` and `loan`; audit trigger writes to `loan_audit`.
- Migration depends on existing `book`, `loan`, and `member` data, and PostgreSQL `generate_series`.
- Seed data targets `author`, `book`, `member`, and `loan`.

#### Cross-cutting
- Primary keys, foreign keys, uniqueness, checks, and indexes enforce identifiers, relationships, inventory bounds, valid loan dates, and report access.
- The migration drops `book.copies_free` and `book.copies_total`; checkout and return routines are summarized as reading or updating `book.copies_free`, so the summaries describe differing inventory schemas.
- Late-fee logic uses `CURRENT_DATE` when no return date is supplied and caps the result at 2000 cents.
