### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:50:30Z
- G: 
- A: 0.0.0
- X: file
---
> Handles book lending, returns, late fee calculation, and audit trail for a library system.

#### Purpose
- Record loan events and enforce business rules for checkout and return.
- Compute late fees.
- Maintain audit log of loan actions.

#### Type
- Table: `loan_audit` (PostgreSQL)
- Function: `fn_checkout_book` (plpgsql), `fn_late_fee` (sql), `trg_loan_audit_fn` (plpgsql)
- Procedure: `sp_return_book` (plpgsql)
- Trigger: `trg_loan_audit` (PostgreSQL)

#### Schema
**loan_audit**
- `audit_id BIGSERIAL PRIMARY KEY`
- `loan_id BIGINT NOT NULL`
- `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
- `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (`member_id`, `is_blocked`) – checkout validation.
- `book` (`book_id`, `copies_free`) – availability check.
- `loan` (`loan_id`, `returned_on`, `book_id`) – return processing.
- `loan` (`due_on`, `returned_on`) – late fee calculation.

#### Writes
- `book` (`copies_free`) – decrement on checkout, increment on return.
- `loan` – insert new loan, update `returned_on`.
- `loan_audit` – insert audit rows on checkout and return.

#### Relationships
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
- `loan_audit.loan_id -> loan.loan_id`

#### Routine logic
**fn_checkout_book(p_member_id, p_book_id, p_days)**
- Abort if member blocked.
- Decrement `copies_free`; error if none.
- Insert into `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.

**sp_return_book(p_loan_id)**
- Update `loan.returned_on` to `CURRENT_DATE`; capture `book_id`.
- If no row updated, error.
- Increment `book.copies_free`.

**fn_late_fee(p_due_on, p_returned_on)**
- Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
- Multiply by 50, cap at 2000.

**trg_loan_audit_fn()**
- On INSERT: log `CHECKOUT`.
- On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Trigger function: `trg_loan_audit_fn`.
- Trigger: `trg_loan_audit` on `loan`.
