### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:43:22Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library book checkout, return, overdue-fee calculation, and loan audit logging.

#### Purpose
- Checkout an available book to an unblocked member, return a loan, and calculate capped late fees.
- Record checkout and return events in `loan_audit`.

#### Type
- PostgreSQL, PL/pgSQL
- Table: `loan_audit`
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Procedure: `sp_return_book`
- Trigger: `trg_loan_audit`

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key (unnamed constraint)
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')` (unnamed constraint)
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`
- Indexes: none declared.

#### Reads
- `member.member_id`, `member.is_blocked` — checkout eligibility check.
- `book.book_id`, `book.copies_free` — checkout availability predicate.
- `loan.loan_id`, `loan.returned_on` — return eligibility check.
- `loan.book_id` — returned from loan update.
- Trigger context: `TG_OP`, `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`.
- `fn_late_fee` inputs: `p_due_on`, `p_returned_on`; reads `CURRENT_DATE`.

#### Writes
- `book.copies_free` — decrement on successful checkout; increment on successful return.
- `loan.book_id`, `loan.member_id`, `loan.due_on` — checkout inserts 1 row per successful call.
- `loan.returned_on` — return procedure updates an eligible loan.
- `loan_audit.loan_id`, `loan_audit.action` — trigger inserts 1 row for each loan insert (`CHECKOUT`) or transition from not returned to returned (`RETURN`); exact total depends on triggering events.

#### Relationships
- `loan.book_id -> book.book_id` — checkout associates a loan with a book; return uses the loan’s book ID to restore availability.
- `loan.member_id -> member.member_id` — checkout associates a loan with a member.
- `loan_audit.loan_id -> loan.loan_id` — audit event records a loan ID. No foreign key is declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises an exception if the member is blocked or no free copy is available.
  - Decrements `book.copies_free`, inserts a loan due `CURRENT_DATE + p_days`, and returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `returned_on` to `CURRENT_DATE` for an unreturned loan; raises an exception if unknown or already returned.
  - Increments the associated book’s `copies_free`.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Immutable SQL function; calculates 50 cents per overdue day, with zero floor and 2000-cent cap. Uses `CURRENT_DATE` when `p_returned_on` is NULL.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts `CHECKOUT` audit entry on loan insert; inserts `RETURN` entry when `returned_on` changes from NULL to non-NULL; returns `NEW`.
- `trg_loan_audit`
  - AFTER INSERT OR UPDATE OF `returned_on` on `loan`, FOR EACH ROW; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Trigger function: `trg_loan_audit_fn`
- Built-ins: `now()`, `CURRENT_DATE`, `COALESCE`, `GREATEST`, `LEAST`
