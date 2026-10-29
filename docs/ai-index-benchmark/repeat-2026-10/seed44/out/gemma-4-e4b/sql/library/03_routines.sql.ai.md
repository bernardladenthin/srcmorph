### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:12Z
- G: 
- A: 0.0.0
- X: file
---
> This schema manages book lending operations, tracking checkouts, returns, calculating late fees, and maintaining an audit trail for loan events.

#### Purpose
* Manages book lending lifecycle: checkout, return, and fee calculation.
* Provides an immutable audit log for loan status changes.

#### Type
table, function, procedure, trigger (PostgreSQL, PL/pgSQL)

#### Schema
loan_audit
audit_id BIGSERIAL PRIMARY KEY
loan_id BIGINT NOT NULL
action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
changed_at TIMESTAMPTZ NOT NULL DEFAULT now()

#### Reads
member.member_id (in fn_checkout_book)
book.book_id, book.copies_free (in fn_checkout_book, sp_return_book)
loan.loan_id, loan.returned_on, loan.book_id (in sp_return_book, trg_loan_audit_fn)
loan.loan_id, loan.returned_on (in trg_loan_audit_fn)

#### Writes
loan_audit (loan_id, action) (in trg_loan_audit_fn)
book.copies_free (in fn_checkout_book, sp_return_book)
loan (book_id, member_id, due_on) (in fn_checkout_book)
loan (returned_on) (in sp_return_book)

#### Relationships
member.member_id -> loan.member_id (implied by fn_checkout_book)
book.book_id -> loan.book_id (in fn_checkout_book, sp_return_book)
loan.loan_id -> loan_audit.loan_id (in trg_loan_audit_fn)

#### Routine logic
fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER):
* Checks if member p_member_id is blocked in member table.
* Decrements book.copies_free for book_id p_book_id if available.
* Inserts new record into loan table with due_on calculated as CURRENT_DATE + p_days.
* Returns new loan_id.

sp_return_book(p_loan_id BIGINT):
* Updates loan record for p_loan_id setting returned_on to CURRENT_DATE.
* Retrieves book_id from updated loan record.
* Increments book.copies_free for the retrieved book_id.

fn_late_fee(p_due_on DATE, p_returned_on DATE):
* Calculates late fee: (days overdue * 50), capped at 2000.
* Uses COALESCE(p_returned_on, CURRENT_DATE) for calculation.

trg_loan_audit_fn():
* Fires on INSERT into loan: Inserts 'CHECKOUT' into loan_audit.
* Fires on UPDATE of returned_on in loan: Inserts 'RETURN' into loan_audit if returned_on changes from NULL to non-NULL.

#### Dependencies
member (in fn_checkout_book)
book (in fn_checkout_book, sp_return_book)
loan (in sp_return_book, trg_loan_audit_fn)
loan_audit (in trg_loan_audit_fn)
