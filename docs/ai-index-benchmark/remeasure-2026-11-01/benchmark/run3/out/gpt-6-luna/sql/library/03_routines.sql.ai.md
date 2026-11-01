### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:00:56Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library-book checkout and return, maintains available-copy counts, calculates capped overdue fees, and records loan audit events.

#### Purpose
- Checks out available books to unblocked members, processes returns, and calculates late fees.
- Audits loan checkout and return events.

#### Type
- PostgreSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key.
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')`.
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.

#### Reads
- `member`: `member_id`, `is_blocked` (`fn_checkout_book`).
- `book`: `book_id`, `copies_free` (`fn_checkout_book`, `sp_return_book`).
- `loan`: `loan_id`, `returned_on`, `book_id` (`sp_return_book`).
- Trigger event row `loan`: `loan_id`, `returned_on` (`trg_loan_audit_fn`).
- `fn_late_fee` inputs: `p_due_on`, `p_returned_on`; uses `CURRENT_DATE`.

#### Writes
- `book`: updates `copies_free` (`fn_checkout_book`: decrement; `sp_return_book`: increment).
- `loan`: `fn_checkout_book` inserts `book_id`, `member_id`, `due_on` — 1 row; `sp_return_book` updates `returned_on`.
- `loan_audit`: `trg_loan_audit_fn` has two conditional INSERT statements, each inserting 1 row: checkout event or return event.

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` — audit association used by trigger; no foreign key declared.
- `loan.book_id -> book.book_id` — book association used by checkout and return routines; no foreign key declared.
- `loan.member_id -> member.member_id` — member association used by checkout; no foreign key declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) -> BIGINT`
  - Rejects blocked members; decrements `book.copies_free` only when a free copy exists, otherwise raises an exception.
  - Inserts a loan with due date `CURRENT_DATE + p_days`; returns the new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `loan.returned_on` to the current date if not already returned; raises an exception for unknown or previously returned loans.
  - Increments the corresponding book’s `copies_free`.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) -> INTEGER`
  - Returns overdue days multiplied by 50 cents, floored at zero and capped at 2000 cents; uses `CURRENT_DATE` when `p_returned_on` is null.
- `trg_loan_audit_fn() -> TRIGGER`
  - On loan insertion, inserts a `CHECKOUT` event into `loan_audit`; when `returned_on` changes from null to non-null, inserts a `RETURN` event.
  - Returns `NEW`.
- `trg_loan_audit`
  - Row-level `AFTER INSERT OR UPDATE OF returned_on` trigger on `loan`; executes `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-in: `now()`, `CURRENT_DATE`, `COALESCE`, `GREATEST`, `LEAST`.
