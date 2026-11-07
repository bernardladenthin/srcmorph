### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:58:17Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Implements library checkout and return workflows, calculates overdue fees, and records loan checkout/return events for auditing.

#### Purpose
- Enforce member blocking and copy availability during checkout; restore copy availability on return.
- Calculate capped late fees and maintain a loan action audit trail.

#### Type
- PostgreSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.

#### Schema
- `loan_audit`:
  - `audit_id BIGSERIAL` — primary key; unnamed constraint.
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL` — unnamed CHECK: `action IN ('CHECKOUT', 'RETURN')`.
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.

#### Reads
- `fn_checkout_book`: `member.member_id`, `member.is_blocked`; `book.book_id`, `book.copies_free`.
- `sp_return_book`: `loan.loan_id`, `loan.returned_on`, `loan.book_id`.
- `fn_late_fee`: parameters `p_due_on`, `p_returned_on`; reads `CURRENT_DATE`.
- `trg_loan_audit_fn`: trigger row fields `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`; reads `TG_OP`.

#### Writes
- `fn_checkout_book`: updates `book.copies_free`; inserts one row per successful call into `loan` columns `book_id`, `member_id`, `due_on`; reads returned `loan_id`.
- `sp_return_book`: updates `loan.returned_on`; updates `book.copies_free`.
- `trg_loan_audit_fn`: inserts one `loan_audit` row per qualifying trigger event, setting `loan_id` and `action` to `CHECKOUT` for inserts or `RETURN` when `returned_on` changes from NULL to non-NULL.
- `trg_loan_audit`: fires after `INSERT` or `UPDATE OF returned_on` on `loan`.

#### Relationships
- `loan.loan_id -> loan_audit.loan_id` — audit records copy the loan identifier; no foreign key declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`: raises an exception for blocked members or unavailable copies; decrements free copies, creates a loan due `p_days` from today, and returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`: marks an unreturned loan returned today; raises an exception if unknown or already returned; increments the associated book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`: immutable SQL function; computes 50 cents per overdue day, using today when return date is NULL, with a zero floor and 2000-cent cap.
- `trg_loan_audit_fn() RETURNS TRIGGER`: records checkout on loan insertion and return when `returned_on` transitions from NULL to non-NULL; returns `NEW`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Trigger function: `trg_loan_audit_fn`.
