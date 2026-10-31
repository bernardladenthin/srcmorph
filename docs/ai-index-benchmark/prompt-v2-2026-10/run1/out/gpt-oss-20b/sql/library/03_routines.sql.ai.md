### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:39:43Z
- G: 
- A: 0.0.0
- X: file
---
> Provides checkout, return, late fee calculation, and audit trail for a lending library.

#### Purpose
- Implements core library operations: checkout, return, late fee, and audit logging.

#### Type
- Table: `loan_audit` (PostgreSQL)
- Function: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn` (PL/pgSQL or SQL)
- Procedure: `sp_return_book` (PL/pgSQL)
- Trigger: `trg_loan_audit` (AFTER INSERT/UPDATE ON `loan`)

#### Schema
**loan_audit**
- `audit_id BIGSERIAL PRIMARY KEY`
- `loan_id BIGINT NOT NULL`
- `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
- `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (`member_id`, `is_blocked`) – check blocked status in `fn_checkout_book`.
- `book` (`book_id`, `copies_free`) – verify free copy in `fn_checkout_book`.
- `loan` (`loan_id`, `returned_on`) – update and return logic in `sp_return_book`.
- `loan` (`loan_id`, `returned_on`) – trigger `trg_loan_audit_fn` reads `NEW`/`OLD` rows.

#### Writes
- `book` – decrement `copies_free` in `fn_checkout_book`; increment in `sp_return_book`.
- `loan` – insert new row in `fn_checkout_book`; update `returned_on` in `sp_return_book`.
- `loan_audit` – insert audit rows in `trg_loan_audit_fn` (one per checkout or return).

#### Relationships
- `loan.book_id` → `book.book_id`
- `loan.member_id` → `member.member_id`
- `loan_audit.loan_id` → `loan.loan_id`

#### Routine logic
**fn_checkout_book(p_member_id, p_book_id, p_days)**
- Verify member not blocked.
- Decrement `book.copies_free`; error if none.
- Insert into `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.

**sp_return_book(p_loan_id)**
- Update `loan.returned_on` to `CURRENT_DATE`; capture `book_id`.
- If no row updated, raise exception.
- Increment `book.copies_free`.

**fn_late_fee(p_due_on, p_returned_on)**
- Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
- Multiply by 50, cap at 2000, ensure non‑negative.

**trg_loan_audit_fn()**
- On INSERT: log `CHECKOUT`.
- On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Trigger: `trg_loan_audit` on `loan` table.
