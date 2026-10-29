### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:58Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A lending library system that models authors, books, members, and loans with integrity constraints, checkout logic, and branch management.

#### Purpose
This package provides the full data model and operational logic for a lending library. It manages the relational structure of library entities, enforces business rules around availability and member status, and supports book checkout, returns, late fee calculation, and branch-level copy tracking.

#### Responsibilities
- **Core schema**: Defines authors, books, members, and loans with integrity constraints and indexes.
- **Checkout/return logic**: Functions and triggers that validate member status, manage book availability, and maintain audit trails.
- **Migration and branching**: Adds branch infrastructure, redistributes book copies per branch, and replaces per-book counters with a `book_copy` table.
- **Reporting**: Views and materialized views for overdue tracking, member statistics, and popular books.

#### Key units
- **author table**: Stores author records with identity, name, and country.
- **book table**: Holds book metadata (ISBN, title, author link) and, pre-migration, copy counts and availability constraints.
- **member table**: Member records with contact info, join date, block status, and home branch assignment.
- **loan table**: Transaction records linking members and books, with due dates and return status.
- **loan_audit table**: Audit trail capturing every CHECKOUT and RETURN action.
- **fn_checkout_book function**: Validates member and availability, decrements copies, inserts loan, returns loan_id.
- **sp_return_book procedure**: Marks loan returned, increments book copies, raises error if unknown/already returned.
- **fn_late_fee function**: Calculates capped daily late fees (50c/day, max $20).
- **trg_loan_audit trigger**: Auto-inserts audit records on loan insert or returned_on update.
- **branch table** (post-migration): Branch entities with name and city.
- **book_copy table** (post-migration): Individual copies linked to books and branches, tracking status.
- **overdue_loans view**: Identifies unreturned loans past due date with days_overdue.
- **member_loan_stats view**: Aggregates per-member total and open loan counts.
- **popular_books materialized view**: Books loaned ≥5 times in the last year with author and count.
- **idx_book_copy_book index**: Supports queries on book_copy by book_id.

#### Data flow
1. **Checkout**: `fn_checkout_book` reads member status and book `copies_free`; if valid, it inserts a `loan` row, decrements `book.copies_free` (pre-migration) or links to a `book_copy` row (post-migration), and fires `trg_loan_audit`.
2. **Return**: `sp_return_book` sets `loan.returned_on = CURRENT_DATE`, increments the book's free copy count (or updates the associated `book_copy` status), and fires `trg_loan_audit`.
3. **Late fee**: `fn_late_fee` computes cents from due and returned dates.
4. **Reporting**: Views aggregate loan data across `member`, `book`, and `author` tables to produce overdue lists, member stats, and popularity rankings.

#### Dependencies
- **Tables**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch` (post-migration), `book_copy` (post-migration).
- **Functions**: `fn_checkout_book`, `fn_late_fee`, `sp_return_book`.
- **Triggers**: `trg_loan_audit`.
- **Sequences**: `author_id`, `book_id`, `member_id`, `loan_id`, `loan_audit_audit_id_seq`.
- **Post-migration**: `generate_series` for data migration; FK references between `book_copy`↔`book`, `book_copy`↔`branch`, `loan.copy_id`↔`book_copy.copy_id`, `member.home_branch_id`↔`branch.branch_id`.

#### Cross-cutting
- **Constraint pattern**: `CHECK (copies_free <= copies_total)` pre-migration; replaced by per-row status in `book_copy`.
- **Error handling**: Routines raise exceptions on blocked members, unavailable copies, or invalid return operations.
- **Audit pattern**: All CHECKOUT/RETURN actions flow through `loan_audit` via trigger, ensuring a complete history.
- **Naming evolution**: `member.joined_on` renamed to `member_since`; `book.copies_free/copies_total` removed in favor of `book_copy` status tracking.
- **Index strategy**: FK-indexes on `book.author_id`, `loan.member_id`, `loan.due_on`; post-migration index `idx_book_copy_book` on `book_copy (book_id)`.
