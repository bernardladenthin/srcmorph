### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:51:33Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Manages book loans, returns, late fees, and audit logging for a library system.

#### Purpose
- Handles checkout and return operations for library books.
- Calculates late fees.
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
- `member` (`member_id`, `is_blocked`) – check blocked status.
- `book` (`book_id`, `copies_free`) – verify and decrement free copies.
- `loan` (`loan_id`, `returned_on`) – update return status.
- `loan` (`loan_id`, `book_id`) – retrieve book id on return.
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
  - Verify member not blocked.
  - Decrement `book.copies_free`; error if none.
  - Insert into `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.

- **sp_return_book(p_loan_id)**
  - Update `loan.returned_on` to `CURRENT_DATE`; return `book_id`.
  - If no row updated, raise exception.
  - Increment `book.copies_free` for returned book.

- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Multiply by 50, cap at 2000, ensure non‑negative.

- **trg_loan_audit_fn()**
  - On INSERT: log `CHECKOUT`.
  - On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`.
- Trigger: `trg_loan_audit`.
