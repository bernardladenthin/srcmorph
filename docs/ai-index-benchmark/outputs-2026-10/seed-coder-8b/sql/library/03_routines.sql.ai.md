### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:19:01Z
- G: 
- A: 0.0.0
- X: file
---
> Manages lending library operations: book checkouts, returns, late fees, and audit trails.

#### Purpose
- Manages lending library operations
- Tracks book checkouts and returns
- Calculates late fees
- Maintains audit trail of lending activities

#### Type
- Table: loan_audit
- Function: fn_checkout_book, fn_late_fee, trg_loan_audit_fn
- Procedure: sp_return_book
- Trigger: trg_loan_audit

#### Schema
##### loan_audit
- audit_id BIGSERIAL PRIMARY KEY
- loan_id BIGINT NOT NULL
- action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
- changed_at TIMESTAMPTZ NOT NULL DEFAULT now()

#### Reads
- loan_audit: loan_id, action
- loan: loan_id, returned_on
- book: book_id, copies_free
- member: member_id, is_blocked

#### Writes
- loan_audit: audit_id, loan_id, action, changed_at
- loan: loan_id, returned_on
- book: book_id, copies_free

#### Relationships
- loan_audit.loan_id -> loan.loan_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id

#### Routine logic
- fn_checkout_book:
  - Checks if member is blocked
  - Updates book availability
  - Creates new loan record
  - Returns new loan ID
- sp_return_book:
  - Updates loan record with return date
  - Updates book availability
- fn_late_fee:
  - Calculates late fee based on due date and return date
- trg_loan_audit_fn:
  - Inserts audit record for new checkouts
  - Inserts audit record for book returns

#### Dependencies
- loan table
- book table
- member table
