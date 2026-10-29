### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:54:22Z
- G: 
- A: 0.0.0
- X: file
---
> Handles book lending, returns, late fee calculation, and audit trail for a library system.

#### Purpose
- Implements checkout, return, and late fee logic.
- Records audit events for loan actions.

#### Type
- Table: `loan_audit` (PostgreSQL).
- Function: `fn_checkout_book` (plpgsql), `fn_late_fee` (sql), `trg_loan_audit_fn` (plpgsql).
- Procedure: `sp_return_book` (plpgsql).
- Trigger: `trg_loan_audit` (PostgreSQL).

#### Schema
**loan_audit**
- `audit_id BIGSERIAL PRIMARY KEY`
- `loan_id BIGINT NOT NULL`
- `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
- `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (`member_id`, `is_blocked`) – `fn_checkout_book`.
- `book` (`book_id`, `copies_free`) – `fn_checkout_book`.
- `loan` (`loan_id`, `book_id`, `returned_on`) – `sp_return_book`, `trg_loan_audit_fn`.
- `loan` (`due_on`, `returned_on`) – `fn_late_fee`.

#### Writes
- `book` (`copies_free`) – decrement in `fn_checkout_book`, increment in `sp_return_book`.
- `loan` – insert in `fn_checkout_book`, update `returned_on` in `sp_return_book`.
- `loan_audit` – insert in `trg_loan_audit_fn` for checkout and return events.

#### Relationships
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
- `loan_audit.loan_id -> loan.loan_id`

#### Routine logic
**fn_checkout_book(p_member_id, p_book_id, p_days)**
- Check member not blocked.
- Decrement `copies_free` if available; error if none.
- Insert new loan with `due_on = CURRENT_DATE + p_days`.
- Return new `loan_id`.

**sp_return_book(p_loan_id)**
- Update loan `returned_on = CURRENT_DATE` where not already returned.
- Capture `book_id`; error if loan unknown or already returned.
- Increment `copies_free` for that book.

**fn_late_fee(p_due_on, p_returned_on)**
- Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
- Multiply by 50 cents, cap at 2000 cents.

**trg_loan_audit_fn()**
- On INSERT: log `CHECKOUT`.
- On UPDATE where `returned_on` changes from NULL to NOT NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Trigger: `trg_loan_audit` uses `trg_loan_audit_fn`.
