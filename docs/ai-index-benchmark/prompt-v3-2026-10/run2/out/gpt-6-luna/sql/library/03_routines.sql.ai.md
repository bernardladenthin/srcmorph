### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:12:01Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library book checkout and return workflows, calculates overdue fees, and records loan checkout/return events for audit.

#### Purpose
- Checkout a free book to an unblocked member, return a loan and restore inventory, calculate late fees, and audit loan events.

#### Type
- PostgreSQL: table `loan_audit`, functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`, procedure `sp_return_book`, trigger `trg_loan_audit` (PL/pgSQL except `fn_late_fee`, which is SQL).

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key (constraint name not declared).
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')` (constraint name not declared).
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.
- Index: `loan_audit_pkey` — `loan_audit(audit_id)`, implicit primary-key index; method not specified.

#### Reads
- `fn_checkout_book`: `member.member_id`, `member.is_blocked`; `book.book_id`, `book.copies_free`.
- `sp_return_book`: `loan.loan_id`, `loan.returned_on`, `loan.book_id`; `book.book_id`.
- `fn_late_fee`: parameters `p_due_on`, `p_returned_on`; uses `CURRENT_DATE`.
- `trg_loan_audit_fn`: trigger operation `TG_OP`; `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`.

#### Writes
- `fn_checkout_book`: updates `book.copies_free`; inserts one row into `loan(book_id, member_id, due_on)` per successful call.
- `sp_return_book`: updates `loan.returned_on`; updates `book.copies_free`.
- `trg_loan_audit_fn`: inserts one `loan_audit(loan_id, action)` row with `CHECKOUT` per `INSERT` trigger event; inserts one row with `RETURN` when `returned_on` changes from NULL to non-NULL.
- `trg_loan_audit`: fires after `INSERT` or `UPDATE OF returned_on` on `loan`; row-level execution of `trg_loan_audit_fn`.

#### Relationships
- Trigger audit linkage: `loan.loan_id -> loan_audit.loan_id` (inserted as audit data; no foreign-key constraint declared).

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`: rejects blocked members; decrements available copies or raises if none; inserts a loan due `CURRENT_DATE + p_days`; returns the new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`: marks an unreturned loan returned today and restores its book’s free-copy count; raises if the loan is unknown or already returned.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`: computes overdue days using return date or current date, floors at zero, charges 50 cents per day, caps at 2000; immutable.
- `trg_loan_audit_fn() RETURNS TRIGGER`: records checkout on loan insertion and return when `returned_on` transitions from NULL; returns `NEW`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-ins: `now()`, `CURRENT_DATE`, `COALESCE`, `GREATEST`, `LEAST`.
