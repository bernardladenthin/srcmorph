### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:46:01Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library book checkout and return, overdue fee calculation, and loan activity auditing.

#### Purpose
- Tracks loan audit events for checkouts and returns.
- Provides routines to check out and return books and calculate capped late fees.

#### Type
- PostgreSQL / PL/pgSQL: table, functions, procedure, trigger function, trigger.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key; constraint name not declared.
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')`; constraint name not declared.
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`
  - No explicit indexes declared.

#### Reads
- `member.member_id`, `member.is_blocked` — checked by `fn_checkout_book`.
- `book.book_id`, `book.copies_free` — checkout availability check; `book.book_id` used to identify the returned loan’s copy.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — return lookup and update.
- `p_due_on`, `p_returned_on`, `CURRENT_DATE` — inputs and date source for `fn_late_fee`.
- Trigger context: `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`.

#### Writes
- `book.copies_free` — decremented by `fn_checkout_book`; incremented by `sp_return_book`.
- `loan.book_id`, `loan.member_id`, `loan.due_on` — `fn_checkout_book` inserts one loan row per successful call.
- `loan.returned_on` — `sp_return_book` updates one matching unreturned loan row.
- `loan_audit.loan_id`, `loan_audit.action` — `trg_loan_audit_fn` inserts one row for an inserted loan (`CHECKOUT`) or a newly returned loan (`RETURN`); otherwise inserts zero rows.

#### Relationships
- No foreign keys are declared.
- `loan_audit.loan_id` records the corresponding `loan.loan_id` in trigger logic.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises an exception if the member is blocked.
  - Decrements `book.copies_free` only when a free copy exists; raises an exception otherwise.
  - Inserts a `loan` with `due_on = CURRENT_DATE + p_days`; returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `returned_on` to `CURRENT_DATE` for a matching unreturned loan and retrieves its `book_id`.
  - Raises an exception for an unknown or already returned loan.
  - Increments the corresponding `book.copies_free`.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Computes overdue days using `p_returned_on` or `CURRENT_DATE`, floors at zero, charges 50 cents per day, and caps the result at 2000 cents.
  - Declared `IMMUTABLE`.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - On loan insert, writes a `CHECKOUT` audit event.
  - On transition from `returned_on IS NULL` to non-NULL, writes a `RETURN` audit event.
  - Returns `NEW`.
- `trg_loan_audit`
  - Row-level `AFTER INSERT OR UPDATE OF returned_on` trigger on `loan`; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`.
- Trigger: `trg_loan_audit`.
