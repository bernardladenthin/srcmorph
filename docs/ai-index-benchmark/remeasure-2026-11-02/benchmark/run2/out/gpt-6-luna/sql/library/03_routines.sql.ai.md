### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:44:30Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Implements library lending operations: checking out and returning book copies, calculating overdue fees, and recording loan audit events.

#### Purpose
- Manages checkout and return effects on book availability and loan records.
- Calculates capped late fees and audits loan checkout and return events.

#### Type
- PostgreSQL / PL/pgSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key (`loan_audit_pkey`)
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`
- No explicit indexes.

#### Reads
- `member.member_id`, `member.is_blocked` — checkout block check.
- `book.book_id`, `book.copies_free` — checkout availability check/update; return copy restoration.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — return processing.
- `fn_late_fee`: parameters `p_due_on`, `p_returned_on`; uses `CURRENT_DATE`.

#### Writes
- `book.copies_free` — decrement on checkout; increment on return.
- `loan`: `INSERT (book_id, member_id, due_on)` on checkout; update `returned_on` on return.
- `loan_audit`: trigger inserts `(loan_id, action)` for checkout (`CHECKOUT`) or first transition to returned (`RETURN`); one row per qualifying event.

#### Relationships
- No declared foreign keys.
- Audit association: `loan_audit.loan_id` records `loan.loan_id`.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises an exception for blocked members or when no free copy can be decremented.
  - Inserts a loan with due date `CURRENT_DATE + p_days`; returns the new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `returned_on` to `CURRENT_DATE` for an unreturned loan and obtains its `book_id`.
  - Raises an exception for an unknown or already returned loan; otherwise increments the book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Computes overdue days using `p_returned_on`, or `CURRENT_DATE` if null; clamps at zero and charges 50 cents per day, capped at 2000 cents.
  - SQL function marked `IMMUTABLE`.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - On loan insert, writes a `CHECKOUT` audit row; when `returned_on` changes from null to non-null, writes a `RETURN` audit row.
  - Returns `NEW`.
- `trg_loan_audit`
  - Row-level `AFTER INSERT OR UPDATE OF returned_on` trigger on `loan`; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-in function `now()`; trigger function `trg_loan_audit_fn`.
