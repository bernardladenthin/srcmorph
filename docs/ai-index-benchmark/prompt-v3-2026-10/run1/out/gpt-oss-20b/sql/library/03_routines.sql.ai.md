### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:54:40Z
- G: 
- A: 0.0.0
- X: file
---
> Records checkout and return events for library loans, calculates late fees, and updates book inventory.

#### Purpose
- Audit trail for loan actions (checkout, return).
- Enforce checkout rules and update inventory.
- Compute late fees.

#### Type
- Table: `loan_audit` (PostgreSQL).
- Function: `fn_checkout_book` (plpgsql), `fn_late_fee` (sql), `trg_loan_audit_fn` (plpgsql).
- Procedure: `sp_return_book` (plpgsql).
- Trigger: `trg_loan_audit` (AFTER INSERT OR UPDATE OF returned_on ON loan).

#### Schema
`loan_audit`  
- `audit_id BIGSERIAL PRIMARY KEY`  
- `loan_id BIGINT NOT NULL`  
- `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`  
- `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`  

#### Reads
- `fn_checkout_book`: `member` (`member_id`, `is_blocked`), `book` (`book_id`, `copies_free`), `loan` (none).  
- `sp_return_book`: `loan` (`loan_id`, `returned_on`, `book_id`), `book` (`book_id`).  
- `fn_late_fee`: parameters only.  
- `trg_loan_audit_fn`: `NEW`, `OLD` rows of `loan`.  

#### Writes
- `fn_checkout_book`:  
  - `book`: 1 row (`copies_free` decremented).  
  - `loan`: 1 row (`INSERT` → `loan_id` returned).  
- `sp_return_book`:  
  - `loan`: 1 row (`returned_on` updated).  
  - `book`: 1 row (`copies_free` incremented).  
- `trg_loan_audit_fn`:  
  - `loan_audit`: 1 row per INSERT or UPDATE that sets `returned_on`.  

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` (audit reference).  
- `loan.book_id -> book.book_id` (implicit via `sp_return_book`).  
- `loan.member_id -> member.member_id` (implicit via `fn_checkout_book`).  

#### Routine logic
`fn_checkout_book(p_member_id, p_book_id, p_days)`  
- Abort if member blocked.  
- Decrement `book.copies_free`; error if none.  
- Insert new `loan` with `due_on = CURRENT_DATE + p_days`.  
- Return new `loan_id`.

`sp_return_book(p_loan_id)`  
- Update `loan.returned_on` to `CURRENT_DATE`; capture `book_id`.  
- If no row updated, raise exception.  
- Increment `book.copies_free`.

`fn_late_fee(p_due_on, p_returned_on)`  
- Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.  
- Multiply by 50, cap at 2000.

`trg_loan_audit_fn()`  
- On INSERT: log `CHECKOUT`.  
- On UPDATE where `returned_on` changes from NULL to NOT NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.  
- Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`.  
- Trigger: `trg_loan_audit`.
