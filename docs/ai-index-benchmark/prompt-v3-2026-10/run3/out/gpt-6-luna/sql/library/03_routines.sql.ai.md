### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:36:48Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library book checkout and return workflows, calculates overdue fees, and records checkout/return events for loan auditing.

#### Purpose
- Tracks loan audit events and provides routines for checking out books, returning loans, and calculating late fees.
- Audits loan inserts and transitions from unreturned to returned.

#### Type
- PostgreSQL: table, functions, procedure, trigger
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Procedure: `sp_return_book`
- Trigger: `trg_loan_audit`

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key; constraint name not declared
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL` — CHECK (`action IN ('CHECKOUT', 'RETURN')`); constraint name not declared
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`
- Index: `loan_audit_pkey` — `loan_audit(audit_id)`; primary-key index, default method

#### Reads
- `fn_checkout_book`: `member.member_id`, `member.is_blocked`; `book.book_id`, `book.copies_free`
- `sp_return_book`: `loan.loan_id`, `loan.returned_on`, `loan.book_id`; `book.book_id`
- `fn_late_fee`: parameters `p_due_on`, `p_returned_on`; reads `CURRENT_DATE`
- `trg_loan_audit_fn`: trigger record fields `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`; reads `TG_OP`

#### Writes
- `fn_checkout_book`: updates `book.copies_free`; inserts one row per successful call into `loan(book_id, member_id, due_on)`; returns inserted `loan_id`
- `sp_return_book`: updates `loan.returned_on`; updates `book.copies_free`
- `trg_loan_audit_fn`: inserts one `loan_audit(loan_id, action)` row for an `INSERT` event, or one row when `returned_on` changes from NULL to non-NULL; action is `CHECKOUT` or `RETURN`
- `trg_loan_audit`: fires after `INSERT` or `UPDATE OF returned_on` on `loan`

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` — audit association; no foreign-key constraint declared

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Rejects blocked members; decrements one available book copy or raises an exception if none is available.
  - Creates a loan due `CURRENT_DATE + p_days`; returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `returned_on` to `CURRENT_DATE` for an unreturned loan; raises an exception if unknown or already returned.
  - Increments the corresponding book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Immutable SQL function; calculates 50 cents per overdue day, floored at zero and capped at 2000 cents; uses `CURRENT_DATE` when return date is NULL.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Records `CHECKOUT` on loan insertion and `RETURN` when a loan transitions from unreturned to returned; returns `NEW`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Built-in: `now()`, `CURRENT_DATE`, `COALESCE`, `GREATEST`, `LEAST`
