### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:06:32Z
- G: 
- A: 0.0.0
- X: file
---
> Records and manages book loans, returns, late fees, and audit trails for a lending library.

#### Purpose
- Implements checkout, return, late fee calculation, and audit logging for library loans.

#### Type
- Table: `loan_audit` (PostgreSQL)
- Function: `fn_checkout_book` (plpgsql), `fn_late_fee` (sql), `trg_loan_audit_fn` (plpgsql)
- Procedure: `sp_return_book` (plpgsql)
- Trigger: `trg_loan_audit` (AFTER INSERT OR UPDATE OF returned_on ON loan)

#### Schema
**loan_audit**
- `audit_id BIGSERIAL PRIMARY KEY`
- `loan_id BIGINT NOT NULL`
- `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
- `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (`member_id`, `is_blocked`) – check blocked status.
- `book` (`book_id`, `copies_free`) – verify free copy.
- `loan` (`loan_id`, `returned_on`) – update return status.
- `loan` (`due_on`, `returned_on`) – late fee calculation (via `fn_late_fee`).

#### Writes
- `book` (`copies_free`) – decrement on checkout, increment on return.
- `loan` – insert new loan record; update `returned_on` on return.
- `loan_audit` – insert audit rows for checkout and return actions.

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
**fn_checkout_book(p_member_id, p_book_id, p_days)**
- Raise exception if member blocked.
- Decrement `book.copies_free`; error if none.
- Insert into `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.

**sp_return_book(p_loan_id)**
- Update `loan.returned_on` to `CURRENT_DATE`; return `book_id`.
- Raise exception if loan unknown or already returned.
- Increment `book.copies_free`.

**fn_late_fee(p_due_on, p_returned_on)**
- Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
- Multiply by 50, cap at 2000.

**trg_loan_audit_fn()**
- On INSERT: log `CHECKOUT`.
- On UPDATE where `returned_on` changes from NULL to NOT NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_late_fee`
- Trigger: `trg_loan_audit` on `loan` table.
