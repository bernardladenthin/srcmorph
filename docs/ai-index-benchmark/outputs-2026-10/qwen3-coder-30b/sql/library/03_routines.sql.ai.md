### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:17:10Z
- G: 
- A: 0.0.0
- X: file
---
> Manages book lending operations including checkout, return, late fees, and audit logging for a library system.

#### Purpose
- Processes book checkouts and returns
- Calculates late fees for overdue books
- Maintains audit trail of loan actions

#### Type
- Table: loan_audit
- Function: fn_checkout_book, fn_late_fee, trg_loan_audit_fn
- Procedure: sp_return_book
- Trigger: trg_loan_audit

#### Schema
- loan_audit.audit_id BIGSERIAL PRIMARY KEY
- loan_audit.loan_id BIGINT NOT NULL
- loan_audit.action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
- loan_audit.changed_at TIMESTAMPTZ NOT NULL DEFAULT now()

#### Reads
- fn_checkout_book: member.member_id, member.is_blocked, book.book_id, book.copies_free
- sp_return_book: loan.loan_id, loan.returned_on, loan.book_id
- fn_late_fee: p_due_on DATE, p_returned_on DATE
- trg_loan_audit_fn: NEW.loan_id, NEW.returned_on, OLD.returned_on

#### Writes
- fn_checkout_book: book.copies_free, loan (INSERT)
- sp_return_book: loan.returned_on, book.copies_free
- loan_audit (INSERT)

#### Relationships
- loan_audit.loan_id -> loan.loan_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id

#### Routine logic
- fn_checkout_book: validates member not blocked, checks book availability, updates book copies, inserts loan record
- sp_return_book: marks loan as returned, increments book copies
- fn_late_fee: computes late fee based on days overdue with cap
- trg_loan_audit_fn: logs checkout or return actions to audit table

#### Dependencies
- member
- book
- loan
- loan_audit
