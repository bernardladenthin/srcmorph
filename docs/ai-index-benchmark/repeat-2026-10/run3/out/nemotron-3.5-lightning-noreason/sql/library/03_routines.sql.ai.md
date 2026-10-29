### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:40:56Z
- G: 
- A: 0.0.0
- X: file
---
> This file models a lending library system that handles book checkout, returns, late fee calculation, and an audit trail of loan actions.

#### Purpose
- `loan_audit` table: records every checkout and return action for loans.
- `fn_checkout_book`: lends a free copy of a book to a member, validates member status and book availability, creates a new loan, and returns the new loan ID.
- `sp_return_book`: marks a loan as returned, validates the loan state, and increments the book's free copy count.
- `fn_late_fee`: calculates the late fee in cents (50 per day overdue, capped at 2000).
- `trg_loan_audit`: trigger that writes audit entries on loan insert or when `returned_on` is updated.

#### Type
PostgreSQL PL/pgSQL routines (function, procedure, trigger).

#### Schema
**loan_audit**
- `audit_id` BIGSERIAL PRIMARY KEY
- `loan_id` BIGINT NOT NULL
- `action` VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
- `changed_at` TIMESTAMPTZ NOT NULL DEFAULT now()
- Index: `audit_id` primary key

**book** (referenced, structure inferred from usage)
- `book_id` BIGINT (PK)
- `copies_free` INTEGER

**loan** (referenced, structure inferred from usage)
- `loan_id` BIGINT (PK)
- `book_id` BIGINT FK
- `member_id` BIGINT FK
- `due_on` DATE
- `returned_on` DATE (nullable)

#### Reads
- `fn_checkout_book`: reads `member` (column `member_id`, `is_blocked`), reads `book` (column `book_id`, `copies_free`).
- `sp_return_book`: reads `loan` (columns `loan_id`, `book_id`, `returned_on`).
- `fn_late_fee`: reads parameters `p_due_on`, `p_returned_on`.
- `trg_loan_audit_fn`: reads `loan` columns `loan_id`, `returned_on` (NEW, OLD).

#### Writes
- `loan_audit`: inserted by trigger on `loan` INSERT or UPDATE of `returned_on`.
- `fn_checkout_book`: writes to `loan` (INSERT with `book_id`, `member_id`, `due_on`), writes to `book` (UPDATE `copies_free` decrement).
- `sp_return_book`: writes to `loan` (UPDATE `returned_on`), writes to `book` (UPDATE `copies_free` increment).
- `fn_late_fee`: writes result (INTEGER) as return value.

#### Relationships
- `loan.book_id` → `book.book_id` (FK implied by UPDATE/RETURN logic)
- `loan.member_id` → `member.member_id` (FK implied by checkout validation)
- `loan_audit.loan_id` → `loan.loan_id`

#### Routine logic
- `fn_checkout_book(p_member_id, p_book_id, p_days)`: 
  - Checks if member is blocked → raises exception.
  - Decrements `book.copies_free` where `copies_free > 0` → raises exception if no copy.
  - Inserts into `loan` with `due_on = CURRENT_DATE + p_days`, captures `loan_id` via RETURNING.
  - Returns the new `loan_id`.
- `sp_return_book(p_loan_id)`: 
  - Updates `loan.returned_on = CURRENT_DATE` where `loan_id` and `returned_on IS NULL`.
  - If no row updated, raises exception.
  - Increments `book.copies_free` by 1 for the returned `book_id`.
- `fn_late_fee(p_due_on, p_returned_on)`: 
  - Returns `LEAST(GREATEST(COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, 0) * 50, 2000)`.
- `trg_loan_audit_fn()`: 
  - On INSERT: inserts audit row with action 'CHECKOUT'.
  - On UPDATE of `returned_on` when `NEW.returned_on IS NOT NULL AND OLD.returned_on IS NULL`: inserts audit row with action 'RETURN'.
  - Always returns NEW.

#### Dependencies
- Tables: `loan`, `book`, `member`, `loan_audit`
- Functions: `fn_checkout_book`, `fn_late_fee`
- Procedures: `sp_return_book`
- Triggers: `trg_loan_audit`
- Sequences: `loan_audit_audit_id_seq` (implied by BIGSERIAL)
