### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:19:31Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Lending library circulation management for book inventory, member borrowing, branch holdings, overdue tracking, and late fees.
#### Purpose
- Manage lending-library inventory, member loans, branch copies, and circulation rules with reporting and audit
- Enforce availability, due-date, blocked-member, and per-copy status constraints in PostgreSQL
#### Responsibilities
- Core schema: `author`, `book`, `member`, `loan` with ISBN uniqueness, `copies_total`/`copies_free`, `loan_due_after_loan`, partial `idx_loan_open`
- Reporting: `overdue_loans` lateness, `member_loan_stats` totals/open/last activity, materialized `popular_books` yearly loans
- Circulation routines: `fn_checkout_book` checkout, `sp_return_book` return, `fn_late_fee` fee calculation, `trg_loan_audit` audit trail via `loan_audit`
- Branch migration v2: `branch`, `book_copy` per-copy inventory, `loan.copy_id`, `member.home_branch_id`, retire `copies_total`/`copies_free`
- Test fixture: deterministic seed inserts for `author` 15 rows, `book` 110 rows, `member` 50 rows, `loan` 150 rows
#### Key units
- `author(author_id, full_name, birth_year, country_code)`: author identity
- `book(book_id, isbn, title, author_id, copies_total, copies_free)`: title inventory with `book_free_le_total`
- `member(member_id, email, full_name, joined_on/member_since, is_blocked, home_branch_id)`: borrower with block flag
- `loan(loan_id, book_id, copy_id, member_id, loaned_on, due_on, returned_on)`: loan lifecycle with open/overdue state
- `branch(branch_id, name, city)`: library locations
- `book_copy(copy_id, book_id, branch_id, barcode, status)`: individual copy with `AVAILABLE/ON_LOAN/LOST`
- `loan_audit(audit_id, loan_id, action, changed_at)`: `CHECKOUT`/`RETURN` trail
- `overdue_loans(loan_id, email, title, days_overdue)`: unreturned past-due loans
- `member_loan_stats(loans_total, loans_open, last_loan_on)`: per-member activity
- `popular_books(book_id, title, author_name, times_loaned)`: most-borrowed `COUNT(*)>=5` last 365 days
- `fn_checkout_book(p_member_id, p_book_id, p_days) RETURNS BIGINT`: blocked/copy check, decrement, `INSERT loan`
- `sp_return_book(p_loan_id)`: set `returned_on`, increment `copies_free`
- `fn_late_fee(p_due_on, p_returned_on) RETURNS INTEGER`: overdue-days*50 capped 2000
- `trg_loan_audit_fn() / trg_loan_audit`: `AFTER INSERT OR UPDATE OF returned_on ON loan` audit insert
#### Data flow
- Checkout: `member.is_blocked` + `book.copies_free>0` → `fn_checkout_book` decrements `copies_free`, `INSERT loan(due_on=CURRENT_DATE+p_days)` → `trg_loan_audit` writes `CHECKOUT`
- Return: `sp_return_book` `UPDATE loan.returned_on` + increment `book.copies_free` → trigger writes `RETURN`
- Reporting: `loan JOIN member/book/author` → `overdue_loans`, `member_loan_stats`, `popular_books` aggregation
- Migration: `book.copies_total` via `generate_series` → `INSERT book_copy(barcode=isbn-n, status)` + `INSERT branch(Main)` + `UPDATE member.home_branch_id` → `ALTER loan ADD copy_id`, drop aggregate counters
#### Dependencies
- Internal: `book.author_id→author`, `loan.book_id→book`, `loan.member_id→member ON DELETE CASCADE`, `book_copy.book_id→book`, `book_copy.branch_id→branch`, `loan.copy_id→book_copy`, `member.home_branch_id→branch`, `loan_audit.loan_id→loan`
- External: PostgreSQL `BIGSERIAL/SMALLSERIAL`, `CHECK`, `UNIQUE`, partial `WHERE returned_on IS NULL` index, `PL/pgSQL`, `SQL IMMUTABLE`, `generate_series`, `CURRENT_DATE`, `now()`
#### Cross-cutting
- Constraint-driven integrity: `UNIQUE isbn/email/barcode`, `CHECK birth_year/copies/status/due_on`, `FK REFERENCES`
- Indexing: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_popular_books_id UNIQUE`, `idx_book_copy_book`
- Audit pattern: `loan AFTER INSERT OR UPDATE` trigger capturing state transitions `NULL-to-not-NULL`
- Idempotent seed: `BEGIN/COMMIT` bulk `INSERT` with fixed IDs, derived `DATE + N` loan dates
