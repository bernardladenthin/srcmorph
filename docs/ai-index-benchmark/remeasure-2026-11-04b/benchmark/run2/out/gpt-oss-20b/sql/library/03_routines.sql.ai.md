### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:02:25Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Manages book lending, returns, late fees, and audit trail for a library system.

#### Purpose
- Handles checkout and return operations for library books.
- Calculates late fees.
- Records audit events for loan actions.

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
- `book` (`book_id`, `copies_free`) – verify and decrement free copies.
- `loan` (`loan_id`, `returned_on`, `book_id`) – update return status.
- `loan` (`loan_id`, `returned_on`) – trigger audit on update.

#### Writes
- `book` – decrement `copies_free` on checkout; increment on return.
- `loan` – insert new loan record; update `returned_on` on return.
- `loan_audit` – insert audit rows for checkout and return actions.

#### Relationships
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Verify member not blocked.
  - Decrement `copies_free` if available; error if none.
  - Insert loan with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.

- **sp_return_book(p_loan_id)**
  - Update loan `returned_on = CURRENT_DATE`; capture `book_id`.
  - If no row updated, raise exception.
  - Increment `copies_free` for the returned book.

- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Multiply by 50, cap at 2000.

- **trg_loan_audit_fn()**
  - On INSERT: log `CHECKOUT`.
  - On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Trigger: `trg_loan_audit` on `loan` table.
