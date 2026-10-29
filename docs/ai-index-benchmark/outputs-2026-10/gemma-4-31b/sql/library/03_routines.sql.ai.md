### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:34:14Z
- G: 
- A: 0.0.0
- X: file
---
> Manages the lifecycle of book loans, including checkouts, returns, late fee calculations, and auditing.

#### Purpose
* Implement business logic for borrowing and returning books.
* Maintain an audit trail of loan status changes.
* Calculate overdue penalties.

#### Type
* Table, Function, Procedure, Trigger (PostgreSQL).

#### Schema
* `loan_audit` table:
    * `audit_id BIGSERIAL` PK
    * `loan_id BIGINT` NOT NULL
    * `action VARCHAR(10)` NOT NULL CHECK (`action` IN ('CHECKOUT', 'RETURN'))
    * `changed_at TIMESTAMPTZ` NOT NULL DEFAULT now()

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
* `fn_checkout_book(p_member_id, p_book_id, p_days)`:
    * Validates `member.is_blocked` is false.
    * Decrements `book.copies_free` if > 0.
    * Inserts new `loan` record with calculated `due_on`.
    * Returns new `loan_id`.
* `sp_return_book(p_loan_id)`:
    * Updates `loan.returned_on` to current date for active loans.
    * Increments `book.copies_free` for the associated book.
* `fn_late_fee(p_due_on, p_returned_on)`:
    * Calculates days overdue (using `CURRENT_DATE` if `p_returned_on` is null).
    * Multiplies days by 50 cents, capped at 2000 cents.
* `trg_loan_audit_fn()`:
    * On `INSERT` to `loan`: inserts 'CHECKOUT' into `loan_audit`.
    * On `UPDATE` of `loan.returned_on` (from NULL to NOT NULL): inserts 'RETURN' into `loan_audit`.

#### Dependencies
* `member` table
* `book` table
* `loan` table
* `loan_audit` table
