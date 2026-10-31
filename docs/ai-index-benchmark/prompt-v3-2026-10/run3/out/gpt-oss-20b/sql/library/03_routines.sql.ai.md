### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:43:27Z
- G: 
- A: 0.0.0
- X: file
---
> Handles book lending, returns, late fees, and audit logging for a library system.

#### Purpose
- Provides checkout, return, and late fee logic.
- Records audit trail of loan actions.

#### Type
- Table: `loan_audit` (PostgreSQL).
- Function: `fn_checkout_book` (plpgsql), `fn_late_fee` (sql), `trg_loan_audit_fn` (plpgsql trigger function).
- Procedure: `sp_return_book` (plpgsql).
- Trigger: `trg_loan_audit` (AFTER INSERT OR UPDATE OF returned_on ON loan).

#### Schema
- **loan_audit**
  - `audit_id BIGSERIAL PRIMARY KEY`
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (`member_id`, `is_blocked`) – checkout validation.
- `book` (`book_id`, `copies_free`) – availability check.
- `loan` (`loan_id`, `returned_on`) – return status check.
- `loan` (`due_on`, `returned_on`) – late fee calculation.

#### Writes
- `book` – decrement `copies_free` on checkout; increment on return.
- `loan` – insert new loan record; update `returned_on` on return.
- `loan_audit` – insert audit rows for checkout and return actions.

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Reject if member blocked.
  - Decrement `copies_free`; error if none.
  - Insert into `loan`; return new `loan_id`.
- **sp_return_book(p_loan_id)**
  - Update `loan.returned_on`; error if already returned or unknown.
  - Increment `book.copies_free`.
- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue; multiply by 50; cap at 2000.
- **trg_loan_audit_fn()**
  - On INSERT: log `CHECKOUT`.
  - On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`.
- Trigger: `trg_loan_audit`.
