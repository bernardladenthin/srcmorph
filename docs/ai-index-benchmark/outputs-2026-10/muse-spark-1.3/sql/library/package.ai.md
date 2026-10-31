### sql/library
- H: 1.0
- C: 71CC6A32
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:18:45Z
- G: 
- A: 0.0.0
- X: package
- F: [01_schema.sql](01_schema.sql.ai.md)
- F: [02_views.sql](02_views.sql.ai.md)
- F: [03_routines.sql](03_routines.sql.ai.md)
- F: [04_migration_v2.sql](04_migration_v2.sql.ai.md)
- F: [05_seed_data.sql](05_seed_data.sql.ai.md)
---
> Lending library circulation tracking book lending, returns, overdue fees, and branch inventory.
#### Purpose
- Manage lending library authors, books, members, loans with availability and overdue control
- Support branch expansion, circulation reporting, and demo seeding
#### Responsibilities
- Core entities: `author`, `book`, `member`, `loan` with `isbn`/`email` uniqueness, `copies_total`/`copies_free`, `due_on`/`returned_on` checks
- Branch inventory migration v2: `branch`, `book_copy` with `barcode`/`status`, `loan.copy_id`, `member.home_branch_id`, `member_since` rename, drop `copies_total`/`copies_free`
- Circulation operations: `fn_checkout_book` checkout, `sp_return_book` return, `fn_late_fee` fee calc, `loan_audit` + `trg_loan_audit` audit trail
- Reporting: `overdue_loans` overdue + `days_overdue`, `member_loan_stats` totals/open/last, `popular_books` materialized most-loaned past year
- Seed dataset: 15 `author`, 110 `book`, 50 `member`, 150 `loan` rows with date-offset history
#### Key units
- `author`: author identity with `full_name`, `birth_year`, `country_code`
- `book`: title/isbn inventory linked to `author_id`, pre-v2 `copies_total`/`copies_free`
- `member`: borrower with `email`, `is_blocked`, `joined_on`/`member_since`, `home_branch_id`
- `loan`: borrowing event with `book_id`/`copy_id`, `member_id`, `loaned_on`/`due_on`/`returned_on`
- `branch`: library location with `name`, `city`
- `book_copy`: physical copy with `book_id`, `branch_id`, `barcode`, `status` AVAILABLE/ON_LOAN/LOST
- `loan_audit`: loan lifecycle log with `loan_id`, `action` CHECKOUT/RETURN, `changed_at`
- `overdue_loans` view: open overdue loans with `email`, `title`, `days_overdue`
- `member_loan_stats` view: per-member `loans_total`, `loans_open`, `last_loan_on`
- `popular_books` materialized view: `times_loaned`, `author_name` last 365 days
- `fn_checkout_book(p_member_id, p_book_id, p_days)`: blocked/free-copy guard, decrement, INSERT `loan`, returns `v_loan_id`
- `sp_return_book(p_loan_id)`: validate open loan, set `returned_on`, increment `copies_free`
- `fn_late_fee(p_due_on, p_returned_on)`: 50 cents/day capped 2000, IMMUTABLE
- `trg_loan_audit_fn` + `trg_loan_audit`: AFTER INSERT OR UPDATE OF `returned_on` logging CHECKOUT/RETURN
#### Data flow
- Checkout: `member.is_blocked` + `book.copies_free` checks -> `book.copies_free` decrement -> `loan` INSERT with `due_on=CURRENT_DATE+p_days` -> `loan_audit` CHECKOUT
- Return: `loan.returned_on IS NULL` validation -> `loan.returned_on=CURRENT_DATE` -> `book.copies_free` increment -> `loan_audit` RETURN
- Fee: `loan.due_on` + `returned_on`/`CURRENT_DATE` -> `fn_late_fee` capped amount
- Reporting: `loan` JOIN `member`/`book`/`author` -> `overdue_loans`, `member_loan_stats`, `popular_books`
- Migration v2: `book.copies_total` x `generate_series` -> `book_copy` INSERT with generated `barcode`/`status` -> `loan.copy_id`, `member.home_branch_id` backfill -> drop aggregate counters
#### Dependencies
- Internal: `book.author_id->author`, `loan.book_id->book`, `loan.member_id->member`, `loan.copy_id->book_copy`, `book_copy.branch_id->branch`, `member.home_branch_id->branch`, `loan_audit.loan_id->loan`
- PostgreSQL: `BIGSERIAL`/`SMALLSERIAL`, `PL/pgSQL`, `CURRENT_DATE`/`now()`/`INTERVAL`, `COUNT FILTER`/`GROUP BY`/`HAVING`, `generate_series`, partial index `WHERE returned_on IS NULL`, `UNIQUE INDEX idx_popular_books_id`
- Indexes: `idx_book_author`, `idx_loan_member`, `idx_loan_open`, `idx_book_copy_book`
#### Cross-cutting
- Integrity via `CHECK`: `birth_year` range, `copies_free<=copies_total`, `due_on>loaned_on`, `action`/`status` enums, `UNIQUE isbn`/`email`/`barcode`
- Guard pattern in routines: reject `is_blocked` members, `copies_free>0` requirement, unknown/already-returned error
- Audit pattern: `FOR EACH ROW` trigger detecting `INSERT` vs `returned_on NULL->NOT NULL` transition
- Migration pattern: `CREATE TABLE` + `INSERT SELECT` expansion + `ALTER TABLE` FK/add/rename/drop + `CREATE INDEX`
