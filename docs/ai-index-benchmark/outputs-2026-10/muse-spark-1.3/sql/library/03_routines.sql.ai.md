### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:16:16Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library checkout, return, overdue fee calculation, and loan audit trail.
#### Purpose
- Lend book copies to members, process returns, compute late fees, audit loan lifecycle.
- Enforces blocked-member check, free-copy availability, overdue cap.
#### Type
- table / function / procedure / trigger, PostgreSQL PL/pgSQL + SQL
#### Schema
- loan_audit: `audit_id BIGSERIAL` PK, `loan_id BIGINT` NOT NULL, `action VARCHAR(10)` NOT NULL CHECK IN CHECKOUT RETURN, `changed_at TIMESTAMPTZ` NOT NULL DEFAULT now()
#### Reads
- member.member_id, member.is_blocked in fn_checkout_book blocked check
- book.book_id, book.copies_free in fn_checkout_book availability check
- loan.loan_id, loan.returned_on, loan.book_id in sp_return_book return validation
- loan.loan_id, loan.returned_on in trg_loan_audit_fn INSERT / UPDATE detection via NEW.loan_id, OLD.returned_on, NEW.returned_on
#### Writes
- loan: INSERT book_id, member_id, due_on in fn_checkout_book; UPDATE returned_on in sp_return_book
- book.copies_free: decrement on checkout, increment on return
- loan_audit.loan_id, loan_audit.action: INSERT CHECKOUT on loan INSERT, INSERT RETURN on returned_on transition
#### Relationships
- loan_audit.loan_id -> loan.loan_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id
#### Routine logic
- fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT: rejects if member.is_blocked, decrements book.copies_free where copies_free > 0, INSERTs loan with due_on CURRENT_DATE + p_days, returns v_loan_id
- sp_return_book(p_loan_id BIGINT): UPDATEs loan.returned_on = CURRENT_DATE where returned_on IS NULL, RETURNING book_id, raises if unknown/already returned, increments book.copies_free
- fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER IMMUTABLE: LEAST(GREATEST(COALESCE(p_returned_on,CURRENT_DATE)-p_due_on,0)*50,2000), 50 cents/day capped 2000
- trg_loan_audit_fn() RETURNS TRIGGER: on TG_OP INSERT logs CHECKOUT, on returned_on NULL->NOT NULL logs RETURN, RETURN NEW
- trg_loan_audit AFTER INSERT OR UPDATE OF returned_on ON loan FOR EACH ROW EXECUTE FUNCTION trg_loan_audit_fn()
#### Dependencies
- tables member, book, loan, loan_audit
- functions fn_checkout_book, fn_late_fee, trg_loan_audit_fn
- procedure sp_return_book
- trigger trg_loan_audit
