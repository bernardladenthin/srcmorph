### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:24:12Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages library catalog and membership records, book lending and returns, inventory tracking, operational auditing, and borrowing reports.

#### Purpose
- Defines a PostgreSQL lending-library database, including catalog, members, loans, checkout/return operations, and reports.
- Includes sample data and a migration from book-level copy counts to branch-based individual-copy inventory.

#### Responsibilities
- **Core schema and sample records:** `author`, `book`, `member`, and `loan` tables, constraints, indexes, and seed data.
- **Lending reports:** overdue loans, member borrowing statistics, and a materialized view of popular books.
- **Checkout, return, fees, and audit:** routines update loans and inventory; a trigger records checkout and return events in `loan_audit`.
- **Inventory migration:** adds `branch` and `book_copy`, links loans to copies and members to home branches, and removes book-level copy counters.

#### Key units
- `author`, `book`, `member`, `loan` — catalog, membership, and borrowing records.
- `overdue_loans` — reports unreturned loans past their due dates with days overdue.
- `member_loan_stats` — aggregates total and open loans and most recent loan date per member.
- `popular_books` — materialized report of books loaned at least five times in the previous 365 days.
- `fn_checkout_book` — checks member eligibility and copy availability, creates a loan, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores a book’s free-copy count.
- `fn_late_fee` — calculates overdue fees at 50 cents per day, capped at 2000 cents.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return actions through a row-level loan trigger.
- `branch`, `book_copy` — represent library locations and individually identified copies.
- `04_migration_v2.sql` — transactionally backfills existing inventory into copies and assigns members to the main branch.

#### Data flow
- Seed data populates authors, books, members, and loan history in one transaction.
- Checkout validates member and inventory state, decrements `book.copies_free`, inserts a loan, and triggers a `CHECKOUT` audit record.
- Return marks the loan returned, increments the book’s free-copy count, and triggers a `RETURN` audit record.
- Views derive overdue, member-activity, and popular-book reports from loan and catalog records.
- Migration generates `book_copy` rows from existing copy counts, assigns availability status, links loans and members to branches, and removes the legacy counters.

#### Dependencies
- Views and routines depend on `author`, `book`, `member`, and `loan`; routines additionally use `loan_audit`.
- Foreign keys connect books to authors, loans to books and members, copies to books and branches, and members to home branches.
- The migration uses PostgreSQL `generate_series`; routines use built-ins including `CURRENT_DATE`, `now()`, `COALESCE`, `GREATEST`, and `LEAST`.
- Seed records target the pre-migration schema, including `book.copies_total`, `book.copies_free`, and `member.joined_on`.

#### Cross-cutting
- PostgreSQL tables, views, materialized views, indexes, PL/pgSQL routines, and triggers.
- Schema constraints enforce unique identifiers, valid copy counts and statuses, and due dates after loan dates.
- Checkout and return routines raise exceptions for blocked members, unavailable copies, and invalid or already-returned loans.
- Migration changes run within a transaction; seed inserts are also transactional.
