### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T05:05:41Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library: checkout, return, late fee and audit trail (PostgreSQL, PL/pgSQL).

#### Purpose
- Models the core loan operations (checkout, return) and tracks them with an audit trail.
- Calculates late fees based on due and return dates.

#### Type
- Table: `loan_audit`
- Functions: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`
- Trigger: `trg_loan_audit`

#### Schema
**loan_audit**
- `audit_id` BIGINT PRIMARY KEY
- `loan_id` BIGINT NOT NULL
- `action` VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT','RETURN'))
- `changed_at` TIMESTAMPTZ NOT NULL DEFAULT now()

**fn_checkout_book**
- Parameters: `p_member_id BIGINT`, `p_book_id BIGINT`, `p_days INTEGER DEFAULT 21`
- Returns: `BIGINT` (new loan id)

**sp_return_book**
- Parameter: `p_loan_id BIGINT`
- Returns: void

**fn_late_fee**
- Parameters: `p_due_on DATE`, `p_returned_on DATE`
- Returns: `INTEGER` (late fee in cents)

**trg_loan_audit_fn**
- Returns: `TRIGGER`
- Logic: inserts audit rows on INSERT or RETURN update of `loan`

#### Reads
- `fn_checkout_book`: `member`, `book`
- `sp_return_book`: `loan`, `book`
- `fn_late_fee`: none
- `trg_loan_audit_fn`: `loan` (NEW/OLD)

#### Writes
- `fn_checkout_book`: `book` (copies_free), `loan`
- `sp_return_book`: `loan` (returned_on), `book` (copies_free)
- `trg_loan_audit_fn`: `loan_audit`

#### Relationships
- `loan_audit.loan_id -> loan.loan_id`
- `fn_checkout_book` uses `member.member_id`, `book.book_id`
- `sp_return_book` uses `loan.loan_id`, `book.book_id`

#### Routine logic
**fn_checkout_book**
- Checks member block status
- Decrements `book.copies_free`
- Inserts new row into `loan`
- Returns new `loan_id`

**sp_return_book**
- Marks `loan.returned_on`
- Increments `book.copies_free`
- Raises error if loan unknown or already returned

**fn_late_fee**
- Computes days overdue
- Applies 50 cents per day, capped at 2000 cents

**trg_loan_audit_fn**
- On INSERT: logs `CHECKOUT`
- On UPDATE of `returned_on`: logs `RETURN`

#### Dependencies
- `member`, `book`, `loan` tables
- `loan_audit` table
- `CURRENT_DATE`, `TIMESTAMPTZ` functions
