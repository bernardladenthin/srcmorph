### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:08:52Z
- G: 
- A: 0.0.0
- X: file
---
```markdown
## Purpose
- Provides a complete checkout, return, late fee calculation, and audit logging system for lending books in a library.

## Type
- PostgreSQL PL/pgSQL procedures and functions.

## Schema
- **loan_audit** (table)
  - `audit_id BIGSERIAL PRIMARY KEY`
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`
- **loan** (assumed table, not shown)
  - `loan_id BIGINT PRIMARY KEY`
  - `book_id BIGINT NOT NULL`
  - `member_id BIGINT NOT NULL`
  - `due_on DATE NOT NULL`
  - `returned_on TIMESTAMP WITH TIME ZONE`

## Reads
- `book` (for checking free copies and updating copies_free)
- `member` (for blocking status)

## Writes
- `loan` (INSERT for new loans)
- `loan_audit` (INSERT for audit records)
- `book` (UPDATE for updating copies_free)

## Relationships
- `loan.loan_id -> loan_audit.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

## Routine Logic
- **fn_checkout_book**
  - Checks member status.
  - Decreases free copies of the book.
  - Inserts a new loan record.
  - Returns the new loan_id.
- **sp_return_book**
  - Marks a loan as returned.
  - Updates the book's free copies.
  - Raises exception if loan is unknown or already returned.
- **fn_late_fee**
  - Calculates late fee: 50 cents per overdue day, capped at 2000 cents.
- **trg_loan_audit_fn**
  - Logs CHECKOUT and RETURN actions in `loan_audit`.

## Dependencies
- `book` table (for copies management)
- `member` table (for blocking checks)
- `loan` table (for loan records)
- `loan_audit` table (for audit logging)
```
