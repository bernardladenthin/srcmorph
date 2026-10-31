### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:13:24Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library checkout, return, late fee calculation, and audit trail management for book loans.

#### Purpose
- Implements core lending operations: checkout, return, late fee, and audit logging for a book lending system.
- Provides PL/pgSQL routines for member/book management and audit trail.

#### Type
- Mixed object kinds: table, function, procedure, trigger (PL/pgSQL dialect).

#### Schema
- loan_audit table: audit_id BIGSERIAL PRIMARY KEY, loan_id BIGINT NOT NULL, action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT','RETURN')), changed_at TIMESTAMPTZ NOT NULL DEFAULT now().

#### Reads
- fn_checkout_book: member (is_blocked), book (copies_free), loan (loan_id, due_on).
- sp_return_book: loan (loan_id, returned_on), book (book_id).
- fn_late_fee: none.
- trg_loan_audit_fn: loan (loan_id, returned_on), loan_audit (loan_id, action).

#### Writes
- fn_checkout_book: book (copies_free decrement), loan (INSERT loan record).
- sp_return_book: loan (SET returned_on), book (copies_free increment).
- fn_late_fee: none.
- trg_loan_audit_fn: loan_audit (INSERT).

#### Relationships
- loan_audit.loan_id references loan (implicit foreign key).
- fn_checkout_book: book_id -> book, member_id -> member, loan_id -> loan.
- sp_return_book: loan_id -> loan, book_id -> book.
- trg_loan_audit_fn: loan insert/update -> loan_audit.

#### Routine logic
- fn_checkout_book(p_member_id, p_book_id, p_days): blocks member check, frees a copy, creates loan record, returns loan_id.
- sp_return_book(p_loan_id): marks loan returned, restores book copy, errors on unknown or already returned loan.
- fn_late_fee(p_due_on, p_returned_on): computes late fee as min(max(overdue days*50,0),2000) in cents.
- trg_loan_audit_fn(): logs CHECKOUT on loan insert, RETURN on returned_on update, returns row.

#### Dependencies
- member table (is_blocked validation), book table (copies_free), loan table (loan_id, returned_on), loan_audit table, PL/pgSQL language.
