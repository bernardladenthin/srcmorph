### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:25:47Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Lending library circulation for cataloging authors/books, managing members/loans, and tracking overdues, popularity, and late fees across branches.
#### Purpose
- Manage lending-library inventory and circulation: `author`, `book`, `member`, `loan` with copy availability and loan-date rules.
- Support branch expansion, overdue/popularity reporting, checkout/return operations, and seeded test dataset.
#### Responsibilities
- Core schema (`01_schema.sql`): `author`, `book`, `member`, `loan` tables with `isbn`/`email` uniqueness, `copies_total`/`copies_free`, `loan_due_after_loan`, `book_free_le_total`, indexes `idx_book_author`, `idx_loan_member`, `idx_loan_open`.
- Reporting views (`02_views.sql`): `overdue_loans` with `days_overdue`, `member_loan_stats` with `loans_total`/`loans_open`/`last_loan_on`, `MATERIALIZED VIEW popular_books` with `times_loaned`.
- Circulation routines (`03_routines.sql`): `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`/`trg_loan_audit` plus `loan_audit` trail for `CHECKOUT`/`RETURN`.
- Branch migration v2 (`04_migration_v2.sql`): new `branch`, `book_copy` with `barcode`/`status`, `loan.copy_id`, `member.home_branch_id`/`member_since`; drops `book.copies_total`/`copies_free`.
- Seed data (`05_seed_data.sql`): transactional `BEGIN`–`COMMIT` load of 15 `author`, 110 `book`, 50 `member`, 150 `loan` rows with open/overdue `returned_on NULL`.
#### Key units
- `author(author_id, full_name, birth_year, country_code)`: catalog author identity.
- `book(book_id, isbn, title, author_id, published_year, copies_total, copies_free)`: title inventory with per-book counters (pre-v2).
- `member(member_id, email, full_name, joined_on/member_since, is_blocked, home_branch_id)`: borrower account with block flag.
- `loan(loan_id, book_id, copy_id, member_id, loaned_on, due_on, returned_on)`: circulation record, copy-level after v2.
- `branch(branch_id, name, city)`: library location, seeded `Main/Springfield`.
- `book_copy(copy_id, book_id, branch_id, barcode, status)`: individual copy with `AVAILABLE`/`ON_LOAN`/`LOST`.
- `loan_audit(audit_id, loan_id, action, changed_at)`: `CHECKOUT`/`RETURN` lifecycle log.
- `overdue_loans`: unreturned past-due joins `loan`/`member`/`book` with `CURRENT_DATE - due_on`.
- `member_loan_stats`: per-member `COUNT(*)`, `FILTER WHERE returned_on IS NULL`, `MAX(loaned_on)`.
- `popular_books`: yearly `COUNT(*) HAVING >=5` grouped by `book_id/title/author_name`, with `UNIQUE INDEX idx_popular_books_id`.
- `fn_checkout_book(p_member_id, p_book_id, p_days DEFAULT 21) RETURNS BIGINT`: blocked/availability checks, `copies_free` decrement, `loan` insert.
- `sp_return_book(p_loan_id)`: `returned_on=CURRENT_DATE` marking, `copies_free+1` restore.
- `fn_late_fee(p_due_on, p_returned_on) RETURNS INTEGER IMMUTABLE`: 50c/day, `LEAST(...,2000)` cap, `GREATEST(...,0)` floor.
- `trg_loan_audit AFTER INSERT OR UPDATE OF returned_on ON loan`: logs `INSERT` as `CHECKOUT`, `NULL->NOT NULL` as `RETURN`.
#### Data flow
- Checkout: `member.is_blocked` + `book.copies_free>0` check → `UPDATE book.copies_free-1` → `INSERT loan(due_on=CURRENT_DATE+p_days)` → `INSERT loan_audit CHECKOUT` via trigger.
- Return: `UPDATE loan.returned_on` where `IS NULL` → `UPDATE book.copies_free+1` → `INSERT loan_audit RETURN`; `fn_late_fee(due_on, returned_on/CURRENT_DATE)` computes overdues.
- Reporting: `loan` ⋈ `member`/`book`/`author` → `overdue_loans`, `member_loan_stats`, `popular_books` aggregations.
- Migration v2: `book CROSS JOIN LATERAL generate_series(1,copies_total)` → `INSERT book_copy(isbn||'-'||n, AVAILABLE/ON_LOAN)` at `branch_id=1` → `UPDATE member.home_branch_id=1` → `ADD loan.copy_id` → `DROP book.copies_*`.
#### Dependencies
- Internal: `book.author_id→author`, `loan.book_id→book`, `loan.member_id→member ON DELETE CASCADE`, `book_copy→book/branch`, `loan.copy_id→book_copy`, `member.home_branch_id→branch`, `loan_audit→loan`.
- External/built-in: PostgreSQL `BIGSERIAL/SMALLSERIAL`, `CHECK`, partial index `WHERE returned_on IS NULL`, `PL/pgSQL`, `SQL`, `generate_series`, `now()`, `CURRENT_DATE`.
#### Cross-cutting
- Key conventions: `BIGSERIAL` PKs (`author_id, book_id, member_id, loan_id, copy_id, audit_id`), `REFERENCES` FKs, `UNIQUE isbn/email/barcode/branch.name`, `CHECK` on `birth_year/copies/status/action/due_on`.
- Indexing: `idx_book_author`, `idx_loan_member`, `idx_loan_open(due_on) WHERE returned_on IS NULL`, `idx_book_copy_book(book_id)`, `idx_popular_books_id`.
- Error handling: `RAISE` on blocked member, zero `copies_free`, unknown/already-returned `loan` in `fn_checkout_book`/`sp_return_book`.
