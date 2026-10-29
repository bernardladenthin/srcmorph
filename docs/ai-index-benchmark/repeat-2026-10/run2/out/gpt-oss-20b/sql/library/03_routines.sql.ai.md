### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:14:18Z
- G: 
- A: 0.0.0
- X: file
---
> Records and manages book loan transactions, including checkout, return, late fee calculation, and audit trail.

#### Purpose
- Capture audit events for loan insertions and updates.
- Provide checkout and return logic for library books.
- Compute late fees.

#### Type
- Table: `loan_audit` (PostgreSQL)
- Function: `fn_checkout_book` (plpgsql)
- Procedure: `sp_return_book` (plpgsql)
- Function: `fn_late_fee` (sql, immutable)
- Trigger function: `trg_loan_audit_fn` (plpgsql)
- Trigger: `trg_loan_audit` (AFTER INSERT/UPDATE OF returned_on ON loan)

#### Schema
`loan_audit`  
- `audit_id BIGSERIAL PRIMARY KEY`  
- `loan_id BIGINT NOT NULL`  
- `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`  
- `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `fn_checkout_book`: reads `member` (`member_id`, `is_blocked`), `book` (`copies_free`), `loan` (none)
- `sp_return_book`: reads `loan` (`returned_on`, `book_id`), `book` (`copies_free`)
- `fn_late_fee`: reads parameters only
- `trg_loan_audit_fn`: reads `TG_OP`, `NEW`, `OLD` (loan row)
- `trg_loan_audit`: reads `loan` row for trigger event

#### Writes
- `fn_checkout_book`: updates `book.copies_free`, inserts into `loan` (loan_id returned)
- `sp_return_book`: updates `loan.returned_on`, updates `book.copies_free`
- `trg_loan_audit_fn`: inserts into `loan_audit` (action CHECKOUT or RETURN)
- `trg_loan_audit`: triggers `trg_loan_audit_fn` on `loan` changes

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` (audit reference)
- `loan.book_id -> book.book_id` (loan to book)
- `loan.member_id -> member.member_id` (loan to member)

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Verify member not blocked (`member.is_blocked`).
  - Decrement `book.copies_free`; error if none.
  - Insert into `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.

- **sp_return_book(p_loan_id)**
  - Update `loan.returned_on` to `CURRENT_DATE` where `returned_on IS NULL`; capture `book_id`.
  - If no row updated, raise exception.
  - Increment `book.copies_free` for returned book.

- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Multiply by 50 cents, cap at 2000 cents.

- **trg_loan_audit_fn()**
  - On INSERT: log `CHECKOUT`.
  - On UPDATE where `returned_on` changes from NULL to NOT NULL: log `RETURN`.
  - Return NEW row.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Trigger: `trg_loan_audit` on `loan` table.
