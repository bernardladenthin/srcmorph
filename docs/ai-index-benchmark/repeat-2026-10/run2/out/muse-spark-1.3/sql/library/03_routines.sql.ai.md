### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:16:51Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library checkout, return, overdue fee calculation, and loan audit trail.
#### Purpose
- Checkout/return circulating books, compute late fees, audit loan lifecycle.
- Enforce blocked-member and available-copy rules for lending library.
#### Type
- table / function / procedure / trigger, PostgreSQL PL/pgSQL, SQL.
#### Schema
- loan_audit: audit_id BIGSERIAL PK, loan_id BIGINT NOT NULL, action VARCHAR(10) NOT NULL CHECK CHECKOUT RETURN, changed_at TIMESTAMPTZ NOT NULL DEFAULT now()
- Index/PK: loan_audit audit_id PRIMARY KEY.
#### Reads
- member.member_id, member.is_blocked in fn_checkout_book blocked check.
- book.book_id, book.copies_free in fn_checkout_book decrement and sp_return_book increment.
- loan.loan_id, loan.returned_on, loan.book_id in sp_return_book RETURNING book_id.
- loan.loan_id, loan.returned_on via NEW.loan_id, NEW.returned_on, OLD.returned_on in trg_loan_audit_fn.
- fn_late_fee inputs p_due_on DATE, p_returned_on DATE, CURRENT_DATE fallback.
#### Writes
- loan.book_id, loan.member_id, loan.due_on, loan.loan_id via INSERT RETURNING loan_id in fn_checkout_book.
- book.copies_free decrement in fn_checkout_book, increment in sp_return_book.
- loan.returned_on via UPDATE WHERE returned_on IS NULL in sp_return_book.
- loan_audit.loan_id, loan_audit.action CHECKOUT on loan INSERT, RETURN on returned_on transition.
- Trigger target: loan AFTER INSERT OR UPDATE OF returned_on.
#### Relationships
- loan_audit.loan_id -> loan.loan_id via NEW.loan_id audit insert.
- loan.member_id -> member.member_id via p_member_id checkout.
- loan.book_id -> book.book_id via p_book_id checkout and v_book_id return.
- book.book_id -> book.book_id via copies_free update by book_id.
#### Routine logic
- fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT: rejects is_blocked member, decrements book.copies_free where copies_free > 0, INSERTs loan due_on CURRENT_DATE + p_days, RETURNs v_loan_id.
- sp_return_book(p_loan_id BIGINT): UPDATEs loan.returned_on CURRENT_DATE where returned_on IS NULL RETURNING book_id, raises if unknown/already returned, increments book.copies_free.
- fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER LANGUAGE sql IMMUTABLE: LEAST GREATEST overdue-days * 50 capped 2000, COALESCE returned CURRENT_DATE.
- trg_loan_audit_fn() RETURNS TRIGGER: on TG_OP INSERT inserts loan_audit CHECKOUT, on returned_on NULL-to-not-NULL inserts loan_audit RETURN, RETURN NEW.
- trg_loan_audit AFTER INSERT OR UPDATE OF returned_on ON loan FOR EACH ROW EXECUTE FUNCTION trg_loan_audit_fn().
#### Dependencies
- Tables: loan, book, member, loan_audit.
- Functions/procedures: fn_checkout_book, sp_return_book, fn_late_fee, trg_loan_audit_fn.
- Trigger: trg_loan_audit on loan.
