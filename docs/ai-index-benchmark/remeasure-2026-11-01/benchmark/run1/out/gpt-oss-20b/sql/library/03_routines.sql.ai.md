### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:26:55Z
- G: 
- A: 0.0.0
- X: file
---
> Records and manages book loan transactions, including checkout, return, late fees, and audit trail.

#### Purpose
- Handles checkout and return of library books.
- Calculates late fees.
- Maintains audit trail of loan actions.

#### Type
- Table: `loan_audit`
- Function: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Procedure: `sp_return_book`
- Trigger: `trg_loan_audit`
- Dialect: PostgreSQL, PL/pgSQL

#### Schema
**loan_audit**
- `audit_id BIGSERIAL PRIMARY KEY`
- `loan_id BIGINT NOT NULL`
- `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
- `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (member_id, is_blocked) – `fn_checkout_book`
- `book` (book_id, copies_free) – `fn_checkout_book`, `sp_return_book`
- `loan` (loan_id, returned_on, book_id) – `sp_return_book`, `trg_loan_audit_fn`

#### Writes
- `book` – `fn_checkout_book` (copies_free: -1), `sp_return_book` (copies_free: +1)
- `loan` – `fn_checkout_book` (INSERT), `sp_return_book` (UPDATE returned_on)
- `loan_audit` – `trg_loan_audit_fn` (INSERT on INSERT or UPDATE of returned_on)

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
**fn_checkout_book(p_member_id, p_book_id, p_days)**
- Check member not blocked.
- Decrement `book.copies_free`; error if none.
- Insert into `loan` with due date; return new `loan_id`.

**sp_return_book(p_loan_id)**
- Update `loan.returned_on`; error if already returned.
- Increment `book.copies_free` for returned book.

**fn_late_fee(p_due_on, p_returned_on)**
- Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
- Multiply by 50, cap at 2000.

**trg_loan_audit_fn()**
- On INSERT: log `CHECKOUT`.
- On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Trigger: `trg_loan_audit` on `loan` table.
