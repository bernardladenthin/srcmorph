### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:02:23Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> A set of PostgreSQL routines that manage book loans, enforce copy availability, record audit events, and calculate late fees.

#### Purpose
- Enforce loan rules and track audit trail for book lending.
- Provide utility functions for checkout, return, and late fee calculation.

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
- `member` (`member_id`, `is_blocked`) – in `fn_checkout_book`
- `book` (`book_id`, `copies_free`) – in `fn_checkout_book` and `sp_return_book`
- `loan` (`loan_id`, `returned_on`, `book_id`) – in `sp_return_book` and `trg_loan_audit_fn`

#### Writes
- `book` – decrement `copies_free` in `fn_checkout_book`; increment `copies_free` in `sp_return_book`
- `loan` – insert new row in `fn_checkout_book`; update `returned_on` in `sp_return_book`
- `loan_audit` – insert one row per checkout or return in `trg_loan_audit_fn`

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Reject if member blocked.
  - Decrease `copies_free` if available; error if none.
  - Insert loan with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.
- **sp_return_book(p_loan_id)**
  - Set `returned_on = CURRENT_DATE` for the loan if not already returned; error if not found.
  - Increment `copies_free` for the returned book.
- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `max(0, COALESCE(p_returned_on, CURRENT_DATE) - p_due_on)`.
  - Multiply by 50 cents; cap at 2000 cents.
- **trg_loan_audit_fn()**
  - On loan INSERT: log `CHECKOUT`.
  - On loan UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Trigger: `trg_loan_audit` on `loan` table.
