### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:35:41Z
- G: 
- A: 0.0.0
- X: file
---
> Lending-library routines: checkout and return of book copies, late-fee calculation, and a trigger-driven audit trail of loan events (PostgreSQL PL/pgSQL).

#### Purpose
- Checks out a free book copy to a member and records the loan; returns a loan and restocks the copy.
- Computes late fees in cents and logs CHECKOUT/RETURN events to `loan_audit` via trigger.

#### Type
- Multiple objects: table `loan_audit`, functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`, procedure `sp_return_book`, trigger `trg_loan_audit`.
- Dialect: PostgreSQL (PL/pgSQL, BIGSERIAL, TIMESTAMPTZ, `EXECUTE FUNCTION`).

#### Schema
- Table `loan_audit`:
  - `audit_id BIGSERIAL` PK
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL` CHECK in ('CHECKOUT', 'RETURN')
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member.member_id`, `member.is_blocked` (checkout block check)
- `book.book_id`, `book.copies_free` (availability check)
- `loan.loan_id`, `loan.book_id`, `loan.returned_on` (return lookup, trigger OLD/NEW)
- `loan.due_on`, `loan.returned_on` (inputs to `fn_late_fee`, passed as parameters)

#### Writes
- `book.copies_free`: decremented in `fn_checkout_book`, incremented in `sp_return_book`
- `loan`: INSERT in `fn_checkout_book` (`book_id`, `member_id`, `due_on`); UPDATE `returned_on` in `sp_return_book`
- `loan_audit`: INSERT from `trg_loan_audit_fn` (`loan_id`, `action`)

#### Relationships
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
- `loan_audit.loan_id -> loan.loan_id` (logical; no FK declared in this file)

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises if `member.is_blocked` is true for `p_member_id`.
  - Decrements `book.copies_free` where `copies_free > 0`; raises if no row updated.
  - Inserts `loan` with `due_on = CURRENT_DATE + p_days`; returns new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)` (procedure)
  - Sets `loan.returned_on = CURRENT_DATE` where `loan_id` matches and `returned_on IS NULL`, capturing `book_id`.
  - Raises if no row matched (unknown or already returned).
  - Increments `book.copies_free` for that `book_id`.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER` (IMMUTABLE, SQL)
  - Days overdue = `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`, floored at 0.
  - Fee = days × 50 cents, capped at 2000.
- `trg_loan_audit_fn()` (trigger function)
  - On INSERT: logs `loan_audit` action 'CHECKOUT'.
  - On UPDATE where `returned_on` changes from NULL to NOT NULL: logs action 'RETURN'.
  - Returns NEW.
- `trg_loan_audit` : AFTER INSERT OR UPDATE OF `returned_on` ON `loan`, FOR EACH ROW, executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit` (`member`, `book`, `loan` are not defined in this file)
- Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Trigger: `trg_loan_audit`
- Built-ins: `now()`, `CURRENT_DATE`, `LEAST`, `GREATEST`, `COALESCE`
