### sql/library
- H: 1.0
- C: 73EB165D
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T22:46:58Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> PostgreSQL lending-library database: catalog, membership, circulation (checkout/return), late-fee calculation, audit trail, and reporting views for a multi-branch book loan system.

#### Purpose
- Complete database layer for a library lending system: schema, business-logic routines, reporting views, a v2 multi-branch migration, and seed data.
- Manages the full loan lifecycle (checkout → return) with availability tracking, blocked-member enforcement, and an audit trail.

#### Responsibilities
- **Schema & integrity** — `01_schema.sql` defines `author`, `book`, `member`, `loan` tables with FK constraints, CHECK constraints, and partial indexes.
- **Reporting** — `02_views.sql` provides `overdue_loans`, `member_loan_stats`, and `popular_books` (materialized view) for operational dashboards.
- **Loan lifecycle & business rules** — `03_routines.sql` implements `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, and a `trg_loan_audit` trigger that writes to `loan_audit`.
- **Multi-branch migration** — `04_migration_v2.sql` introduces `branch` and `book_copy` tables, links `loan` to a specific `copy_id`, assigns `member.home_branch_id`, and drops the old `copies_total`/`copies_free` counters.
- **Seed data** — `05_seed_data.sql` populates 15 authors, 110 books, 50 members, 150 loans in a single transaction.

#### Key units
- `author` — catalog entity (PK `author_id`, `full_name`, `birth_year`, `country_code`).
- `book` — catalog entity (PK `book_id`, `isbn` UNIQUE, FK `author_id`, `copies_total`/`copies_free` in v1; dropped in v2).
- `member` — patron entity (PK `member_id`, `email` UNIQUE, `is_blocked`, `home_branch_id` in v2).
- `loan` — circulation record (PK `loan_id`, FK `book_id`, `member_id`, `copy_id` in v2, `loaned_on`, `due_on`, `returned_on`).
- `branch` — v2 location entity (PK `branch_id`, `name`, `city`).
- `book_copy` — v2 physical copy (PK `copy_id`, FK `book_id`, `branch_id`, `barcode` UNIQUE, `status` CHECK IN AVAILABLE/ON_LOAN/LOST).
- `loan_audit` — append-only audit log (PK `audit_id`, `loan_id`, `action` CHECK IN CHECKOUT/RETURN, `changed_at`).
- `fn_checkout_book(p_member_id, p_book_id, p_days)` — validates member not blocked, decrements `copies_free`, inserts `loan`, returns `loan_id`.
- `sp_return_book(p_loan_id)` — sets `returned_on`, increments `copies_free`, raises on double-return.
- `fn_late_fee(p_due_on, p_returned_on)` — IMMUTABLE SQL function; 50 cents/day capped at 2000.
- `trg_loan_audit_fn` / `trg_loan_audit` — AFTER INSERT OR UPDATE OF `returned_on` trigger writing CHECKOUT/RETURN rows.
- `overdue_loans` — view joining `loan`/`member`/`book` with computed `days_overdue`.
- `member_loan_stats` — view aggregating total/open loans and last loan date per member.
- `popular_books` — materialized view of books loaned ≥ 5 times in trailing 365 days.

#### Data flow
- **Checkout path**: caller invokes `fn_checkout_book` → reads `member.is_blocked` and `book.copies_free` → decrements `copies_free` → INSERTs `loan` row → trigger `trg_loan_audit` fires → INSERTs `loan_audit` row.
- **Return path**: caller invokes `sp_return_book` → sets `loan.returned_on` → increments `book.copies_free` → trigger detects NULL→non-NULL transition → INSERTs `loan_audit` row.
- **Reporting path**: views (`overdue_loans`, `member_loan_stats`, `popular_books`) read from base tables; `popular_books` is a materialized view requiring manual refresh.
- **Late-fee path**: `fn_late_fee` is a pure function called externally with `due_on` and `returned_on` (or NULL for current date).

#### Dependencies
- All files depend on the four base tables (`author`, `book`, `member`, `loan`) defined in `01_schema.sql`.
- `03_routines.sql` additionally creates and writes to `loan_audit`.
- `04_migration_v2.sql` depends on pre-existing `book`, `loan`, `member` and creates `branch`, `book_copy`; alters `loan`, `member`, `book`.
- `05_seed_data.sql` depends on all four base tables (v1 schema, pre-migration).
- External: PostgreSQL engine (PL/pgSQL, materialized views, partial indexes, `BIGSERIAL`).

#### Cross-cutting
- **Versioning**: v1 schema uses `copies_total`/`copies_free` counters on `book`; v2 migration replaces these with explicit `book_copy` rows per branch. Seed data targets v1.
- **Audit pattern**: single trigger (`trg_loan_audit`) captures both CHECKOUT and RETURN events into `loan_audit` with a `TIMESTAMPTZ` timestamp.
- **Error handling**: routines use `RAISE EXCEPTION` for business-rule violations (blocked member, no free copy, unknown/double-returned loan).
- **Constraint style**: CHECK constraints enforce domain rules (`copies_free <= copies_total`, `due_on > loaned_on`, `action IN ('CHECKOUT','RETURN')`, `status IN ('AVAILABLE','ON_LOAN','LOST')`).
- **Indexing strategy**: partial index `idx_loan_open` on `loan(due_on) WHERE returned_on IS NULL` for efficient overdue queries; unique index on `popular_books(book_id)` for materialized view refresh.
