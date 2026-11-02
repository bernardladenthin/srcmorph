### sql/library
- H: 1.0
- C: E07BF369
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:46:26Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Supports library cataloging, membership, book lending and returns, inventory tracking, and loan reporting.

#### Purpose
- Defines a PostgreSQL lending-library database, its operational routines, reporting views, migration path, and sample records.
- Tracks authors, books, members, loans, copy availability, and loan events.

#### Responsibilities
- **Core schema and sample data:** `author`, `book`, `member`, and `loan` tables, relational constraints, indexes, and seed records.
- **Lending reports:** overdue loans, member borrowing statistics, and popular books based on recent loan activity.
- **Checkout and return operations:** update book availability, create or close loans, calculate late fees, and record audit events.
- **Inventory migration:** introduce `branch` and `book_copy`, migrate book-level counts into branch-assigned copies, and link loans to individual copies.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and borrowing records.
- `overdue_loans` — lists open loans past due with member, book, and days-overdue details.
- `member_loan_stats` — aggregates total and open loans and last-loan date per member.
- `popular_books` — materializes books loaned at least five times in the past 365 days.
- `fn_checkout_book` — checks member eligibility and book availability, creates a loan, and returns its ID.
- `sp_return_book` — closes an open loan and restores book availability.
- `fn_late_fee` — calculates overdue fees at 50 cents per day, clamped at zero and capped at 2000 cents.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — record checkout and first-return events.
- `branch`, `book_copy` — represent branch locations and individually identified copies.

#### Data flow
- Seed records populate authors, books, members, and loans.
- `fn_checkout_book` validates a member and available inventory, decrements availability, and inserts a loan; the audit trigger records checkout.
- `sp_return_book` marks an open loan returned and restores availability; the audit trigger records the first return transition.
- Reporting views derive overdue loans and member statistics from `loan`, `member`, and `book`; `popular_books` aggregates recent loans with author details.
- Migration creates a main branch, converts book-level inventory into `book_copy` rows with availability-derived statuses, and adds copy and member branch assignments.

#### Dependencies
- Views and routines depend on `author`, `book`, `member`, and `loan`; checkout and return also use `loan_audit`.
- Migration adds foreign-key relationships from `book_copy` to `book` and `branch`, from `loan.copy_id` to `book_copy`, and from `member.home_branch_id` to `branch`.
- Migration uses PostgreSQL `generate_series`; audit timestamps use `now()`.

#### Cross-cutting
- PostgreSQL constraints enforce unique ISBNs and member emails, valid inventory counts and copy statuses, and loan due dates after checkout.
- Foreign keys connect catalog, membership, lending, branch, and copy records; deleting a member cascades to their loans.
- Checkout and return routines raise exceptions for blocked members, unavailable copies, unknown loans, and already-returned loans.
