### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:00:20Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalog, member accounts, book inventory, loan circulation, operational reporting, and checkout/return auditing.

#### Purpose
- Defines and evolves the PostgreSQL data model for authors, books, members, branches, physical copies, and loans.
- Supports circulation operations, loan reporting, late-fee calculation, audit events, and sample-data setup.

#### Responsibilities
- **Catalog, membership, and circulation schema:** `author`, `book`, `member`, and `loan` tables with identity keys, constraints, relationships, and lookup indexes.
- **Loan reporting:** `overdue_loans` finds unreturned past-due loans; `member_loan_stats` summarizes borrowing; `popular_books` reports books loaned at least five times in the prior 365 days.
- **Checkout, return, fees, and auditing:** validates member and copy availability, creates and closes loans, updates availability, calculates capped late fees, and records checkout/return audit events.
- **Branch-level inventory migration:** adds `branch` and `book_copy`, assigns generated copies to a main branch, links loans to copies and members to home branches, and replaces book-level copy counts.
- **Sample data:** inserts authors, books, members, and loan histories in a transaction.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and circulation tables.
- `overdue_loans` — view of unreturned loans past their due dates, including days overdue.
- `member_loan_stats` — view aggregating total/open loans and latest loan date per member.
- `popular_books` — materialized view of frequently loaned books with author names; uniquely indexed by `book_id`.
- `fn_checkout_book` — checks member and book availability, creates a loan with a due date, and returns its ID.
- `sp_return_book` — marks an open loan returned and restores book availability.
- `fn_late_fee` — calculates nonnegative overdue charges at 50 cents per day, capped at 2000 cents.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — stores and records checkout and first-return events.
- `branch`, `book_copy` — migration-added branch and individually identified inventory records.
- `04_migration_v2.sql` — converts inventory tracking from book-level counts to branch-level copies and updates member/loan references.

#### Data flow
- Seed inserts populate `author`, `book`, `member`, and `loan` records.
- Checkout validates a member and available book, decrements `book.copies_free`, inserts a `loan`, and triggers a `CHECKOUT` audit record.
- Return updates `loan.returned_on`, restores the book’s free-copy count, and triggers a `RETURN` audit record on the first transition from unreturned.
- Reporting views derive overdue loans, member borrowing statistics, and popular books from `loan`, `member`, `book`, and `author`.
- Migration generates `book_copy` rows from existing `book` copy counts, assigns them to the `Main` branch, and adds copy and home-branch references.

#### Dependencies
- Internal relationships connect `book.author_id` to `author`, loan records to `book` and `member`, and audit events to loans by `loan_id`.
- Views read from `loan`, `member`, `book`, and `author`; routines operate on `member`, `book`, `loan`, and `loan_audit`.
- The migration adds `book_copy.book_id -> book`, `book_copy.branch_id -> branch`, `loan.copy_id -> book_copy`, and `member.home_branch_id -> branch`.
- PostgreSQL built-ins include `CURRENT_DATE`, `now()`, and `generate_series`.

#### Cross-cutting
- PostgreSQL constraints enforce identifiers, unique ISBNs/emails/barcodes, valid copy counts and statuses, and loan due dates after checkout.
- Checkout rejects blocked members and unavailable inventory; return rejects unknown or already-returned loans.
- `trg_loan_audit` runs per row after loan insertion or `returned_on` updates; `fn_late_fee` is immutable.
