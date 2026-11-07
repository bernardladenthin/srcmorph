### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:06:31Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Manages library book checkout and return operations, calculates overdue fees, and records loan audit events.

#### Purpose
- Checkout a free book to an unblocked member, return a loan and restore the copy, calculate late fees, and audit checkout/return events.
- Defines the `loan_audit` table used by the loan audit trigger.

#### Type
- PostgreSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.

#### Schema
- `loan_audit`:
  - `audit_id BIGSERIAL` — primary key; constraint name not declared.
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL` — unnamed CHECK constraint: `action IN ('CHECKOUT', 'RETURN')`.
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.

#### Reads
- `member.member_id`, `member.is_blocked` — checkout blocked-member check.
- `book.book_id`, `book.copies_free` — checkout availability check.
- `loan.loan_id`, `loan.returned_on` — return eligibility; `loan.book_id` returned by update.
- Function inputs: `p_due_on`, `p_returned_on` in `fn_late_fee`.

#### Writes
- `book.copies_free` — `fn_checkout_book` decrements; `sp_return_book` increments.
- `loan` — `fn_checkout_book` inserts `book_id`, `member_id`, `due_on`; `sp_return_book` sets `returned_on`.
- `loan_audit` — trigger function inserts `loan_id`, `action` (`CHECKOUT` on insert; `RETURN` when `returned_on` changes from NULL to non-NULL).

#### Relationships
- `loan.book_id -> book.book_id` — checkout and return logic.
- `loan_audit.loan_id -> loan.loan_id` — audit records reference loans in trigger logic; no FK constraint declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`: rejects blocked members; decrements available copies or raises an exception; inserts a loan with due date `CURRENT_DATE + p_days`; returns the new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`: sets `returned_on` to `CURRENT_DATE` for an unreturned loan; raises an exception for unknown/already-returned loans; restores the book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`: immutable SQL function; computes overdue days using return date or `CURRENT_DATE`, floors at zero, charges 50 cents per day, capped at 2000 cents.
- `trg_loan_audit_fn() RETURNS TRIGGER`: inserts a `CHECKOUT` audit row for loan inserts and a `RETURN` audit row when a loan becomes returned; returns `NEW`.
- `trg_loan_audit`: row-level AFTER INSERT or UPDATE OF `returned_on` trigger on `loan`, executing `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-in function: `now()`.
