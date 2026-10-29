### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T04:03:23Z
- G: 
- A: 0.0.0
- X: file
---
> This file manages the core business logic for a lending library, including book checkouts, returns, late fee calculations, and an automated audit trail.

#### Purpose
*   Handles the lifecycle of book loans (checkout/return).
*   Calculates late fees and maintains an audit log of loan status changes.

#### Type
Table, Function, Procedure, Trigger (PostgreSQL, PL/pgSQL)

#### Schema
loan_audit
audit_id BIGSERIAL PRIMARY KEY
loan_id BIGINT NOT NULL
action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
changed_at TIMESTAMPTZ NOT NULL DEFAULT now()

#### Reads
*   member: member_id, is_blocked
*   book: book_id, copies_free
*   loan: loan_id, book_id, returned_on, due_on

#### Writes
*   book: copies_free
*   loan: book_id, member_id, due_on, returned_on
*   loan_audit: loan_id, action, changed_at

#### Relationships
*   loan.book_id -> book.book_id
*   loan.member_id -> member.member_id
*   loan_audit.loan_id -> loan.loan_id

#### Routine logic
fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER)
*   Validates that the member is not blocked.
*   Decrements `copies_free` in `book` table if available.
*   Inserts a new record into `loan` with a calculated `due_on` date.
*   Returns the new `loan_id`.

sp_return_book(p_loan_id BIGINT)
*   Updates `loan` record with `returned_on` date.
*   Increments `copies_free` in `book` table for the associated book.
*   Raises exception if loan is unknown or already returned.

fn_late_fee(p_due_on DATE, p_returned_on DATE)
*   Calculates fee: 50 cents per day overdue.
*   Caps the fee at 2000 cents.
*   Uses `CURRENT_DATE` if `p_returned_on` is null.

trg_loan_audit_fn()
*   Trigger function for `loan` table.
*   Inserts 'CHECKOUT' action into `loan_audit` on new loan insertion.
*   Inserts 'RETURN' action into `loan_audit` when `returned_on` is updated from null.

#### Dependencies
*   member
*   book
*   loan
*   loan_audit
