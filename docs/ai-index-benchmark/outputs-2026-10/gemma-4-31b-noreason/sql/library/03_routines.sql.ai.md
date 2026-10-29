### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:53:15Z
- G: 
- A: 0.0.0
- X: file
---
> Manages library book loan lifecycles, including checkouts, returns, late fee calculations, and an audit trail of loan events.

#### Purpose
* Track loan history and state changes via an audit log.
* Automate book inventory updates during checkout and return processes.
* Calculate financial penalties for overdue books.

#### Type
* Table (PostgreSQL)
* Function (PostgreSQL, PL/pgSQL, SQL)
* Procedure (PostgreSQL, PL/pgSQL)
* Trigger (PostgreSQL)

#### Schema
* `loan_audit`
    * `audit_id` BIGSERIAL (PK)
    * `loan_id` BIGINT (NOT NULL)
    * `action` VARCHAR(10) (NOT NULL, CHECK: 'CHECKOUT', 'RETURN')
    * `changed_at` TIMESTAMPTZ (NOT NULL, DEFAULT now())

#### Reads
* `member`: `member_id`, `is_blocked`
* `book`: `book_id`, `copies_free`
* `loan`: `loan_id`, `returned_on`, `book_id`

#### Writes
* `loan_audit`: `loan_id`, `action`
* `book`: `copies_free`
* `loan`: `book_id`, `member_id`, `due_on`, `returned_on`

#### Relationships
* `loan_audit.loan_id` -> `loan.loan_id`
* `loan.book_id` -> `book.book_id`
* `loan.member_id` -> `member.member_id`

#### Routine logic
* `fn_checkout_book(p_member_id, p_book_id, p_days)`
    * Validates `member.is_blocked` is false.
    * Decrements `book.copies_free` if > 0.
    * Inserts record into `loan` with calculated `due_on` date.
    * Returns new `loan_id`.
* `sp_return_book(p_loan_id)`
    * Updates `loan.returned_on` to current date if previously NULL.
    * Increments `book.copies_free` for the associated book.
* `fn_late_fee(p_due_on, p_returned_on)`
    * Calculates days overdue (using `CURRENT_DATE` if not returned).
    * Multiplies days by 50 cents.
    * Caps result at 2000 cents.
* `trg_loan_audit_fn()`
    * On `INSERT` to `loan`: logs 'CHECKOUT' to `loan_audit`.
    * On `UPDATE` of `loan.returned_on` (from NULL to value): logs 'RETURN' to `loan_audit`.

#### Dependencies
* `member` (table)
* `book` (table)
* `loan` (table)
