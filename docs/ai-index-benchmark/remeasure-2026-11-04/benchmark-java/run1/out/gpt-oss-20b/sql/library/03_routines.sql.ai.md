### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:16:53Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Handles book lending, returns, late fee calculation, and audit trail for a library system.

#### Purpose
- Records loan events and enforces business rules for checkout and return.
- Calculates late fees.
- Maintains audit trail of loan actions.

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
- `member` (SELECT for block status).
- `book` (SELECT for free copies).
- `loan` (SELECT for returned_on status in `sp_return_book`).
- `loan` (SELECT for audit trigger).

#### Writes
- `book` (UPDATE copies_free decrement on checkout; increment on return).
- `loan` (INSERT new loan in `fn_checkout_book`; UPDATE returned_on in `sp_return_book`).
- `loan_audit` (INSERT on checkout and return via trigger).

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Reject if member blocked.
  - Decrement `book.copies_free`; error if none.
  - INSERT into `loan` with due date; RETURN new `loan_id`.

- **sp_return_book(p_loan_id)**
  - UPDATE `loan` set `returned_on` to today; capture `book_id`.
  - If no row updated, error.
  - Increment `book.copies_free`.

- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Multiply by 50, cap at 2000.

- **trg_loan_audit_fn()**
  - On INSERT: log `CHECKOUT`.
  - On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`.
- Trigger: `trg_loan_audit`.
