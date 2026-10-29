### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:04:03Z
- G: 
- A: 0.0.0
- X: file
---
> A PostgreSQL PL/pgSQL routine set managing book loans, checkouts, returns, late fee calculations, and automatic audit logging for the lending library domain.

#### Purpose
- Models the checkout and return lifecycle for book loans, tracking member borrowing, book availability, and overdue status.
- Calculates late fees based on days overdue with a daily rate and maximum cap.
- Automatically logs every loan creation and return event into an audit trail table.

#### Type
- Table: `loan_audit` (PostgreSQL)
- Function: `fn_checkout_book` (PostgreSQL, PL/pgSQL)
- Procedure: `sp_return_book` (PostgreSQL, PL/pgSQL)
- Function: `fn_late_fee` (PostgreSQL, SQL, Immutable)
- Trigger: `trg_loan_audit` (PostgreSQL, PL/pgSQL)

#### Schema
**loan_audit**
- `audit_id` BIGSERIAL PRIMARY KEY
- `loan_id` BIGINT NOT NULL
- `action` VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
- `changed_at` TIMESTAMPTZ NOT NULL DEFAULT now()

#### Reads
- `member` table: checks `is_blocked` status for `p_member_id` in `fn_checkout_book`.
- `book` table: decrements `copies_free` in `fn_checkout_book`; reads `book_id` in `sp_return_book`.
- `loan` table: reads `loan_id` and `returned_on` status in `sp_return_book`; reads `loan_id` in `trg_loan_audit_fn`.
- `CURRENT_DATE` and `CURRENT_TIMESTAMP` in date calculations.

#### Writes
- `loan` table: inserts new loan records with `book_id`, `member_id`, and `due_on` in `fn_checkout_book`.
- `book` table: updates `copies_free` count in `fn_checkout_book` and `sp_return_book`.
- `loan` table: updates `returned_on` timestamp in `sp_return_book`.
- `loan_audit` table: inserts audit records for 'CHECKOUT' and 'RETURN' actions in `trg_loan_audit`.

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` (Foreign key reference)
- `fn_checkout_book.p_member_id -> member.member_id` (Logical join)
- `fn_checkout_book.p_book_id -> book.book_id` (Logical join)
- `sp_return_book.p_loan_id -> loan.loan_id` (Logical join)
- `sp_return_book.v_book_id -> book.book_id` (Logical join)

#### Routine logic
- `fn_checkout_book(p_member_id, p_book_id, p_days)`: Validates member is not blocked, decrements free book copies, inserts a new loan record, returns the new `loan_id`.
- `sp_return_book(p_loan_id)`: Updates the loan's `returned_on` date, retrieves the associated `book_id`, and increments the book's free copies.
- `fn_late_fee(p_due_on, p_returned_on)`: Calculates overdue days, multiplies by 50 cents/day, caps result at 2000, returns total fee in cents.
- `trg_loan_audit_fn()`: Automatically inserts audit records into `loan_audit` whenever a loan is inserted (checkout) or updated (return).

#### Dependencies
- `member` table
- `book` table
- `loan` table
- `loan_audit` table
- `fn_late_fee` function (referenced by late fee logic)
