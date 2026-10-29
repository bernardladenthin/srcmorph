### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:50:20Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library checkout and return processing, late-fee calculation, and loan-action auditing.

#### Purpose
- Decrements free book copies when a member checks out a book; records and returns loans.
- Calculates capped overdue fees and audits checkout and return events.

#### Type
- PostgreSQL: table, functions, procedure, trigger

#### Schema
- `loan_audit`:
  - `audit_id BIGSERIAL` — primary key
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`
- No indexes beyond the primary key are declared.

#### Reads
- `fn_checkout_book`: `member.member_id`, `member.is_blocked`; `book.book_id`, `book.copies_free`
- `sp_return_book`: `loan.loan_id`, `loan.returned_on`, `loan.book_id`
- `fn_late_fee`: input parameters `p_due_on`, `p_returned_on`; uses `CURRENT_DATE`
- `trg_loan_audit_fn`: trigger row fields `TG_OP`, `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`

#### Writes
- `fn_checkout_book`: updates `book.copies_free`; inserts `loan(book_id, member_id, due_on)`
- `sp_return_book`: updates `loan.returned_on`; increments `book.copies_free`
- `trg_loan_audit_fn`: inserts `loan_audit(loan_id, action)` with `CHECKOUT` or `RETURN`
- `trg_loan_audit`: fires after inserts or updates of `loan.returned_on`

#### Relationships
- No foreign-key constraints are declared.
- `loan_audit.loan_id` stores the loan ID used by the loan audit trigger; no FK is declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`:
  - Rejects blocked members.
  - Decrements `book.copies_free` only when a free copy exists; raises an exception otherwise.
  - Creates a loan due on `CURRENT_DATE + p_days`; returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`:
  - Sets `loan.returned_on` to `CURRENT_DATE` if the loan has not already been returned.
  - Raises an exception for unknown or already-returned loans; restores one free book copy.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`:
  - Computes overdue days using `p_returned_on` or `CURRENT_DATE`; charges 50 cents per day, minimum 0 and maximum 2000.
  - Declared `IMMUTABLE`.
- `trg_loan_audit_fn() RETURNS TRIGGER`:
  - On loan insert, records `CHECKOUT`.
  - On transition from `returned_on IS NULL` to non-null, records `RETURN`.
  - Returns `NEW`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Trigger: `trg_loan_audit` on `loan`; executes `trg_loan_audit_fn`
