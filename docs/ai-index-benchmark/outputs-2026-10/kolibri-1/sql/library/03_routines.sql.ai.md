### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:29:10Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library checkout, return, late fee, and audit trail operations for books and members.

#### Purpose
- Checkout a book to a member for a specified number of days
- Return a loan and restore the book copy
- Calculate a late fee in cents
- Maintain an audit trail of checkout and return actions

#### Type
- Table: loan_audit
- Function: fn_checkout_book
- Procedure: sp_return_book
- Function: fn_late_fee
- Trigger: trg_loan_audit

#### Schema
- loan_audit: audit_id BIGSERIAL PK, loan_id BIGINT NOT NULL, action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN')), changed_at TIMESTAMPTZ NOT NULL DEFAULT now()
- book: book_id PK, copies_free INTEGER
- loan: loan_id PK, book_id BIGINT, member_id BIGINT, due_on DATE, returned_on DATE
- member: member_id PK, is_blocked BOOLEAN

#### Reads
- fn_checkout_book: member.member_id, member.is_blocked, book.book_id, book.copies_free, loan.book_id, loan.member_id, loan.due_on
- sp_return_book: loan.loan_id, loan.returned_on, loan.book_id, book.book_id, book.copies_free
- fn_late_fee: p_due_on, p_returned_on
- trg_loan_audit_fn: loan.loan_id, loan.returned_on, loan.NEW.loan_id, loan.NEW.returned_on, loan.OLD.returned_on

#### Writes
- fn_checkout_book: book.copies_free, loan.loan_id, loan.book_id, loan.member_id, loan.due_on
- sp_return_book: loan.returned_on, loan.loan_id, loan.book_id, book.copies_free
- trg_loan_audit_fn: loan_audit.audit_id, loan_audit.loan_id, loan_audit.action, loan_audit.changed_at
- trg_loan_audit: loan.returned_on

#### Relationships
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id
- loan.loan_id -> loan_audit.loan_id

#### Routine logic
- fn_checkout_book: checks member.is_blocked, decrements book.copies_free, inserts loan with due_on, returns loan_id
- sp_return_book: sets loan.returned_on, increments book.copies_free
- fn_late_fee: calculates days overdue, multiplies by 50, caps at 2000
- trg_loan_audit_fn: inserts audit row on loan INSERT or returned_on update

#### Dependencies
- member, book, loan, loan_audit
