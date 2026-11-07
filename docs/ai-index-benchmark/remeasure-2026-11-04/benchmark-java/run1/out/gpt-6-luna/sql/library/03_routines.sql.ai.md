### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:11:58Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Implements library checkout and return workflows, tracks loan audit events, and calculates capped overdue fees.

#### Purpose
- Prevents blocked-member checkout and decrements available book copies when creating a loan.
- Returns loans and copies, records checkout/return audit events, and computes late fees.

#### Type
- PostgreSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key.
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')`.
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.

#### Reads
- `member.member_id`, `member.is_blocked` — blocked-member check in `fn_checkout_book`.
- `book.book_id`, `book.copies_free` — checkout availability condition.
- `loan.loan_id`, `loan.returned_on` — return condition in `sp_return_book`.
- `loan` trigger row: `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`.

#### Writes
- `book.copies_free` — decrement on checkout; increment on return.
- `loan`: `book_id`, `member_id`, `due_on` — `fn_checkout_book` inserts 1 row per successful call; `loan_id` returned.
- `loan.returned_on` — `sp_return_book` sets to `CURRENT_DATE` for an unreturned loan.
- `loan_audit`: `loan_id`, `action` — trigger function inserts 1 `CHECKOUT` row per loan insert, or 1 `RETURN` row when `returned_on` changes from NULL to non-NULL.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises an exception for a blocked member or when no free copy is available.
  - Decrements `book.copies_free`, inserts a loan due `p_days` from `CURRENT_DATE`, and returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `loan.returned_on` to `CURRENT_DATE` for an active loan and captures its `book_id`.
  - Raises an exception for an unknown or already returned loan; otherwise increments the book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Immutable SQL function: charges 50 cents per overdue day, using `CURRENT_DATE` when the return date is NULL; floors at zero and caps at 2000 cents.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts a `CHECKOUT` audit row on loan insert; inserts a `RETURN` audit row when `returned_on` changes from NULL to non-NULL; returns `NEW`.
- `trg_loan_audit`
  - After each `loan` insert or update of `returned_on`, executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Trigger function: `trg_loan_audit_fn`.
