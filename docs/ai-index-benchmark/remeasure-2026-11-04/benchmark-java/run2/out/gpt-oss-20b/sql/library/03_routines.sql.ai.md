### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:33:42Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> A PostgreSQL library system that manages book loans, returns, late fees, and audit trails.

#### Purpose
- Handles checkout and return of books.
- Calculates late fees.
- Records audit events for loan actions.

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
- `member` (`member_id`, `is_blocked`)
- `book` (`book_id`, `copies_free`)
- `loan` (`loan_id`, `returned_on`, `book_id`)

#### Writes
- `book` (`copies_free` decremented by 1 on checkout; incremented by 1 on return)
- `loan` (INSERT new loan; UPDATE `returned_on` on return)
- `loan_audit` (INSERT audit rows on checkout and return)

#### Relationships
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
- `loan_audit.loan_id -> loan.loan_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Verify member not blocked.
  - Decrement `copies_free` if available.
  - Insert new loan with `due_on = CURRENT_DATE + p_days`.
  - Return new `loan_id`.

- **sp_return_book(p_loan_id)**
  - Update loan `returned_on = CURRENT_DATE` where not already returned.
  - Retrieve `book_id`; if none, raise exception.
  - Increment `copies_free` for that book.

- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Multiply by 50 cents, cap at 2000 cents.

- **trg_loan_audit_fn()**
  - On INSERT: log `CHECKOUT`.
  - On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Trigger: `trg_loan_audit` on `loan` table.
