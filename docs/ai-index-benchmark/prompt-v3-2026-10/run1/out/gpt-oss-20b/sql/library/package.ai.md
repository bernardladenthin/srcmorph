### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:56:12Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL data model and procedural toolkit for a multi‑branch lending library, covering catalog, members, loans, reporting, and audit.

#### Purpose
- Persist library catalog, member, and loan data.
- Provide reporting views and audit trails.
- Support branch‑level inventory and book‑copy tracking.

#### Responsibilities
- **Schema definition** – tables for authors, books, members, loans, branches, book copies, and audit logs.
- **Reporting** – views for overdue loans, member statistics, and popular books.
- **Business logic** – functions and procedures for checkout, return, late‑fee calculation, and audit logging.
- **Migration** – upgrade to branch‑based inventory, replacing copy counters with explicit book_copy records.

#### Key units
- `author`, `book`, `member`, `loan` tables – core catalog and transaction data.
- `branch`, `book_copy` tables – branch hierarchy and per‑copy status.
- `loan_audit` table – audit trail of checkout/return actions.
- `fn_checkout_book(p_member_id, p_book_id, p_days)` – checkout logic.
- `sp_return_book(p_loan_id)` – return logic.
- `fn_late_fee(p_due_on, p_returned_on)` – late‑fee calculation.
- `trg_loan_audit` trigger – logs audit rows on loan insert/update.
- Views: `overdue_loans`, `member_loan_stats`, materialized view `popular_books`.

#### Data flow
1. **Checkout** – `fn_checkout_book` checks member status, decrements `book.copies_free` (or updates `book_copy.status` in v2), inserts a `loan`, and logs a `CHECKOUT` audit row.
2. **Return** – `sp_return_book` updates `loan.returned_on`, increments `book.copies_free` (or updates `book_copy.status`), and the trigger logs a `RETURN` audit row.
3. **Reporting** – Views aggregate `loan`, `member`, `book`, and `author` data to expose overdue loans, member loan stats, and popular books.
4. **Migration** – `04_migration_v2.sql` replaces `copies_total/copies_free` with `book_copy` records, adds `branch` references, and updates `loan` and `member` to point to copies and home branches.

#### Dependencies
- Internal: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- External: PostgreSQL built‑in functions (`CURRENT_DATE`, `now()`, `INTERVAL`), standard data types.
- Indexes: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`.

#### Cross‑cutting
- Consistent use of foreign keys and indexes for referential integrity and query performance.
- Centralized audit via `loan_audit` and trigger `trg_loan_audit`.
- Late‑fee logic encapsulated in `fn_late_fee` for reuse.
- Migration script ensures data preservation while shifting to a copy‑based inventory model.
