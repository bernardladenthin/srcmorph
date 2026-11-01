### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:15:24Z
- G: 
- A: 0.0.0
- X: file
---
> Models library book checkouts and returns, tracks loan audit events, and calculates capped overdue fees.

#### Purpose
- Check out available book copies to unblocked members and return copies to inventory.
- Record checkout/return audit events and calculate late fees.

#### Type
- PostgreSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key (inline, unnamed).
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')` (inline, unnamed).
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.
- No explicitly declared indexes.

#### Reads
- `member.member_id`, `member.is_blocked` — `fn_checkout_book`.
- `book.book_id`, `book.copies_free` — checkout availability update and return inventory update.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — `sp_return_book`.
- `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on` — `trg_loan_audit_fn`.
- `p_due_on`, `p_returned_on`, `CURRENT_DATE` — `fn_late_fee`.

#### Writes
- `book.copies_free` — `fn_checkout_book` decrements by 1; `sp_return_book` increments by 1.
- `loan(book_id, member_id, due_on)` — `fn_checkout_book` inserts 1 row per successful call.
- `loan.returned_on` — `sp_return_book` sets to `CURRENT_DATE` for an unreturned loan.
- `loan_audit(loan_id, action)` — `trg_loan_audit_fn` inserts 1 `CHECKOUT` row per loan insert, or 1 `RETURN` row when `returned_on` changes from NULL to non-NULL.

#### Relationships
- `loan.loan_id -> loan_audit.loan_id` — audit event association; no foreign key declared.
- `loan.book_id -> book.book_id` — book association used by return procedure; no foreign key declared.
- `loan.member_id -> member.member_id` — member association used by checkout; no foreign key declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Rejects blocked members; decrements an available copy or raises an exception.
  - Inserts a loan due `CURRENT_DATE + p_days`; returns the new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Marks an unreturned loan returned today and retrieves its book; raises an exception if unknown or already returned.
  - Increments the book’s `copies_free`.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Immutable SQL function; computes overdue days using return date or today, floors at zero, charges 50 cents per day, caps at 2000.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - On loan insert, writes a `CHECKOUT` audit row; on transition from unreturned to returned, writes a `RETURN` audit row; returns `NEW`.
- `trg_loan_audit`
  - Row-level AFTER trigger on `loan` inserts and updates of `returned_on`; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-in: `now()`, `CURRENT_DATE`, `COALESCE`, `GREATEST`, `LEAST`.
