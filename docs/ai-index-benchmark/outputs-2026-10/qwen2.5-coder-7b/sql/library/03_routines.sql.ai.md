### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:17:40Z
- G: 
- A: 0.0.0
- X: file
---
> Manages book loans, including checkout, return, late fees, and audit trails.

#### Type
- Table: `loan_audit`
- Function: `fn_checkout_book`, `fn_late_fee`
- Procedure: `sp_return_book`
- Trigger: `trg_loan_audit`

#### Schema
- `loan_audit`:
  - `audit_id BIGSERIAL PRIMARY KEY`
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `fn_checkout_book`:
  - `member` table: `member_id`
  - `book` table: `book_id`, `copies_free`
- `fn_late_fee`:
  - `loan` table: `due_on`, `returned_on`
- `sp_return_book`:
  - `loan` table: `loan_id`, `returned_on`
  - `book` table: `book_id`, `copies_free`

#### Writes
- `fn_checkout_book`:
  - `book` table: `copies_free`
  - `loan` table: `loan_id`, `book_id`, `member_id`, `due_on`
- `fn_late_fee`:
  - No direct writes
- `sp_return_book`:
  - `loan` table: `returned_on`
  - `book` table: `copies_free`

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `fn_checkout_book.member_id -> member.member_id`
- `fn_checkout_book.book_id -> book.book_id`
- `sp_return_book.loan_id -> loan.loan_id`
- `sp_return_book.book_id -> book.book_id`

#### Routine logic
- `fn_checkout_book`:
  - Checks if member is blocked
  - Decreases free copies of the book
  - Inserts a new loan record
  - Returns the new loan ID
- `sp_return_book`:
  - Updates the loan record to mark it as returned
  - Increases free copies of the book
- `fn_late_fee`:
  - Calculates late fee based on days overdue, capped at 2000
- `trg_loan_audit_fn`:
  - Inserts audit record on loan insert or update of returned_on

#### Dependencies
- `member` table
- `book` table
- `loan` table
