### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:48:29Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Handles book lending, returning, late fee calculation, and audit trail for a library system.

#### Purpose
- Provides checkout and return logic for books.
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
- `member` (`member_id`, `is_blocked`) – `fn_checkout_book`
- `book` (`book_id`, `copies_free`) – `fn_checkout_book`
- `loan` (`loan_id`, `returned_on`, `book_id`) – `sp_return_book`, `trg_loan_audit_fn`

#### Writes
- `book` – `fn_checkout_book`: `copies_free = copies_free - 1` (1 row)
- `loan` – `fn_checkout_book`: `INSERT` 1 row
- `loan` – `sp_return_book`: `UPDATE` 1 row
- `book` – `sp_return_book`: `UPDATE` 1 row
- `loan_audit` – `trg_loan_audit_fn`: `INSERT` 1 row per INSERT or UPDATE of `returned_on`

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Abort if member blocked.
  - Decrement `copies_free` if available; else error.
  - Insert new loan with due date `CURRENT_DATE + p_days`.
  - Return new `loan_id`.

- **sp_return_book(p_loan_id)**
  - Update loan `returned_on` to `CURRENT_DATE` if not already returned; capture `book_id`.
  - If no such loan, error.
  - Increment `copies_free` for the returned book.

- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Multiply by 50, cap at 2000, ensure non‑negative.

- **trg_loan_audit_fn()**
  - On INSERT: log `CHECKOUT`.
  - On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Trigger: `trg_loan_audit` on `loan` table.
