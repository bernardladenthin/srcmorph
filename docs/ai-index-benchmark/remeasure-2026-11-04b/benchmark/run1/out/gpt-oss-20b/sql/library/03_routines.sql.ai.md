### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:53:53Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Provides checkout, return, late‑fee calculation, and audit trail for a library system.

#### Purpose
- Implements core lending operations: checkout, return, late fee, and audit logging.
- Enforces business rules: blocked members, available copies, and overdue fees.

#### Type
- Table: `loan_audit` (PostgreSQL).
- Function: `fn_checkout_book` (plpgsql), `fn_late_fee` (sql), `trg_loan_audit_fn` (plpgsql trigger function).
- Procedure: `sp_return_book` (plpgsql).
- Trigger: `trg_loan_audit` (AFTER INSERT/UPDATE OF `returned_on` on `loan`).

#### Schema
- **loan_audit**
  - `audit_id BIGSERIAL PRIMARY KEY`
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (`member_id`, `is_blocked`) – in `fn_checkout_book`.
- `book` (`book_id`, `copies_free`) – in `fn_checkout_book` and `sp_return_book`.
- `loan` (`loan_id`, `returned_on`, `book_id`) – in `sp_return_book` and `trg_loan_audit_fn`.

#### Writes
- `loan_audit`: 1 row per checkout or return (triggered by `trg_loan_audit`).
- `book`: decrement `copies_free` on checkout; increment on return.
- `loan`: insert new loan in `fn_checkout_book`; update `returned_on` in `sp_return_book`.

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Reject if member blocked.
  - Decrement `book.copies_free`; error if none.
  - Insert into `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.

- **sp_return_book(p_loan_id)**
  - Update `loan.returned_on = CURRENT_DATE`; capture `book_id`.
  - If no row updated, error (unknown or already returned).
  - Increment `book.copies_free` for that `book_id`.

- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Multiply by 50 cents, cap at 2000 cents.

- **trg_loan_audit_fn()**
  - On INSERT: log `CHECKOUT`.
  - On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`.
- Trigger: `trg_loan_audit`.
