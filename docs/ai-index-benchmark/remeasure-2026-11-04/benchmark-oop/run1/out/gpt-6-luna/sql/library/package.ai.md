### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:23:39Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Supports library cataloging, branch inventory, member borrowing, loan reporting, returns, late-fee calculation, and audit tracking.

#### Purpose
- Defines and evolves a PostgreSQL lending-library database for books, authors, members, loans, and branch-specific copies.
- Provides operational routines, reporting views, and sample catalog and loan data.

#### Responsibilities
- **Core schema and migration:** Creates `author`, `book`, `member`, and `loan`; adds `branch` and `book_copy` inventory, assigns legacy copies to the Main branch, and updates member and loan relationships.
- **Loan operations and audit:** Checks out available books, processes returns, calculates capped late fees, and records checkout and return events.
- **Reporting:** Exposes overdue loans and member borrowing statistics; materializes frequently loaned books from recent loan history.
- **Sample data:** Seeds authors, books, members, and loan history in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and borrowing records.
- `branch`, `book_copy` — branch locations and individually identified copies with availability status.
- `overdue_loans` — reports unreturned loans past their due dates.
- `member_loan_stats` — aggregates member loan totals, open loans, and most recent loan date.
- `popular_books` — materialized report of books loaned at least five times in the last 365 days.
- `fn_checkout_book` — validates membership and availability, records a loan, and returns its ID.
- `sp_return_book` — marks an eligible loan returned and restores a copy.
- `fn_late_fee` — calculates overdue charges at 50 cents per day, capped at 2000.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — store and capture checkout and return events.
- `04_migration_v2.sql` — migrates book-level copy counts into branch-specific `book_copy` records and removes the old counters.

#### Data flow
- Book, author, member, and loan records form the base for reporting views and `popular_books`.
- Checkout validates member and book state, inserts a loan, and decrements the available-copy count; the loan trigger records a `CHECKOUT` audit event.
- Return updates an open loan and restores a copy; the trigger records a `RETURN` event on the first transition to returned.
- Migration generates one `book_copy` per legacy total copy, assigning `AVAILABLE` or `ON_LOAN` based on the prior free-copy count, and links members and loans to branches and copies.
- Seed inserts populate the core tables with sample catalog, member, and loan records.

#### Dependencies
- Views and routines read or update `author`, `book`, `member`, and `loan`.
- The migration adds relationships from `book_copy` to `book` and `branch`, from `loan` to `book_copy`, and from `member` to `branch`.
- PostgreSQL built-ins include `CURRENT_DATE`, `now()`, `COALESCE`, `LEAST`, `GREATEST`, and `generate_series`.

#### Cross-cutting
- Referential integrity connects books to authors and loans to books and members; deleting a member cascades to their loans.
- Constraints enforce valid copy counts, loan due dates, member and book identifiers, and allowed `book_copy.status` and audit action values.
- `idx_loan_open` supports open-loan due-date access; `idx_book_author`, `idx_loan_member`, and `idx_book_copy_book` index common relationships; `popular_books` has a unique index on `book_id`.
- The migration renames `member.joined_on` to `member_since` and retires `book.copies_total` and `book.copies_free` in favor of individual copy records.
