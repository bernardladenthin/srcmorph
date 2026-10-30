### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:46:59Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
Synthesizing the sql/library package summary from its per-file breakdowns.

The package is a PostgreSQL lending library database. It defines schema for authors, books, members, and loans. It includes reporting views and routines for checkout, returns, late fees, and audit logging. A v2 migration adds branches and physical copies. Seed data populates the tables.

The schema defines copies_total and copies_free columns. The v2 migration replaces them with branch and book_copy tables. The seed data still references the old columns. This creates a mismatch between the current schema and the migration.

The package is organized into five files: 01_schema.sql for core tables, 02_views.sql for reporting, 03_routines.sql for checkout and returns, 04_migration_v2.sql for the v2 branch and copy migration, and 05_seed_data.sql for demo data.

Key units include author, book, member, loan, loan_audit, branch, book_copy, overdue_loans, member_loan_stats, popular_books, fn_checkout_book, sp_return_book, fn_late_fee, trg_loan_audit, and trg_loan_audit_fn.> PostgreSQL lending-library catalog and circulation: authors, books, members, and loans, with overdue reporting, checkout/return, capped late fees, an audit trail, and a v2 shift from copy counters to branches and physical copies.

#### Purpose
- Owns the lending-library data model and circulation behavior in PostgreSQL: catalog, membership, loans, reporting, and demo data.
- Evolves stock from per-book `copies_free` / `copies_total` to one `book_copy` row per physical item at a `branch`.

#### Responsibilities
- Core schema (`01_schema.sql`): `author`, `book`, `member`, `loan`, uniqueness, stock and due-date checks, and lookup indexes.
- Reporting (`02_views.sql`): open overdue loans, per-member loan aggregates, and a materialized list of frequently loaned books.
- Circulation (`03_routines.sql`): checkout, return, late-fee calculation, and `loan_audit` via a trigger on `loan`.
- Migration v2 (`04_migration_v2.sql`): add `branch` and `book_copy`, backfill copies, point loans and members at copy/branch identity, drop book-level counters.
- Seed (`05_seed_data.sql`): transactional demo inserts of authors, books, members, and loans, including open loans and two blocked members.

#### Key units
- `author`, `book`, `member`, `loan`: catalog people, ISBN titles with stock counters, members, and dated loans.
- `loan_audit`: `CHECKOUT` / `RETURN` history keyed by `loan_id` (no declared foreign key).
- `branch`, `book_copy`: library sites and physical copies (`barcode`, status `AVAILABLE` / `ON_LOAN` / `LOST`).
- `overdue_loans`: open loans past `due_on`, with member email, title, and `days_overdue`.
- `member_loan_stats`: each member’s total loans, open loans, and latest `loaned_on`.
- `popular_books`: books loaned at least five times in 365 days, with author name; unique index `idx_popular_books_id`.
- `fn_checkout_book`: rejects a blocked member or zero free copies, decrements `copies_free`, inserts a loan due in `p_days` (default 21), returns `loan_id`.
- `sp_return_book`: sets `returned_on` only if still null and increments `copies_free`; raises if unknown or already returned.
- `fn_late_fee`: immutable SQL fee in cents, 50 per overdue day, capped at 2000; uses `CURRENT_DATE` when return date is null.
- `trg_loan_audit` / `trg_loan_audit_fn`: after insert audits `CHECKOUT`; after `returned_on` first becomes non-null audits `RETURN`.

#### Data flow
- Catalog path: `author` ← `book` ← `loan` → `member`. Open loans are those with `returned_on IS NULL`; partial index `idx_loan_open` covers them by `due_on`.
- Checkout: read `member.is_blocked` and `book.copies_free` → decrement free copies → insert `loan` (`due_on = CURRENT_DATE + p_days`) → trigger writes `loan_audit` `CHECKOUT`.
- Return: require an open `loan` → set `returned_on = CURRENT_DATE` → increment `book.copies_free` → trigger writes `loan_audit` `RETURN`. Late fee is computed from due and return dates, not stored by these routines.
- Reporting reads `loan`, `member`, `book`, and `author` only; views do not write.
- Migration reads `book.copies_total` / `copies_free`, inserts Main branch (`Springfield`), expands each book into `book_copy` rows (`barcode` = ISBN plus series number; first `copies_free` copies `AVAILABLE`, rest `ON_LOAN`), adds `loan.copy_id` and `member.home_branch_id` (defaulted to 1), renames `joined_on` to `member_since`, then drops `book_free_le_total`, `copies_free`, and `copies_total`.
- Seed loads parents before children inside `BEGIN`/`COMMIT`: 15 authors, 110 books (`copies_total` 2), 50 members (13 and 15 blocked), 150 loans (21-day terms; some `returned_on` null). Seed still uses the pre-migration counter columns.

#### Dependencies
- Tables collaborate as `book.author_id` → `author`, `loan.book_id` → `book`, `loan.member_id` → `member` (`ON DELETE CASCADE` in schema). Migration adds `book_copy` → `book` and `branch`, `loan.copy_id` → `book_copy`, `member.home_branch_id` → `branch`.
- Routines depend on `member`, `book`, `loan`, and `loan_audit`; views depend on `loan`, `member`, `book`, `author`.
- PostgreSQL: `BIGSERIAL`/`SMALLSERIAL`, partial indexes, `FILTER`, materialized views, PL/pgSQL, `generate_series`, `LATERAL`, `now()`, `CURRENT_DATE`, `INTERVAL '365 days'`.

#### Cross-cutting
- Stock rules differ by layer: schema checks `copies_free <= copies_total` and `due_on > loaned_on`; routines mutate counters and raise on blocked members, no free copy, or invalid return; v2 replaces counters with `book_copy.status` and does not redefine checkout/return.
- Dates default to `CURRENT_DATE` or `now()`; loan openness is `returned_on IS NULL` in indexes, views, return logic, and the audit trigger.
- `loan_audit.action` is constrained to `CHECKOUT` or `RETURN`. Audit and seed files declare no foreign keys; seed relationships are by populated ids only.
- Indexes recurring in the package: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id`, `idx_book_copy_book`.
