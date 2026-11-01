### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:59:10Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library checkout and return workflows, calculates overdue fees, and records loan checkout/return events for auditing.

#### Purpose
- Checks out available book copies to unblocked members, returns loans, and restores inventory.
- Calculates capped late fees and records loan lifecycle events in an audit table.

#### Type
- PostgreSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.
- PL/pgSQL routines: `fn_checkout_book`, `sp_return_book`, `trg_loan_audit_fn`; SQL function: `fn_late_fee`.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key (`loan_audit_pkey`, implicit); not null.
  - `loan_id BIGINT` — not null.
  - `action VARCHAR(10)` — not null; CHECK `action IN ('CHECKOUT', 'RETURN')` (implicit name `loan_audit_action_check`).
  - `changed_at TIMESTAMPTZ` — not null; DEFAULT `now()`.
- No explicit indexes declared.

#### Reads
- `member.member_id`, `member.is_blocked` — `fn_checkout_book`.
- `book.book_id`, `book.copies_free` — `fn_checkout_book`; `book.book_id`, `book.copies_free` — `sp_return_book`.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — `sp_return_book`.
- `p_due_on`, `p_returned_on` — `fn_late_fee`.
- Trigger row fields `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on` — `trg_loan_audit_fn`.

#### Writes
- `book.copies_free` — `fn_checkout_book` decrements for an available copy; `sp_return_book` increments after a successful return.
- `loan.book_id`, `loan.member_id`, `loan.due_on` — `fn_checkout_book` inserts one loan per successful call.
- `loan.returned_on` — `sp_return_book` sets the current date for an unreturned loan.
- `loan_audit.loan_id`, `loan_audit.action` — trigger function inserts a `CHECKOUT` audit row on loan insert or a `RETURN` audit row when `returned_on` changes from NULL to non-NULL; one row per qualifying trigger event.

#### Relationships
- `loan.book_id -> book.book_id` — checkout associates a loan with a book; return uses the loan’s book ID to restore inventory.
- `loan.member_id -> member.member_id` — checkout associates a loan with a member.
- `loan_audit.loan_id -> loan.loan_id` — audit rows identify loans; no foreign-key constraint is declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Rejects blocked members; decrements `book.copies_free` only when positive and raises an exception if no copy is available.
  - Inserts a loan due on `CURRENT_DATE + p_days`; returns the new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `loan.returned_on` to `CURRENT_DATE` for an unreturned loan; raises an exception if unknown or already returned.
  - Increments `book.copies_free` for the loan’s book.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Computes overdue days using `p_returned_on` or `CURRENT_DATE`, floors at zero, charges 50 cents per day, and caps at 2000; immutable.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts `CHECKOUT` audit rows for loan inserts and `RETURN` audit rows when `returned_on` transitions from NULL to non-NULL; returns `NEW`.
- `trg_loan_audit`
  - Fires after `INSERT` or `UPDATE OF returned_on` on `loan`, for each row; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Trigger function: `trg_loan_audit_fn`.
