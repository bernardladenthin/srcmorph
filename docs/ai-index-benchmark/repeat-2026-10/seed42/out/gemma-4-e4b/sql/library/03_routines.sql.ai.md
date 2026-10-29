### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:51:18Z
- G: 
- A: 0.0.0
- X: file
---
> This schema manages book lending operations, tracking checkouts, returns, associated fees, and maintaining an audit trail of loan status changes.

#### Purpose
* Defines the `loan_audit` table to log loan lifecycle events (CHECKOUT, RETURN).
* Provides functions (`fn_checkout_book`, `fn_late_fee`) and procedures (`sp_return_book`) to manage lending logic.
* Implements a trigger (`trg_loan_audit`) to automatically record loan actions in `loan_audit`.

#### Type
table, function, procedure, trigger (PostgreSQL, PL/pgSQL, SQL)

#### Schema
loan_audit table:
audit_id BIGSERIAL PRIMARY KEY
loan_id BIGINT NOT NULL
action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
changed_at TIMESTAMPTZ NOT NULL DEFAULT now()

#### Reads
member table (member_id, is_blocked)
book table (book_id, copies_free)
loan table (loan_id, book_id, member_id, due_on, returned_on)

#### Writes
loan_audit table (loan_id, action)
book table (copies_free)
loan table (loan_id, returned_on)

#### Relationships
member.member_id -> loan.member_id
book.book_id -> loan.book_id
loan.loan_id -> loan_audit.loan_id

#### Routine logic
fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21):
* Checks if member (p_member_id) is blocked in member table.
* Decrements copies_free in book table for book_id (p_book_id).
* Inserts new record into loan table (book_id, member_id, due_on).
* Returns new loan_id.

sp_return_book(p_loan_id BIGINT):
* Updates loan table for loan_id (p_loan_id) setting returned_on to CURRENT_DATE.
* Retrieves book_id from updated loan record.
* Increments copies_free in book table for retrieved book_id.

fn_late_fee(p_due_on DATE, p_returned_on DATE):
* Calculates late fee: (Days overdue * 50) capped at 2000 cents.

trg_loan_audit_fn():
* Fires on INSERT into loan: Inserts ('CHECKOUT') into loan_audit using NEW.loan_id.
* Fires on UPDATE of returned_on in loan: Inserts ('RETURN') into loan_audit using NEW.loan_id if returned_on is set and OLD.returned_on is null.

#### Dependencies
member
book
loan
