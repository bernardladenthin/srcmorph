### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:31:59Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Handles checkout, return, late fee calculation, and audit trail for a library loan system.

#### Purpose
- Implements checkout and return logic for books.
- Calculates late fees.
- Audits loan actions.

#### Type
- Table: `loan_audit` (PostgreSQL).
- Function: `fn_checkout_book` (plpgsql), `fn_late_fee` (sql), `trg_loan_audit_fn` (plpgsql).
- Procedure: `sp_return_book` (plpgsql).
- Trigger: `trg_loan_audit` (AFTER INSERT OR UPDATE OF returned_on ON loan).

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL PRIMARY KEY`
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (member_id, is_blocked)
- `book` (book_id, copies_free)
- `loan` (loan_id, returned_on, book_id)
- `loan` (loan_id, returned_on, book_id) for audit trigger

#### Writes
- `book` (copies_free) – decrement on checkout, increment on return
- `loan` – INSERT new loan record
- `loan_audit` – INSERT audit record on checkout and return
- `loan` – UPDATE returned_on on return

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Reject if member blocked.
  - Decrement `book.copies_free`; error if none.
  - Insert into `loan` with due date `CURRENT_DATE + p_days`; return new `loan_id`.
- **sp_return_book(p_loan_id)**
  - Update `loan.returned_on` to `CURRENT_DATE`; capture `book_id`.
  - If no row updated, error.
  - Increment `book.copies_free`.
- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Fee = `LEAST(GREATEST(days_overdue, 0) * 50, 2000)`.
- **trg_loan_audit_fn()**
  - On INSERT: log `CHECKOUT`.
  - On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`.
- Trigger: `trg_loan_audit`.
