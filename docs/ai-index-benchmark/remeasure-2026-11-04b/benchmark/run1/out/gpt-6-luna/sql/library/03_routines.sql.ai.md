### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:48:47Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Implements library book checkout and return workflows, calculates overdue lending fees, and records checkout/return events for audit.

#### Purpose
- Manage book-copy availability and loan lifecycle for library members.
- Calculate capped late fees and audit loan checkouts and returns.

#### Type
- PostgreSQL; table, functions, procedure, trigger function, trigger.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key; `NOT NULL`; implicit sequence-backed default.
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`
  - Primary-key constraint: generated name `loan_audit_pkey`; CHECK constraint is unnamed.
  - Index: generated `loan_audit_pkey`, on `loan_audit(audit_id)`, unique B-tree.

#### Reads
- `fn_checkout_book`: `member(member_id, is_blocked)`; `book(book_id, copies_free)`.
- `sp_return_book`: `loan(loan_id, returned_on, book_id)`.
- `fn_late_fee`: parameters `p_due_on`, `p_returned_on`; `CURRENT_DATE`.
- `trg_loan_audit_fn`: trigger row fields `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`; trigger operation `TG_OP`.

#### Writes
- `fn_checkout_book`: updates `book.copies_free`; inserts one `loan` row with `book_id`, `member_id`, `due_on`; returns `loan.loan_id`.
- `sp_return_book`: updates `loan.returned_on`; updates `book.copies_free`.
- `trg_loan_audit_fn`: inserts one `loan_audit` row per matching trigger event: `CHECKOUT` on insert, or `RETURN` when `returned_on` changes from NULL to non-NULL.

#### Relationships
- `trg_loan_audit_fn`: `loan.loan_id -> loan_audit.loan_id` (audit association; no foreign key declared).
- `fn_checkout_book`: `member.member_id -> loan.member_id`; `book.book_id -> loan.book_id` (workflow associations; no foreign keys declared).
- `sp_return_book`: `loan.book_id -> book.book_id` (workflow association; no foreign key declared).

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Rejects blocked members; decrements one free book copy if available, otherwise raises an exception.
  - Inserts a loan due `p_days` after `CURRENT_DATE`; returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `returned_on` to `CURRENT_DATE` for an unreturned loan; raises an exception if unknown or already returned.
  - Increments the corresponding book’s `copies_free`.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Immutable SQL function; computes overdue days using `p_returned_on` or `CURRENT_DATE`, floors at zero, charges 50 cents per day, and caps at 2000 cents.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts a `CHECKOUT` audit event after loan insertion; inserts a `RETURN` event when a loan becomes returned; returns `NEW`.
- `trg_loan_audit`
  - Row-level `AFTER INSERT OR UPDATE OF returned_on` trigger on `loan`; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-ins: `now()`, `CURRENT_DATE`, `COALESCE`, `GREATEST`, `LEAST`.
