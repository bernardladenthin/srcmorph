### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:08:13Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library domain: manages book checkouts, returns, late fee calculation, and audit logging for loan events.

#### Purpose
- Implements core lending operations: `fn_checkout_book` (lends a copy), `sp_return_book` (returns a copy), `fn_late_fee` (calculates overdue penalty), and `trg_loan_audit` (logs loan state changes).
- Maintains inventory counts in `book` and transaction history in `loan` and `loan_audit`.

#### Type
- PostgreSQL (PL/pgSQL)
- Objects: `table` (`loan_audit`), `function` (`fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`), `procedure` (`sp_return_book`), `trigger` (`trg_loan_audit`).

#### Schema
**Table: `loan_audit`**
- `audit_id BIGSERIAL` - PRIMARY KEY
- `loan_id BIGINT` - NOT NULL
- `action VARCHAR(10)` - NOT NULL, CHECK (`action IN ('CHECKOUT', 'RETURN')`)
- `changed_at TIMESTAMPTZ` - NOT NULL, DEFAULT `now()`

#### Reads
- `fn_checkout_book`:
  - `member` table: checks `member_id`, `is_blocked`
  - `book` table: checks `book_id`, `copies_free`
- `sp_return_book`:
  - `loan` table: checks `loan_id`, `returned_on`
- `trg_loan_audit_fn`:
  - `loan` table: accesses `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`

#### Writes
- `fn_checkout_book`:
  - `book`: updates `copies_free` (decrement)
  - `loan`: inserts `book_id`, `member_id`, `due_on`
- `sp_return_book`:
  - `loan`: updates `returned_on`
  - `book`: updates `copies_free` (increment)
- `trg_loan_audit_fn`:
  - `loan_audit`: inserts `loan_id`, `action`

#### Relationships
- `loan_audit.loan_id` -> `loan.loan_id` (implicit logical link via trigger)
- `loan.book_id` -> `book.book_id` (implicit logical link via function/procedure)
- `loan.member_id` -> `member.member_id` (implicit logical link via function)

#### Routine logic
- **`fn_checkout_book(p_member_id, p_book_id, p_days)`**:
  - Validates member is not blocked (`member.is_blocked`).
  - Decrements `book.copies_free` if available; raises exception if no free copy.
  - Inserts into `loan` with `due_on` = `CURRENT_DATE + p_days`.
  - Returns new `loan_id`.
- **`sp_return_book(p_loan_id)`**:
  - Updates `loan.returned_on` to `CURRENT_DATE` if not already returned.
  - Increments `book.copies_free` for the associated `book_id`.
  - Raises exception if loan is unknown or already returned.
- **`fn_late_fee(p_due_on, p_returned_on)`**:
  - Calculates days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
  - Applies 50 cents/day rate, capped at 2000 cents.
  - Returns integer fee.
- **`trg_loan_audit_fn()`**:
  - On `INSERT` into `loan`: logs `'CHECKOUT'` to `loan_audit`.
  - On `UPDATE` of `loan.returned_on` (from NULL to non-NULL): logs `'RETURN'` to `loan_audit`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `trg_loan_audit_fn` (used by trigger `trg_loan_audit`)
