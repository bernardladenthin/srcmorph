### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:31:46Z
- G: 
- A: 0.0.0
- X: file
---
> Manages library book lending operations including checkout, returns, late fee calculation, and audit logging in PostgreSQL.

#### Purpose
*   Implements core lending workflow for a library system.
*   Tracks loan lifecycle events and calculates overdue penalties.

#### Type
*   PostgreSQL (PL/pgSQL, SQL).
*   Objects: table, function, procedure, trigger.

#### Schema
*   `loan_audit` table: `audit_id BIGSERIAL PRIMARY KEY`, `loan_id BIGINT NOT NULL`, `action VARCHAR(10) NOT NULL CHECK ('CHECKOUT', 'RETURN')`, `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.

#### Reads
*   `member` table (columns `member_id`, `is_blocked`) by `fn_checkout_book`.
*   `book` table (columns `book_id`, `copies_free`) by `fn_checkout_book`, `sp_return_book`.
*   `loan` table (columns `loan_id`, `book_id`, `returned_on`, `due_on`) by `fn_checkout_book`, `sp_return_book`, `trg_loan_audit_fn`.
*   `loan_audit` table (columns `loan_id`, `action`) by `trg_loan_audit_fn`.

#### Writes
*   `loan_audit` table (columns `audit_id`, `loan_id`, `action`, `changed_at`) by `trg_loan_audit_fn`.
*   `book` table (columns `copies_free`) by `fn_checkout_book`, `sp_return_book`.
*   `loan` table (columns `loan_id`, `book_id`, `member_id`, `due_on`, `returned_on`) by `fn_checkout_book`, `sp_return_book`.

#### Relationships
*   `loan_audit.loan_id` -> `loan.loan_id`.
*   `loan.book_id` -> `book.book_id`.
*   `loan.member_id` -> `member.member_id`.

#### Routine logic
*   `fn_checkout_book(p_member_id, p_book_id, p_days)`: Validates `member.is_blocked`, decrements `book.copies_free`, inserts `loan` record, returns `loan_id`.
*   `sp_return_book(p_loan_id)`: Sets `loan.returned_on`, increments `book.copies_free`.
*   `fn_late_fee(p_due_on, p_returned_on)`: Computes max 2000 cents fee based on days overdue (50/day).
*   `trg_loan_audit_fn()`: Inserts 'CHECKOUT' on loan insert, 'RETURN' on loan return update.

#### Dependencies
*   `member` table.
*   `book` table.
*   `loan` table.
*   `loan_audit` table.
