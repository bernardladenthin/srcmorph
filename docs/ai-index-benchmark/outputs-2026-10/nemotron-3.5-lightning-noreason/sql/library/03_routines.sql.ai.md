### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:53:27Z
- G: 
- A: 0.0.0
- X: file
---
> This file models a lending library system that handles book checkout, returns, late fee calculation, and an audit trail of loan actions.

#### Purpose
- `fn_checkout_book`: Lends a free copy of a book to a member for a given number of days, validates member status and book availability, creates a loan record, and returns the new loan ID.
- `sp_return_book`: Marks a loan as returned, updates the return date, and increments the book's free copy count; raises an error if the loan is unknown or already returned.
- `fn_late_fee`: Calculates a late fee in cents (50 per day overdue, capped at 2000) based on due and returned dates.
- `trg_loan_audit`: Trigger that writes audit entries to `loan_audit` on loan insert or return date update.

#### Type
PostgreSQL PL/pgSQL functions and trigger.

#### Schema

**loan_audit**
- `audit_id BIGSERIAL PRIMARY KEY`
- `loan_id BIGINT NOT NULL`
- `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
- `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

**book** (referenced, structure inferred from usage)
- `book_id BIGINT` (PK)
- `copies_free INTEGER` (implied by `UPDATE book SET copies_free = copies_free +/- 1`)

**loan** (referenced, structure inferred from usage)
- `loan_id BIGINT` (PK)
- `book_id BIGINT` (FK)
- `member_id BIGINT` (FK)
- `due_on DATE`
- `returned_on DATE` (nullable, checked in trigger and procedure)

#### Reads
- `fn_checkout_book`: reads `member.member_id`, `book.book_id`, `book.copies_free`
- `sp_return_book`: reads `loan.loan_id`, `loan.book_id`, `loan.returned_on`
- `fn_late_fee`: reads `p_due_on`, `p_returned_on` (parameters)
- `trg_loan_audit_fn`: reads `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`

#### Writes
- `fn_checkout_book`: writes to `loan` (`book_id`, `member_id`, `due_on`), writes to `book` (`copies_free` decremented), writes to `loan_audit` (`loan_id`, `action` via trigger)
- `sp_return_book`: writes to `loan` (`returned_on`), writes to `book` (`copies_free` incremented)
- `fn_late_fee`: no table writes (pure function)
- `trg_loan_audit_fn`: writes to `loan_audit` (`loan_id`, `action`)

#### Relationships
- `loan.book_id` → `book.book_id`
- `loan.member_id` → `member.member_id`
- `loan_audit.loan_id` → `loan.loan_id`

#### Routine logic
- `fn_checkout_book(p_member_id, p_book_id, p_days)`: 
  - Checks if member is blocked → raises exception
  - Decrements `book.copies_free` where `copies_free > 0` → raises exception if no copy
  - Inserts into `loan` with `due_on = CURRENT_DATE + p_days`, captures `loan_id` via RETURNING
  - Returns the new `loan_id`
- `sp_return_book(p_loan_id)`: 
  - Updates `loan.returned_on = CURRENT_DATE` where `loan_id` matches and `returned_on IS NULL`
  - If no row updated, raises exception
  - Retrieves `book_id` from updated row, increments `book.copies_free`
- `fn_late_fee(p_due_on, p_returned_on)`: 
  - Computes days overdue as `GREATEST(COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, 0)`
  - Multiplies by 50, caps at 2000 via `LEAST`, returns integer
- `trg_loan_audit_fn()`: 
  - On INSERT: inserts `loan_audit` row with `action = 'CHECKOUT'`
  - On UPDATE of `returned_on` when `NEW.returned_on IS NOT NULL AND OLD.returned_on IS NULL`: inserts `loan_audit` row with `action = 'RETURN'`
  - Returns `NEW` to allow the operation

#### Dependencies
- Tables: `loan`, `book`, `member`, `loan_audit`
- Functions: `fn_checkout_book`, `fn_late_fee`
- Procedures: `sp_return_book`
- Triggers: `trg_loan_audit`
- Sequences: `loan_audit_audit_id_seq` (implied by BIGSERIAL)
