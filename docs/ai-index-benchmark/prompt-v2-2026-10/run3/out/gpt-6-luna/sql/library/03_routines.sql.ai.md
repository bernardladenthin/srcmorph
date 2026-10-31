### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:59:09Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library book checkout and return, overdue-fee calculation, and loan action auditing.

#### Purpose
- Checks out available book copies to unblocked members and returns copies to inventory.
- Calculates capped late fees and records checkout/return actions in `loan_audit`.

#### Type
- PostgreSQL, PL/pgSQL/SQL: table, functions, procedure, trigger

#### Schema
- `loan_audit`:
  - `audit_id BIGSERIAL` — primary key; constraint name not declared
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')`; constraint name not declared
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`
- No explicitly named indexes.

#### Reads
- `member.member_id`, `member.is_blocked` — `fn_checkout_book`
- `book.book_id`, `book.copies_free` — `fn_checkout_book`; `book.book_id` — `sp_return_book`
- `loan.loan_id`, `loan.returned_on` — `sp_return_book`
- `loan` trigger row values: `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`
- Function inputs: `p_due_on`, `p_returned_on` — `fn_late_fee`

#### Writes
- `book.copies_free` — `fn_checkout_book` decrements by one; `sp_return_book` increments by one.
- `loan.book_id`, `loan.member_id`, `loan.due_on` — `fn_checkout_book`; inserts one loan row per successful invocation.
- `loan.returned_on` — `sp_return_book`, set to `CURRENT_DATE` for an unreturned loan.
- `loan_audit.loan_id`, `loan_audit.action` — `trg_loan_audit_fn`; inserts one row for each loan INSERT (`CHECKOUT`) or qualifying return transition (`RETURN`).

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` — audit association; no foreign key declared.
- `loan.book_id -> book.book_id` — used by checkout/return logic; no foreign key declared.
- `loan.member_id -> member.member_id` — used by checkout logic; no foreign key declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises an exception if the member is blocked or no free copy is available.
  - Decrements `book.copies_free`, inserts a loan due `p_days` after `CURRENT_DATE`, and returns the new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `loan.returned_on` to `CURRENT_DATE` if the loan is not already returned.
  - Raises an exception for an unknown or already-returned loan; otherwise increments the associated book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Returns 50 cents per overdue day, using `CURRENT_DATE` when `p_returned_on` is null; floors at zero and caps at 2000 cents.
  - Declared `IMMUTABLE`.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts a `CHECKOUT` audit row for a loan INSERT.
  - Inserts a `RETURN` audit row when `returned_on` changes from null to non-null; returns `NEW`.
- `trg_loan_audit`
  - Fires after INSERT or UPDATE OF `returned_on` on `loan`, once per row; executes `trg_loan_audit_fn`.

#### Dependencies
- `member`, `book`, `loan`, `loan_audit`
- Built-ins: `now()`, `CURRENT_DATE`
