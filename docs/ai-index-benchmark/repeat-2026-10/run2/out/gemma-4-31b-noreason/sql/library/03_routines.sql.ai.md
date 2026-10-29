### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:56:47Z
- G: 
- A: 0.0.0
- X: file
---
> Manages library book loans, returns, late fee calculations, and an audit trail for loan activity.

#### Purpose
* Track loan lifecycle events (checkout/return) via an audit log.
* Automate book inventory updates and loan creation.
* Calculate overdue penalties based on return dates.

#### Type
* Table, Function, Procedure, Trigger (PostgreSQL / PL/pgSQL).

#### Schema
* `loan_audit` table:
    * `audit_id` BIGSERIAL (PK)
    * `loan_id` BIGINT (NOT NULL)
    * `action` VARCHAR(10) (NOT NULL, CHECK: 'CHECKOUT', 'RETURN')
    * `changed_at` TIMESTAMPTZ (NOT NULL, DEFAULT now())

#### Reads
* `member`: `member_id`, `is_blocked` (via `fn_checkout_book`)
* `book`: `book_id`, `copies_free` (via `fn_checkout_book`)
* `loan`: `loan_id`, `returned_on`, `book_id` (via `sp_return_book`, `trg_loan_audit_fn`)

#### Writes
* `loan_audit`: `loan_id`, `action` (via `trg_loan_audit_fn`)
* `book`: `copies_free` (via `fn_checkout_book`, `sp_return_book`)
* `loan`: `book_id`, `member_id`, `due_on`, `returned_on` (via `fn_checkout_book`, `sp_return_book`)

#### Relationships
* `loan_audit.loan_id` -> `loan.loan_id`
* `loan.book_id` -> `book.book_id`
* `loan.member_id` -> `member.member_id`

#### Routine logic
* `fn_checkout_book(p_member_id, p_book_id, p_days)`:
    * Validates `member.is_blocked` is false.
    * Decrements `book.copies_free` if > 0.
    * Inserts record into `loan` with calculated `due_on` date.
    * Returns new `loan_id`.
* `sp_return_book(p_loan_id)`:
    * Updates `loan.returned_on` to current date for active loans.
    * Increments `book.copies_free` for the associated book.
* `fn_late_fee(p_due_on, p_returned_on)`:
    * Calculates days overdue (using `CURRENT_DATE` if not returned).
    * Multiplies days by 50 cents.
    * Caps result at 2000 cents.
* `trg_loan_audit_fn()`:
    * Triggered AFTER INSERT or UPDATE of `returned_on` on `loan`.
    * Inserts 'CHECKOUT' into `loan_audit` on new loan.
    * Inserts 'RETURN' into `loan_audit` when `returned_on` changes from NULL to a value.

#### Dependencies
* `member` table
* `book` table
* `loan` table
