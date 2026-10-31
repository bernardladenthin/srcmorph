### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:37Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> A PostgreSQL lending-library database that models the catalog, members, and circulation lifecycle — checkout, return, late-fee assessment, audit logging, and reporting — with a v2 migration introducing per-copy inventory across branches.

#### Purpose
- Core database layer for a lending library: schema, reporting views, business routines, and seed data.
- Moves inventory from per-book counters to per-copy rows in a versioned migration (v2).

#### Responsibilities
- **Schema definition** (`01_schema.sql`): tables `author`, `book`, `member`, `loan` with FKs, CHECK constraints (birth year range, copies non-negative, `copies_free <= copies_total`, `due_on > loaned_on`), and indexes including a partial index on open loans.
- **Reporting views** (`02_views.sql`): `overdue_loans`, `member_loan_stats`, and materialized view `popular_books` (last 365 days, `COUNT(*) >= 5`, unique index on `book_id`).
- **Circulation logic** (`03_routines.sql`): `fn_checkout_book` (blocks blocked members, decrements `copies_free`, inserts loan with `due_on = CURRENT_DATE + p_days` default 21), `sp_return_book` (sets `returned_on`, increments `copies_free`), `fn_late_fee` (50 cents/day capped at 2000, IMMUTABLE), and trigger `trg_loan_audit` logging CHECKOUT/RETURN to `loan_audit`.
- **Migration to v2** (`04_migration_v2.sql`): adds `branch` and `book_copy` tables, backfills one copy row per `copies_total` via `generate_series` with barcode `isbn || '-' || n`, sets status AVAILABLE/ON_LOAN, adds `loan.copy_id` and `member.home_branch_id`, renames `joined_on` → `member_since`, drops `copies_free`/`copies_total` and constraint `book_free_le_total`.
- **Seed data** (`05_seed_data.sql`): transactional batch loading 15 authors, 110 books, 50 members, 150 loans.

#### Key units
- `author`, `book`, `member`, `loan` — core tables with FK chain `loan.book_id → book.author_id → author` and `loan.member_id → member`.
- `view overdue_loans` — unreturned loans past due with computed `days_overdue`.
- `view member_loan_stats` — per-member totals, open count (`FILTER (WHERE returned_on IS NULL)`), last loan date.
- `materialized view popular_books` — loan counts per book with author name for the trailing year.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee` — circulation and fee logic.
- `trg_loan_audit_fn` + `trg_loan_audit` — audit trail on `loan` inserts/return updates.
- `branch`, `book_copy` — v2 inventory model with `status` CHECK in ('AVAILABLE','ON_LOAN','LOST') and unique `barcode`.

#### Data flow
- Checkout: `fn_checkout_book` validates member → decrements `book.copies_free` → inserts `loan` → trigger logs CHECKOUT.
- Return: `sp_return_book` sets `loan.returned_on` → increments `copies_free` → trigger logs RETURN.
- Reporting: views read base tables; `popular_books` is materialized and refreshed via index `idx_popular_books_id`.
- Migration: backfill reads `book.copies_total`/`copies_free` to populate `book_copy`, then drops the old counter columns.

#### Dependencies
- Internal: routines reference `member`, `book`, `loan`, `loan_audit`; views join `loan`–`member`–`book`–`author`; migration alters `loan`, `member`, `book` and creates `branch`, `book_copy`; seed data targets all four base tables.
- External: PostgreSQL dialect features — `BIGSERIAL`, `SMALLSERIAL`, partial indexes, `FILTER`, `CURRENT_DATE`, `INTERVAL`, `generate_series`, `CROSS JOIN LATERAL`, PL/pgSQL triggers, transactional DDL (`BEGIN`/`COMMIT`).

#### Cross-cutting
- CHECK constraints used consistently for domain invariants (dates, copy counts, statuses, action enum).
- `BIGSERIAL` primary keys throughout (`SMALLSERIAL` for `branch`).
- Nullable `returned_on` distinguishes open vs closed loans across views, routines, and partial index.
- All multi-statement scripts wrapped in transactions; `fn_late_fee` is marked `IMMUTABLE`.
