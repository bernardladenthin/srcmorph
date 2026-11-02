### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:37:47Z
- G: 
- A: 0.0.0
- X: file
---
> Manages library book checkouts and returns, tracks loan audit events, and calculates capped overdue fees.

#### Purpose
- `fn_checkout_book` checks out an available book to an unblocked member and returns the new loan ID.
- `sp_return_book` records a loan return and restores book availability; loan changes are audited and late fees can be calculated.

#### Type
- PostgreSQL / PL/pgSQL.
- Table: `loan_audit`.
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`.
- Procedure: `sp_return_book`.
- Trigger: `trg_loan_audit`.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key `loan_audit_pkey`.
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))` — check constraint `loan_audit_action_check`.
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.
- No separately declared indexes.

#### Reads
- `member.member_id`, `member.is_blocked` — `fn_checkout_book`.
- `book.book_id`, `book.copies_free` — `fn_checkout_book`, `sp_return_book`.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — `sp_return_book`.
- `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on` — `trg_loan_audit_fn`.
- `p_due_on`, `p_returned_on`, `CURRENT_DATE` — `fn_late_fee`.

#### Writes
- `book.copies_free` — decrement during checkout; increment during return.
- `loan.book_id`, `loan.member_id`, `loan.due_on` — `fn_checkout_book` inserts one loan row per successful call.
- `loan.returned_on` — `sp_return_book` updates one matching unreturned loan per successful call.
- `loan_audit.loan_id`, `loan_audit.action` — trigger inserts one `CHECKOUT` row per loan insert; inserts one `RETURN` row when `returned_on` changes from NULL to non-NULL.
- Runtime row counts are conditional; no fixed data inserts are present.

#### Relationships
- `loan.book_id -> book.book_id` — checkout inserts the association; return uses it to restore availability.
- `loan.member_id -> member.member_id` — checkout associates the loan with a member.
- `loan_audit.loan_id -> loan.loan_id` — audit events identify the loan; no foreign key is declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises an exception if the member is blocked or no free copy is available.
  - Decrements `book.copies_free`, inserts a loan due on `CURRENT_DATE + p_days`, and returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `loan.returned_on` to `CURRENT_DATE` for an unreturned loan.
  - Raises an exception if the loan is unknown or already returned; otherwise increments the associated book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Immutable SQL function; charges 50 cents per overdue day, using `CURRENT_DATE` when `p_returned_on` is NULL; result is floored at zero and capped at 2000 cents.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts a `CHECKOUT` audit row on loan insertion.
  - Inserts a `RETURN` audit row when `returned_on` transitions from NULL to non-NULL; returns `NEW`.
- `trg_loan_audit`
  - Fires after `INSERT` or update of `loan.returned_on`, once per row, and executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Trigger function: `trg_loan_audit_fn`.
- Built-ins: `now()`, `CURRENT_DATE`, `COALESCE`, `GREATEST`, `LEAST`.
