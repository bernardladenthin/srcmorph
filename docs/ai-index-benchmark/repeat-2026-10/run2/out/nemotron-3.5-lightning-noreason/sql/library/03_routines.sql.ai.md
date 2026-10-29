### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:05Z
- G: 
- A: 0.0.0
- X: file
---
> This file models a lending library system that manages book checkouts, returns, late fee calculations, and an audit trail of loan actions.

#### Purpose
- `loan_audit` table: tracks every checkout and return action for loans.
- `fn_checkout_book`: lends a free copy of a book to a member, validates member status and book availability, creates a new loan, and returns the generated loan ID.
- `sp_return_book`: marks a specific loan as returned, updates the book's free copy count, and raises an error if the loan is unknown or already returned.
- `fn_late_fee`: calculates the late fee in cents (50 per day overdue, capped at 2000) based on due and returned dates.
- `trg_loan_audit`: trigger that automatically inserts audit records when a loan is inserted or when the `returned_on` column is updated.

#### Type
PostgreSQL PL/pgSQL routines (function, procedure, trigger).

#### Schema
**loan_audit**
- `audit_id` BIGSERIAL PRIMARY KEY
- `loan_id` BIGINT NOT NULL
- `action` VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
- `changed_at` TIMESTAMPTZ NOT NULL DEFAULT now()

**book** (referenced, structure inferred from usage)
- `book_id` BIGINT (PK)
- `copies_free` INTEGER

**loan** (referenced, structure inferred from usage)
- `loan_id` BIGINT (PK)
- `book_id` BIGINT
- `member_id` BIGINT
- `due_on` DATE
- `returned_on` DATE

#### Reads
- `fn_checkout_book`: reads `member` (column `member_id`, `is_blocked`), `book` (column `book_id`, `copies_free`).
- `sp_return_book`: reads `loan` (columns `loan_id`, `book_id`, `returned_on`).
- `fn_late_fee`: reads parameters `p_due_on`, `p_returned_on`.
- `trg_loan_audit_fn`: reads `loan` table columns `loan_id`, `returned_on` (NEW, OLD).

#### Writes
- `loan_audit`: INSERT via trigger `trg_loan_audit` (columns `loan_id`, `action`).
- `fn_checkout_book`: UPDATE `book` (column `copies_free`--decrement), INSERT `loan` (columns `book_id`, `member_id`, `due_on`), RETURN `loan_id`.
- `sp_return_book`: UPDATE `loan` (column `returned_on`), UPDATE `book` (column `copies_free`--increment).
- `fn_late_fee`: no table writes (immutable SQL function).

#### Relationships
- `loan.book_id` → `book.book_id`
- `loan.member_id` → `member.member_id`
- `loan_audit.loan_id` → `loan.loan_id`
- Trigger `trg_loan_audit` fires on `loan` INSERT or UPDATE of `returned_on`.

#### Routine logic
- `fn_checkout_book(p_member_id, p_book_id, p_days)`: 
  - Raise exception if member is blocked.
  - Decrement `book.copies_free` where `book_id = p_book_id` and `copies_free > 0`; raise exception if no copy found.
  - INSERT into `loan` with `book_id`, `member_id`, `due_on = CURRENT_DATE + p_days`; capture `loan_id` via RETURNING.
  - Return `v_loan_id`.
- `sp_return_book(p_loan_id)`: 
  - UPDATE `loan` set `returned_on = CURRENT_DATE` where `loan_id = p_loan_id` and `returned_on IS NULL`; capture `book_id` via RETURNING.
  - Raise exception if no row updated.
  - Increment `book.copies_free` where `book_id = v_book_id`.
- `fn_late_fee(p_due_on, p_returned_on)`: 
  - Return `LEAST(GREATEST(COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, 0) * 50, 2000)`.
- `trg_loan_audit_fn()`: 
  - On INSERT: INSERT into `loan_audit` (`loan_id`, `action`) values (`NEW.loan_id`, 'CHECKOUT').
  - On UPDATE of `returned_on` where `NEW.returned_on IS NOT NULL` and `OLD.returned_on IS NULL`: INSERT into `loan_audit` (`loan_id`, `action`) values (`NEW.loan_id`, 'RETURN').
  - RETURN NEW.

#### Dependencies
- Tables: `loan`, `book`, `member`, `loan_audit`.
- Functions: `fn_checkout_book`, `fn_late_fee`.
- Procedures: `sp_return_book`.
- Triggers: `trg_loan_audit`.
- Sequences: `loan_audit_audit_id_seq` (implied by BIGSERIAL).
