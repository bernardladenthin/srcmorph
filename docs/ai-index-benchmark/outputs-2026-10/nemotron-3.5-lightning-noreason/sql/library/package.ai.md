### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T08:55:00Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A lending library system managing authors, books, members, and loans with branch-aware copy tracking and audit trails.

#### Purpose
The package provides a complete lending library information system, from core data modeling and schema evolution to circulation logic, reporting views, and audit-backed transaction handling.

#### Responsibilities
- **Core schema**: Authors, books, members, and loans with integrity constraints for availability and referential integrity.
- **Circulation logic**: Book checkout, returns, late fee calculation, and audit trail via triggers and functions.
- **Migration and evolution**: Branch management, copy redistribution, column renames, and index updates for schema v2.
- **Reporting**: Read-only views for overdue tracking, member statistics, and popular book metrics.
- **Seed data**: Initial reference data and circulation records for system initialization.

#### Key units
- **author / book / member / loan tables**: Core entities with constraints ensuring book availability (`copies_free <= copies_total`) and valid loan dates.
- **loan_audit trigger**: Writes `CHECKOUT`/`RETURN` actions on loan insert/return updates.
- **fn_checkout_book**: Validates member status and book availability, decrements `copies_free`, inserts loan record.
- **sp_return_book**: Marks loan returned, increments `copies_free`, raises error if already returned.
- **fn_late_fee**: Calculates 50 cents/day (capped at 2000) based on due/return dates.
- **overdue_loans view**: Active loans past due with days_overdue calculation.
- **member_loan_stats view**: Per-member loan totals, open count, and most recent loan date.
- **popular_books view**: Book popularity count (last 365 days, min 5 loans) with author names.
- **branch / book_copy tables** (v2): Branch structure, per-copy tracking, and `copy_id` foreign key on loans.
- **member_since / home_branch_id** (v2): Renamed column and new foreign key on members.

#### Data flow
- Seed data inserts into `author`, `book`, `member`, `loan` tables.
- Checkout flow: `fn_checkout_book` validates member/member status and `book.copies_free > 0`, inserts `loan`, decrements `book.copies_free`, triggers `loan_audit` entry.
- Return flow: `sp_return_book` sets `loan.returned_on`, increments `book.copies_free`, triggers audit.
- Late fee: `fn_late_fee` computes cents from due/return dates; called by application or trigger logic.
- Migration v2 redistributes `copies_total` into `book_copy` rows linked to `branch` rows; `loan.copy_id` references `book_copy`; `member.home_branch_id` defaults to branch 1.
- Views aggregate from `loan`, `book`, `member`, `author` tables to surface overdue status, statistics, and popularity.

#### Dependencies
- **Tables**: `author`, `book`, `member`, `loan`, `loan_audit`, `branch`, `book_copy`.
- **Functions**: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`.
- **Triggers**: `trg_loan_audit`.
- **Sequences**: `author_author_id_seq`, `book_book_id_seq`, `member_member_id_seq`, `loan_loan_id_seq`, `loan_audit_audit_id_seq`.
- **Views**: `overdue_loans`, `member_loan_stats`, `popular_books`.
- **Migration**: Relies on `book`, `branch`, `loan`, `member` tables for DDL operations and `generate_series` for copy population.

#### Cross-cutting
- **Availability constraint**: `CONSTRAINT book_free_le_total` ensures `copies_free <= copies_total` (pre-v2); replaced by per-copy `book_copy.status` rows.
- **Referential integrity**: All foreign keys enforce `ON DELETE CASCADE` on `loan.member_id`; `book_copy.book_id` and `book_copy.branch_id` reference `book` and `branch`.
- **Error handling**: Functions raise exceptions for blocked members, unavailable copies, or duplicate returns.
- **Trigger patterns**: `trg_loan_audit` uses `WHEN INSERT OR UPDATE OF returned_on` with `NEW/OLD` checks for `action IN ('CHECKOUT', 'RETURN')`.
- **Index strategy**: `idx_loan_open` filters `returned_on IS NULL`; `idx_popular_books_id` enforces uniqueness on `book_id` in the materialized view; `idx_book_copy_book` indexes `book_copy(book_id)` for loan lookups.
