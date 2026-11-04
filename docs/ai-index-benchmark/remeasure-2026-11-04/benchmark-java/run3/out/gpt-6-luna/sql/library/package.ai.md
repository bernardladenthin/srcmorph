### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:46:54Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Defines a lending-library database for cataloging books and authors, managing members and loans, reporting borrowing activity, and evolving inventory to branch-level copies.

#### Purpose
- Store and enforce library catalog, membership, loan, and inventory data.
- Support checkout, returns, overdue reporting, late-fee calculation, audit history, and sample-data setup.

#### Responsibilities
- **Core schema — `01_schema.sql`:** Defines `author`, `book`, `member`, and `loan`, with keys, constraints, relationships, and indexes.
- **Reporting — `02_views.sql`:** Exposes overdue loans and member borrowing statistics; materializes popular-book counts.
- **Loan operations and audit — `03_routines.sql`:** Checks out available books for eligible members, processes returns, calculates capped late fees, and records checkout/return audit events.
- **Branch and copy migration — `04_migration_v2.sql`:** Adds branches and individually tracked `book_copy` inventory, assigns existing copies to the main branch, adds member home branches and loan copy references, and removes book-level copy counters.
- **Development/test data — `05_seed_data.sql`:** Inserts sample authors, books, members, and loans in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and lending tables.
- `overdue_loans` — view of unreturned loans past due, including `days_overdue`.
- `member_loan_stats` — per-member loan totals, open-loan counts, and most recent loan date.
- `popular_books` — materialized counts of books loaned in the past year.
- `fn_checkout_book` — validates member and book availability, creates a loan, and decrements free copies.
- `sp_return_book` — marks an open loan returned and restores a free-copy count.
- `fn_late_fee` — computes overdue charges at 50 cents per day, floored at zero and capped at 2000 cents.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and first-return events.
- `branch`, `book_copy` — branch locations and individually identified copies with availability status.

#### Data flow
- Seed records populate `author`, `book`, `member`, and `loan`, linked by their foreign-key relationships.
- Checkout checks member blocking and book availability, updates `book.copies_free`, inserts a `loan`, and triggers checkout auditing.
- Return updates an open loan and restores book availability; the trigger audits the first transition to returned.
- Views derive overdue, member-activity, and popular-book reports from loan, member, book, and author data.
- The v2 migration creates a main branch and generates `book_copy` rows from existing `book.copies_total`, then adds branch/copy references and removes legacy copy counters.

#### Dependencies
- Reports depend on `loan`, `member`, `book`, and `author`; `idx_popular_books_id` indexes `popular_books`.
- Loan routines depend on `member`, `book`, and `loan`; audit routines write to `loan_audit`.
- Migration depends on the existing `book`, `loan`, and `member` tables and PostgreSQL `generate_series`.
- Schema and migration use PostgreSQL keys, checks, defaults, indexes, and foreign-key relationships.

#### Cross-cutting
- PostgreSQL throughout; primary keys, uniqueness, foreign keys, checks, and indexes enforce data integrity and query support.
- Checkout and return failures raise exceptions for blocked members, unavailable copies, unknown loans, or already-returned loans.
- The migration changes inventory representation from aggregate book copy counts to branch-associated individual copies; the initial routines and seed data refer to the pre-migration counters and columns.
