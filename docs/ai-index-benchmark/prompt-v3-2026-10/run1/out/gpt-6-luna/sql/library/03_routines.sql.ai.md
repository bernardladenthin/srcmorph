### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:49:48Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library book checkout and return, overdue-fee calculation, and loan activity auditing.

#### Purpose
- Checks out available book copies to unblocked members, records due dates, and processes returns.
- Calculates capped late fees and records checkout/return events in an audit table.

#### Type
- PostgreSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.
- Dialect: PostgreSQL, PL/pgSQL and SQL.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key, NOT NULL; generated constraint/index name `loan_audit_pkey`.
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')`; generated constraint name `loan_audit_action_check`.
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.
- Index: `loan_audit_pkey` — table `loan_audit`, column `audit_id`, primary-key B-tree index (implicit).

#### Reads
- `member.member_id`, `member.is_blocked` — checkout eligibility check.
- `book.book_id`, `book.copies_free` — checkout availability/update predicate.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — return lookup and update.
- `p_due_on`, `p_returned_on` — late-fee calculation.
- Trigger `trg_loan_audit_fn`: `TG_OP`, `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`.

#### Writes
- `book.copies_free` — decrement on successful checkout; increment on successful return.
- `loan.book_id`, `loan.member_id`, `loan.due_on` — checkout insert; `loan.returned_on` — return update.
- `loan_audit.loan_id`, `loan_audit.action` — trigger inserts 1 row per checkout insert or qualifying first return update; otherwise 0 rows.

#### Relationships
- `loan.book_id -> book.book_id` — checkout and return copy updates.
- `loan.member_id -> member.member_id` — checkout member eligibility.
- `loan_audit.loan_id -> loan.loan_id` — audit event association; no FK constraint declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) -> BIGINT`
  - Raises an exception if the member is blocked or no free copy can be decremented.
  - Inserts a loan with `due_on = CURRENT_DATE + p_days`; returns its `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `returned_on = CURRENT_DATE` only for an unreturned loan and retrieves its `book_id`.
  - Raises an exception for an unknown or already-returned loan; otherwise increments the book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) -> INTEGER`
  - Computes overdue days using return date or `CURRENT_DATE`, floors at zero, charges 50 cents per day, caps at 2000; immutable SQL function.
- `trg_loan_audit_fn() -> TRIGGER`
  - Inserts `CHECKOUT` audit event on loan insert; inserts `RETURN` event when `returned_on` changes from NULL to non-NULL; returns `NEW`.
- Trigger `trg_loan_audit`
  - Fires after `INSERT` or `UPDATE OF returned_on` on `loan`, once per row; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Trigger function: `trg_loan_audit_fn`.
