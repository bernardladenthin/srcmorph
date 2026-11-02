### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:47:14Z
- G: 
- A: 0.0.0
- X: file
---
> Records and manages book loan transactions, including checkout, return, late fees, and audit trail.

#### Purpose
- Implements checkout and return logic for library books.
- Calculates late fees.
- Audits loan actions.

#### Type
- Table: `loan_audit` (PostgreSQL).
- Function: `fn_checkout_book` (plpgsql), `fn_late_fee` (sql), `trg_loan_audit_fn` (plpgsql).
- Procedure: `sp_return_book` (plpgsql).
- Trigger: `trg_loan_audit` (AFTER INSERT OR UPDATE OF returned_on ON loan).

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
- `loan` (for `fn_late_fee`: `p_due_on`, `p_returned_on` are parameters, not table reads)

#### Writes
- `book` (`copies_free` decrement on checkout, increment on return)
- `loan` (INSERT new loan, UPDATE `returned_on` on return)
- `loan_audit` (INSERT on checkout and return via trigger)

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Verify member not blocked.
  - Decrement `book.copies_free`; error if none.
  - Insert into `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.

- **sp_return_book(p_loan_id)**
  - Update `loan.returned_on = CURRENT_DATE` where `returned_on IS NULL`; capture `book_id`.
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
- Trigger function `trg_loan_audit_fn` used by trigger `trg_loan_audit`.
