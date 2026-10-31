### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:34:28Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Manages a lending library’s catalog, members, physical inventory, checkout and return workflows, reporting, and loan history.

#### Purpose
- Defines and evolves a PostgreSQL library database from book-level availability counters to branch-aware physical book copies.
- Supports lending operations, audit records, reports, and sample catalog and loan data.

#### Responsibilities
- **Catalog and lending schema:** `author`, `book`, `member`, and `loan` tables with identifiers, relationships, availability and date constraints, and lookup indexes.
- **Reporting:** `overdue_loans` lists active overdue loans; `member_loan_stats` summarizes member borrowing; materialized view `popular_books` counts recent loans by book and author.
- **Checkout, returns, and fees:** routines update inventory and loan records, calculate capped late fees, and audit checkout and return events.
- **Branch-aware migration:** adds `branch` and `book_copy`, assigns existing copies to a main branch, and links loans and members to physical copies and home branches.
- **Seed data:** populates authors, books, members, and loan history.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and lending records; `loan.member_id` cascades on member deletion.
- `overdue_loans` — active loans past their due date with days overdue.
- `member_loan_stats` — per-member total and open loan counts and most recent loan date.
- `popular_books` — materialized counts of loans in the past year for books loaned at least five times; uniquely indexed by `book_id`.
- `fn_checkout_book(p_member_id, p_book_id, p_days)` — rejects blocked members or unavailable books, decrements the free-copy count, creates a loan, and returns its ID.
- `sp_return_book(p_loan_id)` — marks an open loan returned and restores a free copy; errors for unknown or already-returned loans.
- `fn_late_fee(p_due_on, p_returned_on)` — calculates a nonnegative late fee at 50 cents per overdue day, capped at 2000.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and return transitions.
- `branch`, `book_copy` — branch locations and individual copies with `AVAILABLE`, `ON_LOAN`, or `LOST` status.

#### Data flow
- Checkout validates the member and book availability, decrements `book.copies_free`, inserts a `loan`, and triggers a `CHECKOUT` audit record.
- Return updates an unreturned loan’s `returned_on`, restores one free copy, and triggers a `RETURN` audit record when the return date changes from NULL.
- Reporting views derive overdue loans and member statistics from `loan`, `member`, and `book`; `popular_books` aggregates recent loans with book and author details.
- Migration creates a `Main` branch and generates `book_copy` rows from existing book copy totals, assigning initial statuses from free-copy counts; it adds `loan.copy_id` and member home-branch references before dropping book-level copy counters.
- Seed script inserts authors, books, members, and loans in a transaction.

#### Dependencies
- Views and routines use `author`, `book`, `member`, and `loan`; audit trigger logic writes to `loan_audit`.
- Migration depends on existing `book`, `loan`, and `member` data and PostgreSQL `generate_series`.
- Foreign-key relationships connect books to authors, loans to books and members, copies to books and branches, and migrated loans and members to copies and branches.

#### Cross-cutting
- PostgreSQL DDL, views, routines, trigger, migration, and seed data.
- Constraints enforce unique ISBNs and member emails, valid copy counts and copy statuses, and due dates after loan dates.
- `fn_late_fee` is marked `IMMUTABLE`; migration is transactional.
