### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:19:50Z
- G: 
- A: 0.0.0
- X: file
---
> Records checkout and return actions for library loans, calculates late fees, and maintains an audit trail.

#### Purpose
- Tracks loan events and late fee calculations for a lending library.
- Provides checkout and return operations as reusable routines.

#### Type
- Table: `loan_audit` (PostgreSQL, PL/pgSQL).
- Function: `fn_checkout_book` (plpgsql), `fn_late_fee` (sql, immutable), `trg_loan_audit_fn` (plpgsql trigger function).
- Procedure: `sp_return_book` (plpgsql).
- Trigger: `trg_loan_audit` (AFTER INSERT OR UPDATE OF returned_on ON loan).

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL PRIMARY KEY`
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `fn_checkout_book`: reads `member` (`member_id`, `is_blocked`), `book` (`book_id`, `copies_free`).
- `sp_return_book`: reads `loan` (`loan_id`, `returned_on`, `book_id`), `book` (`book_id`).
- `fn_late_fee`: reads `p_due_on`, `p_returned_on` (parameters).
- `trg_loan_audit_fn`: reads `TG_OP`, `NEW`, `OLD` rows from `loan`.

#### Writes
- `fn_checkout_book`: updates `book.copies_free` (decrement), inserts into `loan` (1 row).
- `sp_return_book`: updates `loan.returned_on` (1 row), updates `book.copies_free` (increment).
- `trg_loan_audit_fn`: inserts into `loan_audit` (1 row per INSERT or UPDATE that sets `returned_on`).

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Abort if member blocked.
  - Decrement `book.copies_free`; error if none.
  - Insert new `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.
- **sp_return_book(p_loan_id)**
  - Set `loan.returned_on = CURRENT_DATE` where not already returned; capture `book_id`.
  - If no such loan, raise exception.
  - Increment `book.copies_free` for that book.
- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Multiply by 50, cap at 2000, floor at 0.
- **trg_loan_audit_fn()**
  - On INSERT: log `CHECKOUT`.
  - On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.
  - Return NEW row.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Functions: `now()`, `CURRENT_DATE`.
- Trigger: `trg_loan_audit` on `loan`.
