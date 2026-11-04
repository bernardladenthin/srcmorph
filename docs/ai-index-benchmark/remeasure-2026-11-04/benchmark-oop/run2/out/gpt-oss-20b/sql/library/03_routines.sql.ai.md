### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:40:56Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Handles book lending, returns, late fee calculation, and audit logging for a library system.

#### Purpose
- Implements checkout, return, and late fee logic.
- Records audit trail of loan actions.

#### Type
- Table: `loan_audit`
- Function: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Procedure: `sp_return_book`
- Trigger: `trg_loan_audit`

#### Schema
- **loan_audit**
  - `audit_id BIGSERIAL PRIMARY KEY`
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (`member_id`, `is_blocked`) – check blocked status.
- `book` (`book_id`, `copies_free`) – verify and decrement free copies.
- `loan` (`loan_id`, `returned_on`) – update return status.
- `loan` (`due_on`, `returned_on`) – late fee calculation.

#### Writes
- `book` – decrement `copies_free` on checkout; increment on return.
- `loan` – insert new loan record; update `returned_on` on return.
- `loan_audit` – insert audit rows on checkout and return.

#### Relationships
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
- `loan_audit.loan_id -> loan.loan_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Abort if member blocked.
  - Decrement `book.copies_free`; error if none.
  - Insert into `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.

- **sp_return_book(p_loan_id)**
  - Update `loan.returned_on = CURRENT_DATE` where `returned_on IS NULL`; capture `book_id`.
  - Error if loan unknown or already returned.
  - Increment `book.copies_free` for returned book.

- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Multiply by 50, cap at 2000, floor at 0.

- **trg_loan_audit_fn()**
  - On `INSERT`: log `CHECKOUT`.
  - On `UPDATE` where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_late_fee` (used by application logic)
- Trigger: `trg_loan_audit` on `loan` table.
