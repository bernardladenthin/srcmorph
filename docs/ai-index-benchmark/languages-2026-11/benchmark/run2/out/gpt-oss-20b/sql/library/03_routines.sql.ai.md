### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:41:57Z
- G: 
- A: 0.0.0
- X: file
---
> A PostgreSQL library system that records book loans, returns, late fees, and audit trails.

#### Purpose
- Implements checkout, return, and late fee logic for a lending library.
- Tracks audit events for loan creation and return.

#### Type
- Table: `loan_audit`
- Function: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Procedure: `sp_return_book`
- Trigger: `trg_loan_audit`
- Dialect: PostgreSQL, PL/pgSQL

#### Schema
- **loan_audit**
  - `audit_id BIGSERIAL PRIMARY KEY`
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (`member_id`, `is_blocked`) – check blocked status.
- `book` (`book_id`, `copies_free`) – verify free copy.
- `loan` (`loan_id`, `returned_on`, `book_id`) – update return status.
- `loan` (`loan_id`, `returned_on`) – trigger audit on update.

#### Writes
- `book` – decrement `copies_free` on checkout; increment on return.
- `loan` – insert new loan record; update `returned_on` on return.
- `loan_audit` – insert audit rows for checkout and return events.

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Verify member not blocked.
  - Decrement `book.copies_free`; error if none.
  - Insert into `loan` with due date `CURRENT_DATE + p_days`; return new `loan_id`.

- **sp_return_book(p_loan_id)**
  - Update `loan.returned_on` to `CURRENT_DATE` where not already returned; capture `book_id`.
  - Increment `book.copies_free` for returned book.
  - Raise exception if loan unknown or already returned.

- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute overdue days (`COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`).
  - Multiply by 50 cents, cap at 2000 cents.

- **trg_loan_audit_fn()**
  - On `INSERT`: log `CHECKOUT`.
  - On `UPDATE` where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_late_fee`, `trg_loan_audit_fn`
- Trigger: `trg_loan_audit` on `loan` table.
