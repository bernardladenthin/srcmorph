### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:00:12Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages library catalog and lending operations, including member checkout and returns, inventory, overdue reporting, fees, and loan auditing.

#### Purpose
- Defines and populates the PostgreSQL data model for authors, books, members, loans, branches, and individual copies.
- Supports checkout and return workflows, operational reporting, and development/test data.

#### Responsibilities
- **Catalog and lending schema:** `author`, `book`, `member`, and `loan` tables, with keys, validation constraints, and lookup indexes.
- **Inventory evolution:** migration adds `branch` and barcode-tracked `book_copy` records, links loans to copies and members to home branches, and replaces book-level copy counters.
- **Checkout, returns, and auditing:** routines validate checkout eligibility, record returns, calculate late fees, and audit checkout and return events.
- **Lending reports:** views identify overdue loans, aggregate member activity, and rank books loaned in the prior year.
- **Seed data:** transaction populates authors, books, members, and loans for development and testing.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, member, and lending tables.
- `branch`, `book_copy` — branch directory and copy-level inventory introduced by `04_migration_v2.sql`.
- `overdue_loans` — lists unreturned loans past their due dates with member and book details.
- `member_loan_stats` — summarizes each member’s total and open loans and most recent loan date.
- `popular_books` — materialized ranking of books loaned in the past year.
- `fn_checkout_book` — checks member and copy availability, creates a loan, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores book-level availability.
- `fn_late_fee` — computes overdue charges at 50 cents per day, with a zero floor and 2000-cent cap.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — store and record checkout and return events.

#### Data flow
- Seed records populate authors, books, members, and loans; book-author and loan-member/book foreign keys connect the catalog and lending records.
- `fn_checkout_book` checks member blocking and available copies, decrements `book.copies_free`, inserts a loan, and returns its ID; the loan trigger records checkout.
- `sp_return_book` marks an open loan returned, restores `book.copies_free`, and the trigger records the return transition.
- `fn_late_fee` uses due and return dates, or the current date for an open loan, to calculate a capped fee.
- Reporting views derive overdue loans and member activity from `loan`, `member`, and `book`; `popular_books` aggregates recent loan activity with book and author details.
- The migration generates `book_copy` rows from existing book copy totals, assigns them to the main branch, and adds copy and home-branch references.

#### Dependencies
- PostgreSQL tables `author`, `book`, `member`, and `loan` underpin routines and reports.
- `book_copy` references `book` and `branch`; migrated `loan.copy_id` references `book_copy`, and `member.home_branch_id` references `branch`.
- `loan_audit` stores loan identifiers and actions but has no declared foreign key to `loan`.

#### Cross-cutting
- Primary keys, foreign keys, uniqueness and CHECK constraints enforce identity, relationships, and valid values; indexes support author, member-loan, open-loan, popular-book, and copy lookups.
- Checkout and return routines raise exceptions for blocked or unavailable checkout, unknown loans, and already-returned loans.
- `trg_loan_audit` fires after loan insertion or `returned_on` updates; it records checkout inserts and NULL-to-non-NULL return transitions.
- `fn_late_fee` is an immutable SQL function; other routines and the reporting views are PostgreSQL objects.
