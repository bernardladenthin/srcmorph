### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T02:00:31Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library: checkout, return, late fee and audit trail (PostgreSQL, PL/pgSQL).

#### Purpose
- **Checkout a book**: Lends one free copy of a book to a member for a specified number of days and returns the new loan ID.
- **Return a book**: Marks a loan as returned and puts the copy back.
- **Calculate late fee**: Computes the late fee based on the number of days the book was overdue, with a maximum of $2000.
- **Audit trail**: Records actions (checkout and return) on the loan table.

#### Type
PostgreSQL, PL/pgSQL.

#### Schema
- **loan_audit**
  - `audit_id` BIGSERIAL PRIMARY KEY
  - `loan_id` BIGINT NOT NULL
  - `action` VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
  - `changed_at` TIMESTAMPTZ NOT NULL DEFAULT now()

#### Reads
- **fn_checkout_book**: Reads from `member`, `book`.
- **sp_return_book**: Reads from `loan`.
- **fn_late_fee**: Reads from `loan`.
- **trg_loan_audit_fn**: Reads from `loan`.

#### Writes
- **fn_checkout_book**: Writes to `loan`.
- **sp_return_book**: Writes to `loan`, `book`.
- **fn_late_fee**: No writes.
- **trg_loan_audit_fn**: Writes to `loan_audit`.

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book**:
  - Parameters: `p_member_id`, `p_book_id`, `p_days` (default 21).
  - Steps:
    1. Check if the member is blocked.
    2. Update the book's free copies.
    3. Insert a new loan record and return the new loan ID.
- **sp_return_book**:
  - Parameter: `p_loan_id`.
  - Steps:
    1. Mark the loan as returned.
    2. Update the book's free copies.
- **fn_late_fee**:
  - Parameters: `p_due_on`, `p_returned_on`.
  - Steps: Calculate the late fee based on the number of days overdue, capped at $2000.
- **trg_loan_audit_fn**:
  - Triggers on `INSERT` and `UPDATE OF returned_on` on `loan`.
  - Steps: Insert audit records for checkout and return actions.

#### Dependencies
- `member`
- `book`
- `loan`
