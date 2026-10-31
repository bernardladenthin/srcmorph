### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:32:32Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library checkout and return workflows, overdue-fee calculation, and loan audit logging.

#### Purpose
- Check out available book copies to unblocked members and return copies to inventory.
- Calculate capped late fees and record checkout/return events in `loan_audit`.

#### Type
- PostgreSQL: table, functions, procedure, trigger

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key; constraint name not declared
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')`; constraint name not declared
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`
- No indexes explicitly declared.

#### Reads
- `member.member_id`, `member.is_blocked` — `fn_checkout_book`
- `book.book_id`, `book.copies_free` — checkout availability update; `book.book_id` — return copy update
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — `sp_return_book`
- `loan` row fields `loan_id`, `returned_on` — trigger via `NEW` and `OLD`
- `p_due_on`, `p_returned_on` — `fn_late_fee`

#### Writes
- `book.copies_free` — `fn_checkout_book` decrements by 1 when a free copy exists.
- `loan(book_id, member_id, due_on)` — `fn_checkout_book` inserts 1 row per successful call.
- `loan.returned_on` — `sp_return_book` sets to `CURRENT_DATE` for an unreturned loan.
- `book.copies_free` — `sp_return_book` increments by 1 after a successful return.
- `loan_audit(loan_id, action)` — `trg_loan_audit_fn` inserts 1 row for a loan insert (`CHECKOUT`), or 1 row when `returned_on` changes from NULL to non-NULL (`RETURN`).

#### Relationships
- Trigger audit association: `loan_audit.loan_id -> loan.loan_id` (logical; no foreign key declared)

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises an exception if the member is blocked or no free copy is available.
  - Decrements `book.copies_free`, inserts a loan due `CURRENT_DATE + p_days`, and returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Marks an unreturned loan returned today and restores one free book copy.
  - Raises an exception if the loan is unknown or already returned.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Computes 50 cents per overdue day, using today when `p_returned_on` is NULL; result is floored at 0 and capped at 2000. Marked `IMMUTABLE`.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts a checkout audit row on loan insertion and a return audit row when `returned_on` transitions from NULL to non-NULL; returns `NEW`.
- `trg_loan_audit`
  - Fires after `INSERT` or `UPDATE OF returned_on` on `loan`, once per row; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Procedure: `sp_return_book`
- Trigger: `trg_loan_audit`
