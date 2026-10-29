### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:48:13Z
- G: 
- A: 0.0.0
- X: file
---
> Models the lending library checkout, return, late-fee calculation, and audit-trail workflow for book loans.

#### Purpose
- Implements the core lending operations: `fn_checkout_book` to lend a copy, `sp_return_book` to return it, `fn_late_fee` to compute overdue charges, and `trg_loan_audit` to log state changes.
- Defines the `loan_audit` table to persist an immutable history of `CHECKOUT` and `RETURN` events.

#### Type
- PostgreSQL (PL/pgSQL and SQL dialect).
- Objects: 1 table (`loan_audit`), 2 functions (`fn_checkout_book`, `fn_late_fee`), 1 procedure (`sp_return_book`), 1 trigger function (`trg_loan_audit_fn`), 1 trigger (`trg_loan_audit`).

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL PRIMARY KEY`
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `fn_checkout_book`:
  - `member.member_id`, `member.is_blocked` (existence check).
  - `book.book_id`, `book.copies_free` (availability check).
- `sp_return_book`:
  - `loan.loan_id`, `loan.returned_on`, `loan.book_id` (status check and retrieval).
- `trg_loan_audit_fn`:
  - `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on` (trigger payload).

#### Writes
- `fn_checkout_book`:
  - `book.copies_free` (decrement).
  - `loan.book_id`, `loan.member_id`, `loan.due_on` (insert).
- `sp_return_book`:
  - `loan.returned_on` (set to `CURRENT_DATE`).
  - `book.copies_free` (increment).
- `trg_loan_audit_fn`:
  - `loan_audit.loan_id`, `loan_audit.action` (insert).

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` (logical reference, no FK constraint defined in this file).
- `fn_checkout_book` operates on `member` and `book` entities.
- `sp_return_book` operates on `loan` and `book` entities.

#### Routine logic
- `fn_checkout_book(p_member_id, p_book_id, p_days DEFAULT 21)`:
  - Raises exception if `member.is_blocked` is true.
  - Decrements `book.copies_free` if > 0; raises exception if no free copy.
  - Inserts into `loan` with `due_on = CURRENT_DATE + p_days`.
  - Returns new `loan_id`.
- `sp_return_book(p_loan_id)`:
  - Updates `loan.returned_on` to `CURRENT_DATE` if currently NULL.
  - Raises exception if loan not found or already returned.
  - Increments `book.copies_free` for the associated `book_id`.
- `fn_late_fee(p_due_on, p_returned_on)`:
  - Calculates days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Clamps to minimum 0, multiplies by 50 cents.
  - Caps total at 2000 cents.
  - Returns integer fee.
- `trg_loan_audit_fn()`:
  - On `INSERT` into `loan`: inserts `CHECKOUT` record into `loan_audit`.
  - On `UPDATE` of `loan.returned_on` from NULL to non-NULL: inserts `RETURN` record into `loan_audit`.
  - Returns `NEW`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`.
- Procedure: `sp_return_book`.
- Trigger: `trg_loan_audit`.
