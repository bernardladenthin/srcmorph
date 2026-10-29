### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:57:00Z
- G: 
- A: 0.0.0
- X: file
---
> Manages library book lending operations including checkout, return, late fee calculation, and audit logging in PostgreSQL.

#### Purpose
- Implements core lending logic (checkout/return) and audit trails for a library system.
- Calculates late fees based on overdue days with a maximum cap.

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
- Returns: BIGINT (new loan_id)

**Procedure: sp_return_book**
- Inputs: `p_loan_id` BIGINT
- Returns: None (void)

**Function: fn_late_fee**
- Inputs: `p_due_on` DATE, `p_returned_on` DATE
- Returns: INTEGER (fee in cents)

#### Reads
- `member` (columns: `member_id`, `is_blocked`)
- `book` (columns: `book_id`, `copies_free`)
- `loan` (columns: `loan_id`, `returned_on`, `book_id`)
- `loan_audit` (columns: `loan_id`, `action`)

#### Writes
- `book` (column: `copies_free`)
- `loan` (columns: `book_id`, `member_id`, `due_on`, `returned_on`)
- `loan_audit` (columns: `loan_id`, `action`)

#### Relationships
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id`
- `loan_audit.loan_id` -> `loan.loan_id`

#### Routine logic
- **fn_checkout_book**:
  - Validates member is not blocked.
  - Decrements `copies_free` in `book` if available.
  - Inserts new row into `loan` with calculated `due_on`.
  - Returns generated `loan_id`.
- **sp_return_book**:
  - Updates `loan` to set `returned_on` and retrieves `book_id`.
  - Increments `copies_free` in `book` for the returned item.
- **fn_late_fee**:
  - Calculates days overdue (0 if not overdue).
  - Multiplies by 50 cents.
  - Caps result at 2000 cents.
- **trg_loan_audit_fn**:
  - On INSERT: Logs 'CHECKOUT' action.
  - On UPDATE (returned_on): Logs 'RETURN' action if transitioning from NULL to non-NULL.

#### Dependencies
- `member`
- `book`
- `loan`
- `loan_audit`
