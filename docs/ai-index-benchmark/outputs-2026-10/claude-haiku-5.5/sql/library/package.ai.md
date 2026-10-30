### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:33:25Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Lending library PostgreSQL database: schema for authors, books and copies across branches, members, loans, reporting views, checkout/return routines, audit logging, migration, and seed data.

#### Purpose
- Persistent model and business logic for a library lending system: catalog, members, loans, and physical copy inventory.
- Covers schema evolution (v1 counter-based stock to v2 per-branch `book_copy` rows), reporting, and test data.

#### Responsibilities
- **Schema (v1 and migrated)**: tables `author`, `book`, `member`, `loan`, with `branch` and `book_copy` added by migration; bounds checks on copy counts and loan dates.
- **Reporting views**: `overdue_loans`, `member_loan_stats`, and materialized `popular_books` (365-day window, at least 5 loans) with unique index `idx_popular_books_id`.
- **Lending routines**: `fn_checkout_book` (blocked-member and stock checks, creates loan), `sp_return_book` (marks return, restores stock), `fn_late_fee` (50 cents/day, capped at 2000 cents).
- **Audit trail**: table `loan_audit` filled by trigger `trg_loan_audit` through `trg_loan_audit_fn` on CHECKOUT and RETURN events.
- **Migration**: `04_migration_v2.sql` creates `branch`, backfills `book_copy` from `book.copies_total`/`copies_free`, renames `member.joined_on` to `member_since`, adds `member.home_branch_id` and `loan.copy_id`, drops per-book counters.
- **Seed data**: generated test rows (15 authors, 110 books, 50 members, 150 loans) produced by `generate-seed-sql.py`; not hand-edited.

#### Key units
- `author`, `book`, `member`, `loan`: core entities, identified by `author_id`, `book_id`, `member_id`, `loan_id`.
- `book_copy`, `branch`: per-copy inventory and locations, added in v2 (`copy_id`, `branch_id`, `barcode`, `status`).
- `overdue_loans`, `member_loan_stats`, `popular_books`: reporting views.
- `fn_checkout_book`: checkout function, returns new `loan_id`.
- `sp_return_book`: return procedure, returns `book_id`.
- `fn_late_fee`: immutable late fee calculation.
- `trg_loan_audit_fn` / `trg_loan_audit`: audit trigger logic and binding on `loan`.
- `loan_audit`: audit log table.
- `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`, `idx_popular_books_id`: indexes.

#### Data flow
- Seed/DDL creates `author` → `book` → `member` → `loan`; migration v2 derives `book_copy` rows from `book` counters, then links `loan.copy_id`.
- Checkout: `fn_checkout_book` reads `member.is_blocked` and `book.copies_free`, decrements stock, inserts `loan`; trigger writes `loan_audit` CHECKOUT.
- Return: `sp_return_book` sets `loan.returned_on`, increments stock; trigger writes `loan_audit` RETURN.
- Reporting: views read `loan`, `member`, `book`, `author` to produce overdue, per-member, and popularity results.

#### Dependencies
- Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`.
- Built-ins: `CURRENT_DATE`, `now()`, `COALESCE`, `LEAST`, `GREATEST`, `COUNT`, `MAX`, `generate_series`.
- Sequences implied by `BIGSERIAL` / `SMALLSERIAL` columns.
- Internal: views depend on base tables; routines depend on `book.copies_free` in v1 and `book_copy` after migration.
- Note: v1 routines reference `book.copies_free`, which migration v2 drops; routines are not updated in the provided summaries.

#### Cross-cutting
- Errors signaled via exceptions for blocked members, no free copies, unknown or already-returned loans.
- Integrity via CHECK constraints (copy bounds, `due_on > loaned_on`, status values) and FKs with `ON DELETE CASCADE` on `loan.member_id`.
- Dates use `CURRENT_DATE`; audit timestamps use `TIMESTAMPTZ` with `now()`.
- Scripts are wrapped in `BEGIN` / `COMMIT` transactions (migration, seed).
- Naming: `idx_` prefix for indexes, `fn_` / `sp_` / `trg_` prefixes for routines and triggers.
