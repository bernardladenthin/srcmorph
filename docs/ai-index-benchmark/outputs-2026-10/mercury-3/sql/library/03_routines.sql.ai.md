### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:32:51Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library routines for checking out and returning books, charging late fees, and recording an audit trail of loan actions.

#### Purpose
- Provides checkout/return business logic, late fee calculation, and automatic loan audit logging.

#### Type
- Mixed object file: table, SQL functions, procedure, trigger — PostgreSQL / PL/pgSQL dialect.

#### Schema
- `loan_audit`: `audit_id BIGSERIAL` PK, `loan_id BIGINT` NOT NULL, `action VARCHAR(10)` NOT NULL CHECK (`action` IN ('CHECKOUT','RETURN')), `changed_at TIMESTAMPTZ` NOT NULL DEFAULT `now()`.

#### Reads
- `fn_checkout_book`: reads `member` (`member_id`, `is_blocked`), `book` (`book_id`, `copies_free`).
- `sp_return_book`: reads `loan` (`loan_id`, `returned_on`, `book_id`), `book` (`book_id`).
- `fn_late_fee`: reads input parameters `p_due_on`, `p_returned_on`.
- `trg_loan_audit_fn`: reads `NEW`/`OLD` rows of `loan`.

#### Writes
- `fn_checkout_book`: `UPDATE book SET copies_free = copies_free - 1`, `INSERT INTO loan (book_id, member_id, due_on)`.
- `sp_return_book`: `UPDATE loan SET returned_on = CURRENT_DATE`, `UPDATE book SET copies_free = copies_free + 1`.
- `trg_loan_audit_fn`: `INSERT INTO loan_audit (loan_id, action)`.
- Trigger `trg_loan_audit` fires on `loan` after `INSERT` or `UPDATE OF returned_on`.

#### Relationships
- `loan.book_id -> book.book_id` (implied by update/insert usage).
- `loan.member_id -> member.member_id` (implied by member lookup).
- `loan_audit.loan_id -> loan.loan_id` (audit references loans).

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21)` returns `BIGINT`: raises if member is blocked; decrements `book.copies_free` only when `copies_free > 0`, else raises; inserts a `loan` row with `due_on = CURRENT_DATE + p_days`; returns new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`: updates `loan.returned_on = CURRENT_DATE` only where `returned_on IS NULL`, capturing `book_id`; raises if no row matched; increments `book.copies_free` for that book.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE)` returns `INTEGER`, `IMMUTABLE`, SQL language: `LEAST(GREATEST(COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, 0) * 50, 2000)` — 50 cents per overdue day, capped at 2000.
- `trg_loan_audit_fn()` returns `TRIGGER`: on `TG_OP = 'INSERT'` logs `'CHECKOUT'`; on transition from `OLD.returned_on IS NULL` to `NEW.returned_on IS NOT NULL` logs `'RETURN'`; returns `NEW`.

#### Dependencies
- References tables `member`, `book`, `loan`, `loan_audit`.
- Trigger `trg_loan_audit` depends on function `trg_loan_audit_fn`.
