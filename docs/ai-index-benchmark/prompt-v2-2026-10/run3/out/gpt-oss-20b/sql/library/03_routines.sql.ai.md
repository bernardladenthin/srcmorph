### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:08:11Z
- G: 
- A: 0.0.0
- X: file
---
> Records every checkout and return event for library loans, and provides helper routines for lending, returning, and fee calculation.

#### Purpose
- Capture audit trail of loan actions.
- Provide checkout and return logic.
- Compute late fees.

#### Type
- Table: `loan_audit` (PostgreSQL).
- Function: `fn_checkout_book` (plpgsql), `fn_late_fee` (sql).
- Procedure: `sp_return_book` (plpgsql).
- Trigger function: `trg_loan_audit_fn` (plpgsql).
- Trigger: `trg_loan_audit` (PostgreSQL).

#### Schema
`loan_audit`  
- `audit_id BIGSERIAL PRIMARY KEY`  
- `loan_id BIGINT NOT NULL`  
- `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`  
- `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`  

#### Reads
- `fn_checkout_book`: reads `member` (`member_id`, `is_blocked`), `book` (`book_id`, `copies_free`), `loan` (none).  
- `sp_return_book`: reads `loan` (`loan_id`, `returned_on`, `book_id`), `book` (`book_id`).  
- `fn_late_fee`: reads parameters only.  
- `trg_loan_audit_fn`: reads `NEW`, `OLD` rows of `loan`.  

#### Writes
- `fn_checkout_book`:  
  - `UPDATE book SET copies_free = copies_free - 1 WHERE book_id = p_book_id AND copies_free > 0` (1 row).  
  - `INSERT INTO loan (book_id, member_id, due_on) VALUES (...)` (1 row).  
- `sp_return_book`:  
  - `UPDATE loan SET returned_on = CURRENT_DATE WHERE loan_id = p_loan_id AND returned_on IS NULL` (1 row).  
  - `UPDATE book SET copies_free = copies_free + 1 WHERE book_id = v_book_id` (1 row).  
- `trg_loan_audit_fn`:  
  - On INSERT: `INSERT INTO loan_audit (loan_id, action) VALUES (NEW.loan_id, 'CHECKOUT')` (1 row).  
  - On UPDATE of `returned_on`: `INSERT INTO loan_audit (loan_id, action) VALUES (NEW.loan_id, 'RETURN')` (1 row).  

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` (implicit via trigger).  
- `loan.book_id -> book.book_id` (used in procedures).  
- `loan.member_id -> member.member_id` (used in checkout).  

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**  
  - Reject if member blocked.  
  - Decrement `book.copies_free`; error if none.  
  - Insert new `loan` with `due_on = CURRENT_DATE + p_days`.  
  - Return new `loan_id`.  
- **sp_return_book(p_loan_id)**  
  - Set `loan.returned_on` to today if not already returned; capture `book_id`.  
  - Increment `book.copies_free`.  
- **fn_late_fee(p_due_on, p_returned_on)**  
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.  
  - Multiply by 50, cap at 2000.  
- **trg_loan_audit_fn()**  
  - On INSERT: log `CHECKOUT`.  
  - On UPDATE where `returned_on` changes from NULL to non‑NULL: log `RETURN`.  

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.  
- Functions: `fn_late_fee`.  
- Trigger: `trg_loan_audit` on `loan`.
