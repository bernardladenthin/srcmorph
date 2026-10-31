### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:23:11Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library checkout and return with overdue late-fee calculation and checkout/return audit trail.

#### Purpose
- Lend free `book` copies to `member` via `loan`, handle returns, compute overdue fees, audit `loan` lifecycle.
- Provide `loan_audit` trail for `CHECKOUT` / `RETURN` actions.

#### Type
- table / function / procedure / trigger, PostgreSQL PL/pgSQL + SQL.

#### Schema
- `loan_audit`: `audit_id BIGSERIAL` PK, `loan_id BIGINT` NOT NULL, `action VARCHAR(10)` NOT NULL CHECK `CHECKOUT`, `RETURN`, `changed_at TIMESTAMPTZ` NOT NULL DEFAULT `now()`.

#### Reads
- `member.member_id`, `member.is_blocked` in `fn_checkout_book` blocked-member check.
- `book.book_id`, `book.copies_free` in `fn_checkout_book` availability decrement, in `sp_return_book` copy restore.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` in `sp_return_book` return marking.
- `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on` in `trg_loan_audit_fn` transition detection.
- `p_due_on`, `p_returned_on` dates in `fn_late_fee`, uses `CURRENT_DATE` when null.

#### Writes
- `loan.book_id`, `loan.member_id`, `loan.due_on`, `loan.loan_id` via INSERT in `fn_checkout_book`.
- `book.copies_free` via UPDATE decrement in `fn_checkout_book`, increment in `sp_return_book`.
- `loan.returned_on` via UPDATE in `sp_return_book`.
- `loan_audit.loan_id`, `loan_audit.action` via INSERT in `trg_loan_audit_fn` on `CHECKOUT`, `RETURN`.

#### Relationships
- `loan_audit.loan_id` -> `loan.loan_id`.
- `loan.book_id` -> `book.book_id`.
- `loan.member_id` -> `member.member_id`.
- `book.book_id` -> `book.copies_free` stock adjustment.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`: rejects if `member.is_blocked`, decrements `book.copies_free` where `copies_free > 0`, inserts `loan` with `due_on = CURRENT_DATE + p_days`, returns `v_loan_id`.
- `sp_return_book(p_loan_id BIGINT)`: sets `loan.returned_on = CURRENT_DATE` where `returned_on IS NULL`, restores `book.copies_free + 1`, raises if unknown/already returned.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER IMMUTABLE`: `50` cents per overdue day, `LEAST(...,2000)` cap, `GREATEST(...,0)` floor.
- `trg_loan_audit_fn() RETURNS TRIGGER`: on `INSERT` logs `CHECKOUT`, on `returned_on NULL->NOT NULL` logs `RETURN` into `loan_audit`, returns `NEW`.
- `trg_loan_audit AFTER INSERT OR UPDATE OF returned_on ON loan FOR EACH ROW`: executes `trg_loan_audit_fn()`.

#### Dependencies
- `member`, `book`, `loan`, `loan_audit`.
- `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`, `trg_loan_audit`.
