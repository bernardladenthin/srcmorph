### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:21:16Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library checkout and return operations, overdue-fee calculation, and loan event auditing.

#### Purpose
- Track checkout and return events in `loan_audit`.
- Provide routines to lend and return books, calculate late fees, and audit loan changes.

#### Type
- PostgreSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.

#### Schema
- `loan_audit.audit_id BIGSERIAL` — primary key.
- `loan_audit.loan_id BIGINT NOT NULL`.
- `loan_audit.action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`.
- `loan_audit.changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.

#### Reads
- `member.member_id`, `member.is_blocked` — `fn_checkout_book` checks whether the member is blocked.
- `book.book_id`, `book.copies_free` — `fn_checkout_book` checks and decrements availability; `sp_return_book` increments availability.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — `sp_return_book` finds an unreturned loan and its book.
- `p_due_on`, `p_returned_on` — `fn_late_fee` computes overdue days; uses `CURRENT_DATE` when `p_returned_on` is null.
- `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`, `TG_OP` — trigger function determines audit action.

#### Writes
- `book.copies_free` — decremented by `fn_checkout_book`; incremented by `sp_return_book`.
- `loan.book_id`, `loan.member_id`, `loan.due_on` — inserted by `fn_checkout_book`.
- `loan.returned_on` — set by `sp_return_book`.
- `loan_audit.loan_id`, `loan_audit.action` — inserted by `trg_loan_audit_fn` for checkout and return events.

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` — audit event references a loan by column usage; no foreign key constraint is declared.
- `loan.book_id -> book.book_id` — used by return logic; no foreign key constraint is declared.
- `loan.member_id -> member.member_id` — used by checkout logic; no foreign key constraint is declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises an exception if the member is blocked or no free copy is available.
  - Decrements `book.copies_free`, inserts a `loan` with due date `CURRENT_DATE + p_days`, and returns the new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Marks an unreturned loan returned on `CURRENT_DATE`; raises an exception for an unknown or already returned loan.
  - Restores one free copy to the associated book.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Calculates 50 cents per overdue day, with a zero minimum and 2000-cent cap.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts a `CHECKOUT` audit row on loan insert.
  - Inserts a `RETURN` audit row when `returned_on` changes from null to non-null; returns `NEW`.
- `trg_loan_audit`
  - Fires after `INSERT` or `UPDATE OF returned_on` on `loan`, once per row; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-in values/functions: `CURRENT_DATE`, `now()`, `COALESCE`, `GREATEST`, `LEAST`.
- Trigger: `trg_loan_audit` executes `trg_loan_audit_fn`.
