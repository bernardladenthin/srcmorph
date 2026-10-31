### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:37:30Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL lending-library database: schema, reporting views, checkout/return routines with late-fee and audit trail, a branch/copy migration, and seed data.

#### Purpose
- Models a library lending system: authors, books, physical copies across branches, members, and loans with due and return dates.
- Provides checkout/return workflows, late-fee calculation, overdue and popularity reporting, a v2 migration moving inventory from book-level counters to per-copy rows, and demo seed data.

#### Responsibilities
- Core schema: tables `author`, `book`, `member`, `loan` with CHECK constraints and partial index `idx_loan_open` for open loans.
- Reporting: views `overdue_loans`, `member_loan_stats`, materialized view `popular_books` (indexed by `idx_popular_books_id`).
- Lending operations: `fn_checkout_book` (blocks blocked members, decrements availability, inserts loan), `sp_return_book` (sets `returned_on`, restocks).
- Fees: `fn_late_fee` (50 cents/day, capped at 2000 cents, floored at 0).
- Audit: table `loan_audit` fed by trigger `trg_loan_audit` through `trg_loan_audit_fn` on loan insert and return.
- Schema evolution: `04_migration_v2.sql` adds `branch` and `book_copy`, backfills copies from `book` counters, links `loan.copy_id`, renames `member.joined_on` to `member_since`, adds `member.home_branch_id`, and drops `book.copies_total`/`copies_free`.
- Demo data: `05_seed_data.sql` inserts 15 authors, 110 books, 50 members, 150 loans.

#### Key units
- `author`, `book`, `member`, `loan`: core tables; `loan.returned_on` NULL marks open loans.
- `idx_loan_open`: partial index on `loan(due_on) WHERE returned_on IS NULL`.
- `overdue_loans`: view of unreturned loans past due with `days_overdue`.
- `member_loan_stats`: view of per-member totals, open loans, last loan date.
- `popular_books`: materialized view of titles loaned at least 5 times in the last 365 days.
- `fn_checkout_book(p_member_id, p_book_id, p_days=21)`: returns new loan_id; raises on blocked member or no free copy.
- `sp_return_book(p_loan_id)`: procedure; sets return date and increments copies_free; raises if unknown or already returned.
- `fn_late_fee(p_due_on, p_returned_on)`: IMMUTABLE SQL function returning fee in cents.
- `trg_loan_audit_fn()` / `trg_loan_audit`: trigger writing CHECKOUT and RETURN rows to `loan_audit`.
- `loan_audit`: audit table with `action` CHECK in CHECKOUT/RETURN and `changed_at TIMESTAMPTZ`.
- `branch`: branch lookup (`name` unique, `city`); seeded with Main/Springfield.
- `book_copy`: per-physical-copy row with `barcode` unique and `status` in AVAILABLE/ON_LOAN/LOST.
- `idx_book_copy_book`: index on `book_copy(book_id)`.

#### Data flow
- Seed/schema: `01_schema.sql` creates tables, then `05_seed_data.sql` loads authors -> books -> members -> loans.
- Checkout: `fn_checkout_book` checks `member.is_blocked` and `book.copies_free`, updates availability, inserts `loan`; trigger logs CHECKOUT to `loan_audit`.
- Return: `sp_return_book` sets `loan.returned_on` and restocks; trigger logs RETURN when `returned_on` goes NULL to non-NULL.
- Reporting: views read `loan`, `member`, `book`, `author`; `popular_books` must be refreshed externally.
- Migration: `04_migration_v2.sql` derives `book_copy` rows from `book.copies_total`/`copies_free` via `generate_series`, marking first `copies_free` copies AVAILABLE and the rest ON_LOAN.

#### Dependencies
- Internal: routines depend on `member`, `book`, `loan`, `loan_audit`; views depend on the core tables; migration depends on `book`, `loan`, `member`.
- Cross-file ordering: `01` before `02`/`03`; `04` expects `01` tables plus `book.copies_*` (pre-migration state); `05` expects `01` schema with pre-migration `book` columns.
- Note: `03_routines.sql` references `book.copies_free`, which `04_migration_v2.sql` drops, so the routines are incompatible with the migrated schema unless updated.
- External: PostgreSQL built-ins `CURRENT_DATE`, `now()`, `COALESCE`, `generate_series`, `INTERVAL`, `FILTER`.

#### Cross-cutting
- All scripts are PostgreSQL dialect; DML scripts wrap work in `BEGIN`/`COMMIT`.
- Error handling: routines raise exceptions for blocked members, unavailable copies, and unknown or already-returned loans.
- Auditing: state changes on `loan` are captured by trigger into `loan_audit`.
- Date logic uses `CURRENT_DATE` throughout (fees, overdue, popularity window, default due date).
- Constraints enforce integrity: CHECK on counts and due dates, UNIQUE on ISBN, email, branch name, barcode; FK cascade on `loan.member_id`.
- Concurrency and locking are not addressed in the summaries.
