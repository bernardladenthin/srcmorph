### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:06:01Z
- G: 
- A: 0.0.0
- X: file
---
> A lending library system that models book checkouts, returns, late fees, and audit trails.

#### Purpose
- Models a lending library managing book checkouts, member returns, late fee calculation, and audit logging.
- Supports core library operations: loan creation, copy inventory adjustment, and return tracking.

#### Type
- SQL object kinds: table, function, procedure, trigger.
- Dialect: PostgreSQL, PL/pgSQL.

#### Schema
- audit_id BIGSERIAL PRIMARY KEY (PK)
- loan_id BIGINT NOT NULL
- action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
- changed_at TIMESTAMPTZ NOT NULL DEFAULT now()

#### Reads
- member.member_id, member.is_blocked (fn_checkout_book)
- book.book_id, book.copies_free (fn_checkout_book, sp_return_book)
- loan.loan_id, loan.returned_on (sp_return_book, trg_loan_audit_fn)

#### Writes
- book.copies_free (fn_checkout_book UPDATE, sp_return_book UPDATE)
- loan.returned_on (sp_return_book UPDATE, trg_loan_audit trigger)
- loan_audit.loan_id, loan_audit.action (trg_loan_audit_fn INSERT)

#### Routine logic
- fn_checkout_book: validates member.is_blocked, updates book.copies_free where book_id and copies_free>0, inserts into loan (book_id, member_id, due_on) returning loan_id.
- sp_return_book: updates loan set returned_on=CURRENT_DATE where loan_id and returned_on IS NULL, retrieves book_id, increments book.copies_free.
- fn_late_fee: returns least(greatest(COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, 0) * 50, 2000) as INTEGER.
- trg_loan_audit_fn: on INSERT INTO loan inserts into loan_audit (loan_id, action) VALUES (NEW.loan_id, 'CHECKOUT'); on UPDATE of loan.returned_on where NEW.returned_on IS NOT NULL and OLD.returned_on IS NULL inserts into loan_audit (loan_id, action) VALUES (NEW.loan_id, 'RETURN').

#### Dependencies
- Tables: member, book, loan, loan_audit
- Functions: fn_checkout_book, sp_return_book, fn_late_fee, trg_loan_audit_fn
