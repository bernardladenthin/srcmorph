### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:01:08Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Defines a lending-library database for cataloging books, managing members and circulation, reporting borrowing activity, and evolving inventory from book-level counts to branch-level copies.

#### Purpose
- Provides the schema, circulation operations, reporting views, migration, and sample data for a lending-library database.
- Tracks authors, books, members, loans, availability, overdue activity, and loan-action audits.

#### Responsibilities
- **Catalog and circulation schema:** `author`, `book`, `member`, and `loan` tables, with identifiers, relationships, validation constraints, and indexes.
- **Checkout, returns, and auditing:** routines manage loan creation and return, calculate late fees, and record checkout/return events in `loan_audit`.
- **Operational reporting:** views identify overdue loans, summarize member borrowing, and list frequently borrowed books.
- **Inventory migration:** adds `branch` and `book_copy`, backfills copies into the Main branch, links members and loans to branches/copies, and removes book-level copy counters.
- **Sample dataset:** transactionally seeds authors, books, members, and loan histories.

#### Key units
- `author`, `book`, `member`, `loan` — core catalog, membership, and lending records.
- `fn_checkout_book` — rejects blocked members or unavailable inventory, decrements free-copy count, and creates a loan.
- `sp_return_book` — marks an open loan returned and restores a free copy; rejects unknown or already-returned loans.
- `fn_late_fee` — computes a per-day overdue fee with a zero floor and 2,000-cent cap.
- `loan_audit`, `trg_loan_audit_fn`, `trg_loan_audit` — store and generate audit records for loan checkout and return transitions.
- `overdue_loans` — reports unreturned loans past their due date and days overdue.
- `member_loan_stats` — aggregates total/open loans and most recent loan date by member.
- `popular_books` — materialized report of books loaned at least five times in the prior 365 days, indexed by `book_id`.
- `branch`, `book_copy` — represent branch locations and individually identified copies with availability status.

#### Data flow
- Seed inserts populate authors and books, then members and loans with relationships to the catalog and membership records.
- `fn_checkout_book` validates member and book availability, updates book inventory, and creates a dated loan; the loan trigger audits the insertion.
- `sp_return_book` updates an open loan and inventory; the trigger audits the null-to-non-null return transition.
- Reporting views derive overdue, member activity, and popular-title results from `loan`, `member`, `book`, and `author`.
- The v2 migration creates a Main branch and generates `book_copy` records from existing book copy counts, then assigns member branch defaults and adds loan copy references.

#### Dependencies
- SQL objects collaborate through `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, and `book_copy`.
- The migration uses PostgreSQL `generate_series` to create copy records.
- Routines and overdue reporting use `CURRENT_DATE`; audit timestamps use `now()`.

#### Cross-cutting
- Referential constraints connect books to authors, loans to books and members, and migrated copies and members to branches; deleting a member cascades to their loans.
- Checks constrain inventory counts, loan due dates, copy statuses, author birth years, and audit actions; unique constraints cover ISBNs, member emails, branch names, and barcodes.
- The v2 migration retires `book.copies_total` and `book.copies_free`, while checkout and return routines operate on `book.copies_free`; these files therefore describe different inventory representations.
