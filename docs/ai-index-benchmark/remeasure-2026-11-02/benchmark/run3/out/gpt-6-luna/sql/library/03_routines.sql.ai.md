### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:58:36Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Implements library book checkout and return, calculates overdue fees, and records loan checkout and return events.

#### Purpose
- Manages loan lifecycle operations, including availability updates and blocked-member checks.
- Calculates capped late fees and audits loan checkouts and returns.

#### Type
- PostgreSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key, `NOT NULL`.
  - `loan_id BIGINT` — `NOT NULL`.
  - `action VARCHAR(10)` — `NOT NULL`, CHECK `action IN ('CHECKOUT', 'RETURN')`.
  - `changed_at TIMESTAMPTZ` — `NOT NULL`, DEFAULT `now()`.
- No indexes declared.

#### Reads
- `member.member_id`, `member.is_blocked` — `fn_checkout_book`.
- `book.book_id`, `book.copies_free` — `fn_checkout_book`.
- `loan.loan_id`, `loan.returned_on` — `sp_return_book`.
- `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`, `TG_OP` — `trg_loan_audit_fn`.
- `p_due_on`, `p_returned_on` — `fn_late_fee`.

#### Writes
- `book.copies_free` — `fn_checkout_book` decrements for an available book; `sp_return_book` increments after a successful return.
- `loan.book_id`, `loan.member_id`, `loan.due_on` — `fn_checkout_book` inserts 1 row per call.
- `loan.returned_on` — `sp_return_book` sets to `CURRENT_DATE` for an unreturned loan.
- `loan_audit.loan_id`, `loan_audit.action` — `trg_loan_audit_fn` inserts 1 row for a loan insert (`CHECKOUT`) or first transition to returned (`RETURN`).

#### Relationships
- `loan.book_id -> book.book_id` — checkout and return operations.
- `loan.member_id -> member.member_id` — checkout operation.
- `loan_audit.loan_id -> loan.loan_id` — audit event association; no foreign key is declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Rejects blocked members; decrements `book.copies_free` only when a copy is available, otherwise raises an exception.
  - Inserts a loan with due date `CURRENT_DATE + p_days`; returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `loan.returned_on` to `CURRENT_DATE` for an unreturned loan and captures its `book_id`.
  - Raises an exception for an unknown or already-returned loan; otherwise increments that book’s `copies_free`.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Computes nonnegative overdue days using `p_returned_on`, or `CURRENT_DATE` when null; charges 50 cents per day, capped at 2000 cents. Immutable.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts a `CHECKOUT` audit event on loan insertion; inserts a `RETURN` event when `returned_on` changes from null to non-null.
  - Returns `NEW`.
- `trg_loan_audit`
  - Fires after `INSERT` or `UPDATE OF returned_on` on `loan`, for each row; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-ins: `now()`, `CURRENT_DATE`, `COALESCE`, `GREATEST`, `LEAST`.
