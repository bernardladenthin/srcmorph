### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:51:04Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> PostgreSQL database package for a multi-branch lending library, modeling author catalogs, per-branch physical book copy inventory, member registration with blocking, and the complete loan lifecycle including checkout, due-date tracking, late-fee calculation, returns, and audit logging.

#### Purpose
- Define the relational schema and transactional routines for library circulation operations across two schema versions (v1 aggregate copy counters, v2 per-branch physical copies)
- Provide reporting views for overdue loans, member statistics, and book popularity, plus deterministic seed data for development and testing

#### Responsibilities
- Core schema v1: `author`, `book` (with `copies_total`/`copies_free` counters), `member`, `loan` tables with foreign keys and partial indexes for open loans
- Schema migration v2: introduce `branch` and `book_copy` (per-copy inventory with barcode/status), link `loan` to specific `copy_id`, assign `member.home_branch_id`, and retire aggregate counters from `book`
- Transaction processing: atomic checkout via `fn_checkout_book` (validates member not blocked, decrements availability, inserts loan), return via `sp_return_book` (sets `returned_on`, restores availability), and late-fee computation via `fn_late_fee` (50 cents per day capped at 2000)
- Audit logging: `loan_audit` table populated by `trg_loan_audit` trigger on `loan` INSERT/UPDATE to record CHECKOUT and RETURN events
- Reporting: `overdue_loans` view (unreturned past-due with `days_overdue`), `member_loan_stats` view (totals/open/last loan per member), `popular_books` materialized view (books with ≥5 loans in last 365 days)
- Test fixtures: seed script populating 15 authors, 110 books, 50 members, and 150 loans with deterministic dates

#### Key units
- Tables: `author`, `book`, `member`, `loan`, `branch`, `book_copy`, `loan_audit`
- Views: `overdue_loans`, `member_loan_stats`, `popular_books` (materialized, indexed by `idx_popular_books_id`)
- Functions: `fn_checkout_book(p_member_id, p_book_id, p_days DEFAULT 21)`, `fn_late_fee(p_due_on, p_returned_on)` (IMMUTABLE), `trg_loan_audit_fn()` (trigger function)
- Procedure: `sp_return_book(p_loan_id)`
- Trigger: `trg_loan_audit` (AFTER INSERT OR UPDATE OF `returned_on` ON `loan`)
- Indexes: `idx_book_author`, `idx_loan_member`, `idx_loan_open` (partial on `due_on` WHERE `returned_on IS NULL`), `idx_book_copy_book`

#### Data flow
- Checkout pipeline: `fn_checkout_book` reads `member.is_blocked` → updates `book.copies_free` (decrement) → inserts `loan` with `due_on = CURRENT_DATE + p_days` → `trg_loan_audit` fires inserting `loan_audit` row with action 'CHECKOUT'
- Return pipeline: `sp_return_book` updates `loan.returned_on` (if NULL) → `trg_loan_audit` fires inserting `loan_audit` row with action 'RETURN' → updates `book.copies_free` (increment)
- Fee calculation: `fn_late_fee` computes `LEAST(GREATEST(COALESCE(returned_on, CURRENT_DATE) - due_on, 0) * 50, 2000)` in cents
- Reporting flow: `loan` joined to `member`/`book`/`author` feeds `overdue_loans` (filter `returned_on IS NULL AND due_on < CURRENT_DATE`), `member_loan_stats` (aggregate counts per member), and `popular_books` (filter `loaned_on >= CURRENT_DATE - INTERVAL '365 days'` HAVING COUNT >= 5)
- Migration flow: `book.copies_total`/`copies_free` → `generate_series` backfill into `book_copy` (status AVAILABLE/ON_LOAN, barcode from ISBN) → drop aggregate columns from `book`; `member.joined_on` renamed to `member_since` and `home_branch_id` backfilled to branch 1

#### Dependencies
- Internal FK relationships: `book.author_id`→`author`, `loan.book_id`→`book`, `loan.member_id`→`member` (ON DELETE CASCADE), `loan.copy_id`→`book_copy` (v2), `book_copy.book_id`→`book`, `book_copy.branch_id`→`branch`, `member.home_branch_id`→`branch`
- External: PostgreSQL built-ins (`generate_series`, `now()`, `CURRENT_DATE`, `INTERVAL`, `FILTER` clause), PL/pgSQL, seed generator script `docs/ai-index-benchmark/tools/generate-seed-sql.py`

#### Cross-cutting
- PostgreSQL dialect throughout: `BIGSERIAL`/`SMALLSERIAL` keys, partial indexes, materialized views, `CHECK` constraints (date ordering, copy count limits, status enums, birth year range)
- Transactional boundaries: migration and seed scripts wrapped in explicit `BEGIN`/`COMMIT`; checkout/return designed as atomic operations
- Audit pattern: trigger-based immutable logging of loan lifecycle transitions to `loan_audit` with `action` CHECK constraint ('CHECKOUT'/'RETURN')
- Schema evolution: v1 uses denormalized copy counters on `book`; v2 normalizes to per-copy rows in `book_copy` with branch assignment and barcode tracking, requiring migration of existing data via `generate_series`
