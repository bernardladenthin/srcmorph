### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:49:48Z
- G: 
- A: 0.0.0
- X: file
---
> Manages the lending lifecycle of library books, including checkouts, returns, late fee calculations, and automated audit logging.

#### Purpose
* Automate book availability management and loan tracking.
* Maintain a historical audit trail of all loan activities.

#### Type
Table, Function, Procedure, Trigger (PostgreSQL/PL/pgSQL).

#### Schema
loan_audit
* audit_id BIGSERIAL PK
* loan_id BIGINT NOT NULL
* action VARCHAR(10) NOT NULL (CHECK: 'CHECKOUT', 'RETURN')
* changed_at TIMESTAMPTZ NOT NULL DEFAULT now()

#### Reads
* fn_checkout_book: member.member_id, member.is_blocked, book.book_id, book.copies_free, loan.loan_id
* sp_return_book: loan.loan_id, loan.returned_on, loan.book_id, book.book_id, book.copies_free
* fn_late_fee: p_due_on, p_returned_on
* trg_loan_audit_fn: loan.loan_id, loan.returned_on

#### Writes
* fn_checkout_book: book.copies_free, loan.book_id, loan.member_id, loan.due_on
* sp_return_book: loan.returned_on, book.copies_free
* trg_loan_audit_fn: loan_audit.loan_id, loan_audit.action

#### Relationships
* loan_audit.loan_id -> loan.loan_id
* fn_checkout_book: member.member_id -> member.member_id
* fn_checkout_book: book.book_id -> book.book_id
* sp_return_book: loan.book_id -> book.book_id

#### Routine logic
fn_checkout_book
* Parameters: p_member_id (BIGINT), p_book_id (BIGINT), p_days (INTEGER)
* Validates member is not blocked.
* Decrements book.copies_free if > 0.
* Inserts new record into loan with calculated due_on date.
* Returns new loan_id.

sp_return_book
* Parameters: p_loan_id (BIGINT)
* Updates loan.returned_on to current date.
* Increments book.copies_free for the associated book.

fn_late_fee
* Parameters: p_due_on (DATE), p_returned_on (DATE)
* Calculates days overdue (using current date if not returned).
* Multiplies days by 50 cents.
* Caps fee at 2000 cents.

trg_loan_audit_fn
* Trigger: AFTER INSERT OR UPDATE OF returned_on ON loan
* On INSERT: Logs 'CHECKOUT' to loan_audit.
* On UPDATE (where returned_on changes from NULL to value): Logs 'RETURN' to loan_audit.

#### Dependencies
* member
* book
* loan
* loan_audit
