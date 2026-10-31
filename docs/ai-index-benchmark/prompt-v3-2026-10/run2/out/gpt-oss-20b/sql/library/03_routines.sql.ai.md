### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:19:12Z
- G: 
- A: 0.0.0
- X: file
---
> A PostgreSQL library system that manages book loans, returns, late fees, and audit trails.

#### Purpose
- Implements checkout, return, and late fee logic for a lending library.
- Records audit events for loan creation and return.

#### Type
- Table: `loan_audit`
- Function: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Procedure: `sp_return_book`
- Trigger: `trg_loan_audit`

#### Schema
- **loan_audit**
  - `audit_id BIGSERIAL PRIMARY KEY`
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member` (`member_id`, `is_blocked`)
- `book` (`book_id`, `copies_free`)
- `loan` (`loan_id`, `book_id`, `returned_on`, `due_on`)

#### Writes
- `book` (`copies_free` decremented by 1 in `fn_checkout_book`; incremented by 1 in `sp_return_book`)
- `loan` (INSERT new loan in `fn_checkout_book`; UPDATE `returned_on` in `sp_return_book`)
- `loan_audit` (INSERT on checkout and return in trigger `trg_loan_audit_fn`)

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- **fn_checkout_book(p_member_id, p_book_id, p_days)**
  - Check member not blocked.
  - Decrement `book.copies_free`; error if none.
  - INSERT into `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`.
- **sp_return_book(p_loan_id)**
  - UPDATE `loan` set `returned_on = CURRENT_DATE`; return `book_id`.
  - Increment `book.copies_free`.
- **fn_late_fee(p_due_on, p_returned_on)**
  - Compute days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Multiply by 50, cap at 2000.
- **trg_loan_audit_fn()**
  - On INSERT: log `CHECKOUT`.
  - On UPDATE where `returned_on` changes from NULL to NOT NULL: log `RETURN`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Trigger: `trg_loan_audit` on `loan` table.
