### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:43:45Z
- G: 
- A: 0.0.0
- X: file
---
> Manages library book lending operations including checkout, returns, late fee calculations, and audit logging in PostgreSQL.

#### Purpose
- Implements core lending logic for checking out books, returning them, and calculating late fees.
- Maintains an immutable audit trail of all loan checkouts and returns via triggers.

#### Type
- Tables: `loan_audit` (PostgreSQL)
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn` (PostgreSQL PL/pgSQL/SQL)
- Procedures: `sp_return_book` (PostgreSQL PL/pgSQL)
- Triggers: `trg_loan_audit` (PostgreSQL)

#### Schema
**Table: loan_audit**
- `audit_id` BIGSERIAL PRIMARY KEY
- `loan_id` BIGINT NOT NULL
- `action` VARCHAR(10) NOT NULL CHECK ('CHECKOUT', 'RETURN')
- `changed_at` TIMESTAMPTZ NOT NULL DEFAULT now()

**Table: loan** (Referenced, not defined in file)
- `loan_id` BIGINT (PK)
- `book_id` BIGINT
- `member_id` BIGINT
- `due_on` DATE
- `returned_on` DATE

**Table: member** (Referenced, not defined in file)
- `member_id` BIGINT (PK)
- `is_blocked` BOOLEAN

**Table: book** (Referenced, not defined in file)
- `book_id` BIGINT (PK)
- `copies_free` INTEGER

#### Reads
- `member` (columns: `member_id`, `is_blocked`) in `fn_checkout_book`
- `book` (columns: `book_id`, `copies_free`) in `fn_checkout_book`
- `loan` (columns: `loan_id`, `returned_on`, `book_id`) in `sp_return_book` and `trg_loan_audit_fn`
- `loan` (columns: `loan_id`, `action`) in `trg_loan_audit_fn`
- Inputs: `p_member_id`, `p_book_id`, `p_days`, `p_loan_id`, `p_due_on`, `p_returned_on`

#### Writes
- `book` (column: `copies_free`) in `fn_checkout_book` (decrement)
- `loan` (columns: `book_id`, `member_id`, `due_on`) in `fn_checkout_book` (INSERT)
- `loan` (column: `returned_on`) in `sp_return_book` (UPDATE)
- `book` (column: `copies_free`) in `sp_return_book` (increment)
- `loan_audit` (columns: `loan_id`, `action`) in `trg_loan_audit_fn` (INSERT)

#### Relationships
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id`
- `loan_audit.loan_id` -> `loan.loan_id`

#### Routine logic
**Function: fn_checkout_book**
- Validates member is not blocked; raises exception if blocked.
- Decrements `copies_free` on `book` if available; raises exception if zero.
- Inserts new row into `loan` with calculated `due_on`.
- Returns new `loan_id`.

**Procedure: sp_return_book**
- Updates `loan` to set `returned_on` to current date.
- Retrieves `book_id` from the updated loan.
- Raises exception if loan unknown or already returned.
- Increments `copies_free` on `book`.

**Function: fn_late_fee**
- Calculates days overdue (returned date minus due date, defaulting to current date if not returned).
- Multiplies days by 50 cents.
- Caps result at 2000 cents.

**Trigger: trg_loan_audit_fn**
- On INSERT: Records 'CHECKOUT' action in `loan_audit`.
- On UPDATE: Records 'RETURN' action if `returned_on` changes from NULL to non-NULL.
- Returns NEW row.

#### Dependencies
- `member` table
- `book` table
- `loan` table
- `loan_audit` table
- `CURRENT_DATE`
- `now()`
