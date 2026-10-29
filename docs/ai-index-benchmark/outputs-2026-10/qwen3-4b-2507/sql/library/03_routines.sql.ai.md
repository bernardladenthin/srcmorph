### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:13:22Z
- G: 
- A: 0.0.0
- X: file
---
> Models the lifecycle of book loans including checkout, return, late fees, and audit logging for compliance and traceability.

#### Purpose
- Tracks book checkout and return events with audit logging.
- Calculates late fees based on overdue days, capped at 2000 cents.
- Ensures book availability checks and member blocking validation during checkout.

#### Type
Function, procedure, trigger (PostgreSQL, PL/pgSQL)

#### Schema
**loan_audit**  
- audit_id BIGSERIAL PRIMARY KEY  
- loan_id BIGINT NOT NULL  
- action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))  
- changed_at TIMESTAMPTZ NOT NULL DEFAULT now()  

**loan** (implied via triggers and functions)  
- loan_id BIGINT (primary key, referenced in audit and functions)  
- book_id BIGINT (foreign key to book)  
- member_id BIGINT (foreign key to member)  
- due_on DATE  
- returned_on DATE  

**book** (implied via UPDATE operations)  
- book_id BIGINT (primary key)  
- copies_free INTEGER NOT NULL CHECK (copies_free >= 0)  

**member** (implied via function checks)  
- member_id BIGINT (primary key)  
- is_blocked BOOLEAN  

#### Reads
- member.member_id, member.is_blocked  
- book.book_id, book.copies_free  
- loan.loan_id, loan.returned_on, loan.due_on  

#### Writes
- loan (INSERT, UPDATE on returned_on)  
- book (UPDATE on copies_free)  
- loan_audit (INSERT on checkout or return)  

#### Relationships
- loan.loan_id → loan_audit.loan_id  
- loan.book_id → book.book_id  
- loan.member_id → member.member_id  

#### Routine logic
- fn_checkout_book: validates member is not blocked, reduces book copies_free, inserts new loan with due date, returns loan_id.  
- sp_return_book: marks loan as returned, increases book copies_free if valid.  
- fn_late_fee: computes fee in cents (50/day, capped at 2000), uses COALESCE for null returned_on.  
- trg_loan_audit_fn: logs 'CHECKOUT' on insert, 'RETURN' when returned_on is set and was null before.  

#### Dependencies
- member (for blocking check)  
- book (for copies_free availability)  
- loan (for audit and return logic)  
- current_date, now() (system functions)
