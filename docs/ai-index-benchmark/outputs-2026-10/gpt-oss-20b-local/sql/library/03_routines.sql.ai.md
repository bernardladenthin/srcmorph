### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:15:45Z
- G: 
- A: 0.0.0
- X: file
---
> Handles book lending, returns, late fee calculation, and audit trail for a library system.

#### Purpose
- Provides checkout, return, and late fee logic.
- Records audit events for loan actions.

#### Type
- Table, functions, procedure, trigger (PostgreSQL, PL/pgSQL).

#### Schema
**loan_audit**
- `audit_id BIGSERIAL PRIMARY KEY`
- `loan_id BIGINT NOT NULL`
- `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
- `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (`member_id`, `is_blocked`) – check blocked status.
- `book` (`book_id`, `copies_free`) – verify free copy.
- `loan` (`loan_id`, `book_id`, `returned_on`) – update return status.
- `loan` (`due_on`, `returned_on`) – late fee calculation.

#### Writes
- `book` – decrement `copies_free` on checkout, increment on return.
- `loan` – insert new loan, update `returned_on`.
- `loan_audit` – insert audit rows on checkout and return.

#### Relationships
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
**fn_checkout_book(p_member_id, p_book_id, p_days)**
- Abort if member blocked.
- Decrement book copy; error if none free.
- Insert loan with due date; return new `loan_id`.

**sp_return_book(p_loan_id)**
- Set `returned_on` to today for the loan; error if already returned or unknown.
- Increment book copy count.

**fn_late_fee(p_due_on, p_returned_on)**
- Compute days overdue: `max(0, returned_on_or_today - due_on)`.
- Multiply by 50 cents, cap at 2000 cents.

**trg_loan_audit_fn()**
- On loan INSERT → audit `CHECKOUT`.
- On loan UPDATE where `returned_on` changes from NULL → audit `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Functions: `fn_late_fee`, `trg_loan_audit_fn`.
- Trigger: `trg_loan_audit`.
