### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:37:06Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Implements library book checkout and return, calculates overdue fees, and records checkout/return events for loan auditing.

#### Purpose
- Manage loan creation and return, including available-copy counts and blocked-member checks.
- Calculate capped late fees and audit loan lifecycle changes.

#### Type
- PostgreSQL: table, PL/pgSQL and SQL functions, procedure, trigger.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key (unnamed constraint).
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL` — CHECK (unnamed constraint): `CHECKOUT`, `RETURN`.
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.
- No indexes declared.

#### Reads
- `member.member_id`, `member.is_blocked` — checkout eligibility.
- `book.book_id`, `book.copies_free` — checkout availability.
- `loan.loan_id`, `loan.returned_on` — return eligibility.
- Function inputs `p_due_on`, `p_returned_on`; trigger inputs `TG_OP`, `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`.

#### Writes
- `book.copies_free` — checkout decrements when positive; return increments.
- `loan.book_id`, `loan.member_id`, `loan.due_on` — checkout inserts 1 loan row.
- `loan.returned_on` — return procedure updates an unreturned loan to `CURRENT_DATE`.
- `loan_audit.loan_id`, `loan_audit.action` — trigger inserts 1 `CHECKOUT` row per loan insert or 1 `RETURN` row when `returned_on` transitions from NULL to non-NULL.

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` — audit association; no foreign-key constraint declared.
- `loan.book_id -> book.book_id` — return procedure uses the loan’s book to restore a copy; no foreign-key constraint declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises an exception for a blocked member or when no free copy can be decremented.
  - Inserts a loan due on `CURRENT_DATE + p_days`; returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Marks an unreturned loan returned today; raises an exception if unknown or already returned.
  - Increments the corresponding book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Immutable SQL function; computes nonnegative overdue days using `CURRENT_DATE` when return date is NULL, charges 50 cents per day, capped at 2000 cents.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts `CHECKOUT` audit on loan insertion; inserts `RETURN` audit when `returned_on` changes from NULL to non-NULL; returns `NEW`.
- `trg_loan_audit`
  - Row-level AFTER INSERT or UPDATE OF `loan.returned_on`; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-in: `now()`, `CURRENT_DATE`, `COALESCE`, `GREATEST`, `LEAST`.
