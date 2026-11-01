### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:21:47Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library book checkout and return, calculates overdue fees, and records checkout/return events in an audit trail.

#### Purpose
- Lend available book copies to unblocked members and return copies to inventory.
- Calculate capped late fees and audit loan checkout/return events.

#### Type
- PostgreSQL (PL/pgSQL): table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key.
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`.
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.
- No explicitly declared indexes.

#### Reads
- `member.member_id`, `member.is_blocked` — `fn_checkout_book` checks whether the member is blocked.
- `book.book_id`, `book.copies_free` — `fn_checkout_book` checks copy availability.
- `loan.loan_id`, `loan.returned_on` — `sp_return_book` identifies active loans.
- `p_due_on`, `p_returned_on`, `CURRENT_DATE` — `fn_late_fee` computes overdue days.
- `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`, `TG_OP` — `trg_loan_audit_fn`.

#### Writes
- `book.copies_free` — `fn_checkout_book` decrements an available copy; `sp_return_book` increments the returned book’s available-copy count.
- `loan.book_id`, `loan.member_id`, `loan.due_on` — `fn_checkout_book` inserts one loan row per successful call.
- `loan.returned_on` — `sp_return_book` sets the return date for an unreturned loan.
- `loan_audit.loan_id`, `loan_audit.action` — `trg_loan_audit_fn` inserts one `CHECKOUT` row per loan insert, or one `RETURN` row when `returned_on` changes from NULL to non-NULL.

#### Relationships
- No foreign keys declared.
- `loan.book_id -> book.book_id` — used by checkout and return logic.
- `loan.member_id -> member.member_id` — used by checkout logic.
- `loan_audit.loan_id -> loan.loan_id` — audit association represented by `loan_id`; no FK declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises an exception if the member is blocked or no free copy is available.
  - Decrements `book.copies_free`, inserts a loan due on `CURRENT_DATE + p_days`, and returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `returned_on` to `CURRENT_DATE` for an unreturned loan and retrieves its `book_id`.
  - Raises an exception for an unknown or already returned loan; otherwise increments the book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Calculates overdue days using the return date or `CURRENT_DATE`, floors at zero, charges 50 cents per day, and caps the fee at 2000 cents.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts a `CHECKOUT` audit event for loan inserts.
  - Inserts a `RETURN` audit event when `returned_on` changes from NULL to non-NULL; returns `NEW`.
- `trg_loan_audit`
  - Fires after `INSERT` or update of `loan.returned_on`, once per row, executing `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Trigger function: `trg_loan_audit_fn`.
