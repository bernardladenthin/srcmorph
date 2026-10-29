### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:04:45Z
- G: 
- A: 0.0.0
- X: file
---
> Manages library book lending operations including checkout, return, late fee calculation, and audit logging in PostgreSQL.

#### Purpose
- Implements core lending logic for checking out books to members and processing returns.
- Maintains an immutable audit trail of all loan state changes (checkout/return) via triggers.

#### Type
- `loan_audit`: Table (PostgreSQL)
- `fn_checkout_book`: Function (PostgreSQL, PL/pgSQL)
- `sp_return_book`: Procedure (PostgreSQL, PL/pgSQL)
- `fn_late_fee`: Function (PostgreSQL, SQL)
- `trg_loan_audit_fn`: Function (PostgreSQL, PL/pgSQL)
- `trg_loan_audit`: Trigger (PostgreSQL)

#### Schema
**Table: loan_audit**
- `audit_id` BIGSERIAL PRIMARY KEY
- `loan_id` BIGINT NOT NULL
- `action` VARCHAR(10) NOT NULL CHECK ('CHECKOUT', 'RETURN')
- `changed_at` TIMESTAMPTZ NOT NULL DEFAULT now()

**Function: fn_checkout_book**
- Inputs: `p_member_id` BIGINT, `p_book_id` BIGINT, `p_days` INTEGER (default 21)
- Returns: `BIGINT` (new `loan_id`)

**Procedure: sp_return_book**
- Inputs: `p_loan_id` BIGINT
- Returns: None (void)

**Function: fn_late_fee**
- Inputs: `p_due_on` DATE, `p_returned_on` DATE
- Returns: `INTEGER` (fee in cents)

#### Reads
- `member` table: `member_id`, `is_blocked` (in `fn_checkout_book`)
- `book` table: `copies_free`, `book_id` (in `fn_checkout_book`, `sp_return_book`)
- `loan` table: `loan_id`, `returned_on`, `book_id` (in `sp_return_book`, `trg_loan_audit_fn`)
- `loan_audit` table: None (only written to)

#### Writes
- `book` table: `copies_free` (decrement in `fn_checkout_book`, increment in `sp_return_book`)
- `loan` table: `book_id`, `member_id`, `due_on` (INSERT in `fn_checkout_book`); `returned_on` (UPDATE in `sp_return_book`)
- `loan_audit` table: `loan_id`, `action` (INSERT in `trg_loan_audit_fn`)

#### Relationships
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id`
- `loan_audit.loan_id` -> `loan.loan_id`

#### Routine logic
- **fn_checkout_book**:
  - Validates member is not blocked in `member` table.
  - Decrements `copies_free` in `book` table if available.
  - Inserts new row into `loan` table with calculated `due_on`.
  - Returns generated `loan_id`.
- **sp_return_book**:
  - Updates `loan` row to set `returned_on` to current date.
  - Increments `copies_free` in `book` table for the returned book.
  - Raises exception if loan is unknown or already returned.
- **fn_late_fee**:
  - Calculates days overdue (0 if not overdue).
  - Multiplies days by 50 cents.
  - Caps result at 2000 cents.
- **trg_loan_audit_fn**:
  - On INSERT to `loan`: records 'CHECKOUT' action in `loan_audit`.
  - On UPDATE to `loan` where `returned_on` changes from NULL to value: records 'RETURN' action in `loan_audit`.

#### Dependencies
- `member` table
- `book` table
- `loan` table
- `loan_audit` table
- `CURRENT_DATE`
- `now()`
