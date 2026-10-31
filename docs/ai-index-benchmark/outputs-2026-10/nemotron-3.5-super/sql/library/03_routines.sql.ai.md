### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:50:20Z
- G: 
- A: 0.0.0
- X: file
---
> Models lending-library checkouts, returns, late-fee calculation, and audit logging for book loans in PostgreSQL PL/pgSQL.

#### Purpose
- fn_checkout_book lends one free copy of a book to a member for a configurable number of days and returns the new loan id.
- sp_return_book marks an existing loan as returned and restores the book copy.
- fn_late_fee computes an overdue fee in cents, capped at 2000.
- trg_loan_audit_fn with trigger trg_loan_audit records CHECKOUT and RETURN actions into loan_audit.

#### Type
PostgreSQL table, PL/pgSQL function, PL/pgSQL procedure, SQL function, PL/pgSQL trigger function, and database trigger.

#### Schema
loan_audit:
- audit_id BIGSERIAL PRIMARY KEY
- loan_id BIGINT NOT NULL
- action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
- changed_at TIMESTAMPTZ NOT NULL DEFAULT now()

No additional indexes beyond the primary key on audit_id.

#### Reads
- member.member_id and member.is_blocked from member
- book.book_id and book.copies_free from book
- loan.loan_id, loan.book_id, loan.member_id, loan.due_on, loan.returned_on from loan
- fn_late_fee parameters p_due_on DATE and p_returned_on DATE

#### Writes
- book.copies_free decremented in fn_checkout_book and incremented in sp_return_book
- loan.book_id, loan.member_id, loan.due_on inserted into loan; loan.returned_on updated in loan
- loan_audit.loan_id, loan_audit.action, loan_audit.changed_at inserted by trg_loan_audit_fn

#### Relationships
No explicit FOREIGN KEY constraints are declared in this file.
Implied join/filter pairs used by routines and trigger: member.member_id -> loan.member_id, book.book_id -> loan.book_id, loan.loan_id -> loan_audit.loan_id, loan.returned_on compared between OLD and NEW in the trigger.

#### Routine logic
fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT:
- raises exception if member.member_id = p_member_id and is_blocked is true
- updates book set copies_free = copies_free - 1 where book_id = p_book_id and copies_free > 0; raises exception if no row found
- inserts into loan (book_id, member_id, due_on) values (p_book_id, p_member_id, CURRENT_DATE + p_days) returning loan_id
- returns the new loan_id

sp_return_book(p_loan_id BIGINT):
- updates loan set returned_on = CURRENT_DATE where loan_id = p_loan_id and returned_on is null returning book_id
- raises exception if v_book_id is null (unknown or already returned loan)
- updates book set copies_free = copies_free + 1 where book_id = v_book_id

fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER IMMUTABLE:
- returns least(greatest(coalesce(p_returned_on, CURRENT_DATE) - p_due_on, 0) * 50, 2000)

trg_loan_audit_fn() RETURNS TRIGGER:
- on INSERT into loan: inserts into loan_audit (loan_id, action) values (NEW.loan_id, 'CHECKOUT')
- on UPDATE of loan.returned_on where NEW.returned_on is not null and OLD.returned_on is null: inserts into loan_audit (loan_id, action) values (NEW.loan_id, 'RETURN')
- returns NEW

#### Dependencies
- member (table referenced by fn_checkout_book)
- book (table referenced by fn_checkout_book and sp_return_book)
- loan (table referenced by fn_checkout_book, sp_return_book, and trigger trg_loan_audit)
- trg_loan_audit_fn (trigger function used by trigger trg_loan_audit)
- trg_loan_audit (trigger on loan)
