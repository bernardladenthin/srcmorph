### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:17:13Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Defines a lending-library database for cataloging books and members, managing loans and returns, reporting borrowing activity, and evolving inventory from book-level counts to branch-assigned copies.

#### Purpose
- Provides PostgreSQL schema, lending operations, reports, migration, and deterministic sample data for a library.
- Tracks authors, books, members, loans, availability, returns, and audit events.

#### Responsibilities
- **Catalog and lending schema:** `author`, `book`, `member`, and `loan` tables with keys, constraints, and indexes.
- **Operational workflows:** checkout of available books, return processing, inventory updates, loan audit events, and late-fee calculation.
- **Reporting:** overdue loans, member borrowing statistics, and a materialized ranking of frequently loaned books.
- **Inventory migration:** adds branches and individual `book_copy` records, links loans and members to copies and branches, and removes book-level copy counters.
- **Development data:** seeds authors, books, members, and loan histories in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, account, and borrowing records.
- `overdue_loans` — view of unreturned loans past their due date, including days overdue.
- `member_loan_stats` — per-member totals, open-loan counts, and most recent loan date.
- `popular_books` — materialized view of books loaned at least five times in the preceding 365 days.
- `fn_checkout_book` — validates member and book availability, creates a loan, and decrements free-copy inventory.
- `sp_return_book` — marks an open loan returned and restores its book’s free-copy count.
- `fn_late_fee` — calculates overdue charges at 50 cents per day, capped at 2000.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return events through a loan trigger.
- `branch`, `book_copy` — migration-era branch and individually identified inventory records.

#### Data flow
- Seed data populates `author`, `book`, `member`, and `loan`; views derive reporting results from these tables.
- Checkout checks member status and book availability, inserts a loan, and updates inventory; the trigger records checkout activity.
- Return processing updates the loan and inventory; the trigger records a return when `returned_on` changes from NULL.
- The migration creates a default branch and generates `book_copy` rows from existing book copy counts, then links loans and members to copies and branches.

#### Dependencies
- Views and routines depend on `author`, `book`, `member`, and `loan`; routines additionally use `loan_audit`.
- The migration depends on existing `book`, `loan`, and `member` tables and PostgreSQL `generate_series`.
- `book_copy` references `book` and `branch`; migrated `loan.copy_id` and `member.home_branch_id` reference those inventory and branch records.
- Reporting and operational logic uses PostgreSQL date functions and built-ins including `CURRENT_DATE`, `now()`, `COALESCE`, `GREATEST`, and `LEAST`.

#### Cross-cutting
- PostgreSQL constraints enforce identifiers, uniqueness, valid counts, loan deadlines, and allowed copy and audit statuses.
- The initial schema tracks availability with `book.copies_total` and `book.copies_free`; migration replaces these counters with branch-assigned `book_copy` records.
- The seed and migration scripts use transactions; the migration is explicitly bounded by `BEGIN` and `COMMIT`.
