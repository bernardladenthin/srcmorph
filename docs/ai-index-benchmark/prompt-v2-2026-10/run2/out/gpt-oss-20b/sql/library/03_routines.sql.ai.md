### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:53:02Z
- G: 
- A: 0.0.0
- X: file
---
> Records checkout and return events for library loans, calculates late fees, and enforces business rules for members and book inventory.

#### Purpose
- Enforce checkout/return logic and audit trail for library loans.
- Compute late fees for overdue books.

#### Type
- Table: `loan_audit` (PostgreSQL).
- Function: `fn_checkout_book` (plpgsql), `fn_late_fee` (sql), `trg_loan_audit_fn` (plpgsql).
- Procedure: `sp_return_book` (plpgsql).
- Trigger: `trg_loan_audit` (PostgreSQL).

#### Schema
**loan_audit**
- `audit_id BIGSERIAL PRIMARY KEY`
- `loan_id BIGINT NOT NULL`
- `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
- `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (`member_id`, `is_blocked`) – check blocked status.
- `book` (`book_id`, `copies_free`) – verify free copy.
- `loan` (`loan_id`, `returned_on`) – update return status.
- `loan` (`due_on`, `returned_on`) – compute late fee (via `fn_late_fee`).

#### Writes
- `book` – decrement `copies_free` on checkout; increment on return.
- `loan` – insert new loan record; update `returned_on` on return.
- `loan_audit` – insert audit rows on checkout and return events.

#### Relationships
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
- `loan_audit.loan_id -> loan.loan_id`

#### Routine logic
**fn_checkout_book(p_member_id, p_book_id, p_days)**
- Raise exception if member blocked.
- Decrement `book.copies_free`; error if none.
- Insert into `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.

**sp_return_book(p_loan_id)**
- Update `loan.returned_on = CURRENT_DATE`; capture `book_id`.
- Raise exception if loan unknown or already returned.
- Increment `book.copies_free`.

**fn_late_fee(p_due_on, p_returned_on)**
- Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
- Multiply by 50 cents, cap at 2000 cents.

**trg_loan_audit_fn()**
- On `INSERT`: log `CHECKOUT`.
- On `UPDATE` where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Functions: `fn_late_fee`.
- Trigger: `trg_loan_audit`.
