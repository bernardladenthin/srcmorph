### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:58:30Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library operations: book checkout, return processing, late fee calculation, and loan audit trail.

#### Purpose
- Manages the lifecycle of book loans (checkout, return) and enforces inventory constraints.
- Calculates late fees and maintains an immutable audit log of loan state changes.

#### Type
- **Table**: `loan_audit` (PostgreSQL)
- **Function**: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn` (PL/pgSQL / SQL)
- **Procedure**: `sp_return_book` (PL/pgSQL)
- **Trigger**: `trg_loan_audit` (PostgreSQL)

#### Schema
**Table: `loan_audit`**
- `audit_id BIGSERIAL` - PRIMARY KEY
- `loan_id BIGINT` - NOT NULL
- `action VARCHAR(10)` - NOT NULL, CHECK (`action IN ('CHECKOUT', 'RETURN')`)
- `changed_at TIMESTAMPTZ` - NOT NULL, DEFAULT `now()`

#### Reads
- `fn_checkout_book`:
    - `member` (`member_id`, `is_blocked`)
    - `book` (`book_id`, `copies_free`)
- `sp_return_book`:
    - `loan` (`loan_id`, `returned_on`, `book_id`)
- `trg_loan_audit_fn`:
    - `loan` (via `OLD` and `NEW` row versions)

#### Writes
- `fn_checkout_book`:
    - `book` (`copies_free` - decrement)
    - `loan` (`book_id`, `member_id`, `due_on` - insert)
- `sp_return_book`:
    - `loan` (`returned_on` - update)
    - `book` (`copies_free` - increment)
- `trg_loan_audit_fn`:
    - `loan_audit` (`loan_id`, `action` - insert)

#### Relationships
- `loan_audit.loan_id` -> `loan.loan_id` (logical reference, no explicit FK defined in this file)
- `loan.book_id` -> `book.book_id` (referenced in `fn_checkout_book` and `sp_return_book`)
- `loan.member_id` -> `member.member_id` (referenced in `fn_checkout_book`)

#### Routine logic
- **`fn_checkout_book(p_member_id, p_book_id, p_days)`**:
    - Validates member is not blocked (`member.is_blocked`).
    - Decrements `book.copies_free` if available; raises exception if no free copies.
    - Inserts new record into `loan` with `due_on` = `CURRENT_DATE + p_days`.
    - Returns new `loan_id`.
- **`sp_return_book(p_loan_id)`**:
    - Updates `loan.returned_on` to `CURRENT_DATE` if not already returned.
    - Raises exception if loan ID is invalid or already returned.
    - Increments `book.copies_free` for the associated book.
- **`fn_late_fee(p_due_on, p_returned_on)`**:
    - Calculates days overdue: `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`.
    - Applies 50 cents/day rate.
    - Caps total fee at 2000 cents.
    - Returns integer fee amount.
- **`trg_loan_audit_fn()`**:
    - On `INSERT` into `loan`: logs `'CHECKOUT'` action to `loan_audit`.
    - On `UPDATE` of `loan.returned_on` (from NULL to non-NULL): logs `'RETURN'` action to `loan_audit`.
    - Returns `NEW` row.

#### Dependencies
- **Tables**: `member`, `book`, `loan`, `loan_audit`
- **Functions**: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- **Procedures**: `sp_return_book`
- **Triggers**: `trg_loan_audit`
