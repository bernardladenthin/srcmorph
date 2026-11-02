### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:14:00Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages library cataloging, member borrowing, copy-level inventory, loan returns, overdue reporting, and lending-event auditing.

#### Purpose
- Defines and populates a lending-library database for authors, books, members, loans, and—after migration—branches and individual book copies.
- Supports checkout and return workflows, late-fee calculation, audit records, and lending reports.

#### Responsibilities
- **Core schema and migration:** Establishes author, book, member, and loan tables with relationship and integrity constraints; later adds branches and copy-level inventory, links members and loans to those entities, and removes book-level copy counters.
- **Loan workflows and audit:** Checks out available books to eligible members, processes returns, calculates capped late fees, and records checkout/return events in `loan_audit`.
- **Reporting:** Provides overdue-loan details, per-member borrowing statistics, and a materialized ranking of books loaned at least five times in the past year.
- **Seed data:** Inserts authors, books, members, and mixed active/returned loan history in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, inventory, and borrowing tables.
- `branch`, `book_copy` — branch locations and individually tracked copies with `AVAILABLE`, `ON_LOAN`, or `LOST` status.
- `fn_checkout_book` — rejects blocked members or unavailable inventory, creates a loan, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores book-level free-copy inventory.
- `fn_late_fee` — computes overdue days at 50 cents per day, capped at 2000 cents.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — store and capture checkout and return events.
- `overdue_loans`, `member_loan_stats`, `popular_books` — expose overdue details, member activity, and recent book popularity.
- `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id`, `idx_book_copy_book` — support lookups on authors, loans, popular books, and book copies.

#### Data flow
- Seed inserts populate `author`, `book`, `member`, and `loan`.
- `fn_checkout_book` validates the member and book availability, decrements `book.copies_free`, and inserts a loan; the audit trigger records checkout.
- `sp_return_book` validates and updates the loan, restores the book’s free-copy count, and the audit trigger records a return when `returned_on` changes from NULL.
- Views and the materialized view derive overdue, member-activity, and popularity reports from loan, member, book, and author data.
- Migration v2 creates a main branch and generates `book_copy` rows from existing book totals, assigns members to the branch, links loans to copies, and retires book-level copy counters.

#### Dependencies
- Database objects collaborate across `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, and `book_copy`.
- Foreign keys connect books to authors, loans to books and members, copies to books and branches, and members and loans to branches and copies after migration.
- PostgreSQL built-ins include `CURRENT_DATE`, `now()`, `COALESCE`, `GREATEST`, `LEAST`, and `generate_series`.

#### Cross-cutting
- Constraints enforce unique ISBNs, member emails, and copy barcodes; valid inventory counts and copy statuses; and loan due dates after checkout dates.
- Indexes target author lookup, member and open-loan lookup, popular-book identifiers, and book-copy lookup.
- Checkout and return routines raise errors for blocked members, unavailable books, unknown loans, or already-returned loans.
- The routines use book-level copy counts, while migration v2 replaces those counters with branch-aware copy records.
