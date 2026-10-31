### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:45:51Z
- G: 
- A: 0.0.0
- X: file
---
> <A lending library system that manages book loans, returns, late fees, and audit trails for members and books.>

#### Purpose
- Handles checkout of books to members with copy tracking.
- Processes book returns and restores free copy counts.
- Calculates late fees in cents based on overdue days.
- Logs checkout and return actions in loan_audit.

#### Type
SQL file containing tables, functions, a procedure, and a trigger (PostgreSQL, PL/pgSQL).

#### Schema
*loan_audit*
- audit_id BIGSERIAL PRIMARY KEY
- loan_id BIGINT NOT NULL
- action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))
- changed_at TIMESTAMPTZ NOT NULL DEFAULT now()

*loan* (referenced but not fully defined in this file; implied columns: loan_id PK, book_id, member_id, due_on, returned_on)
- loan_id PK (assumed from usage)
- book_id BIGINT
- member_id BIGINT
- due_on DATE (used in fn_checkout_book)
- returned_on DATE (nullable, used in sp_return_book and trigger logic)

*book* (referenced but not fully defined in this file; implied columns: book_id PK, copies_free INTEGER)
- book_id PK (assumed)
- copies_free INTEGER NOT NULL (updated in fn_checkout_book and sp_return_book)

#### Reads
- `member` table (column `member_id`, column `is_blocked`) in `fn_checkout_book`
- `book` table (column `copies_free`) in `fn_checkout_book`, `sp_return_book`, `trg_loan_audit_fn`
- `loan` table (columns `loan_id`, `returned_on`) in `sp_return_book`, `trg_loan_audit_fn`
- `loan_audit` table (no reads directly, only writes via trigger)

#### Writes
- `book` table (column `copies_free`) in `fn_checkout_book` (UPDATE), `sp_return_book` (UPDATE)
- `loan` table (INSERT in `fn_checkout_book`, UPDATE in `sp_return_book`)
- `loan_audit` table (INSERT in `trg_loan_audit_fn` triggered by loan changes)

#### Relationships
- `loan` → `book` via `book_id` (used in `fn_checkout_book`, `sp_return_book`, trigger)
- `loan` → `member` via `member_id` (used in `fn_checkout_book`)
- `loan_audit` → `loan` via `loan_id` (primary foreign link)

#### Routine logic
- `fn_checkout_book`:
  - Checks if member is blocked (`member.is_blocked = true`).
  - Decrements `book.copies_free` if >0; raises if no free copy.
  - Inserts a new `loan` row with due date (`CURRENT_DATE + p_days`) and returns `loan_id`.
- `sp_return_book`:
  - Updates `loan.returned_on` to `CURRENT_DATE` for the given `loan_id` where `returned_on IS NULL`.
  - Raises if no such loan row found.
  - Increments `book.copies_free` for the associated book.
- `fn_late_fee`:
  - Immutable SQL function; returns `LEAST(GREATEST(COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, 0) * 50, 2000)` in cents.
  - Caps fee at 2000 cents, calculates per-day overdue amount (50 cents/day).
- `trg_loan_audit_fn`:
  - On INSERT to `loan`, inserts `CHECKOUT` action into `loan_audit`.
  - On UPDATE where `returned_on` becomes non-null (and was null), inserts `RETURN` action into `loan_audit`.
  - Returns `NEW`.
- `trg_loan_audit`:
  - AFTER INSERT OR UPDATE OF `returned_on` on `loan`; executes `trg_loan_audit_fn` per row.

#### Dependencies
- `member` (table, column `member_id`, `is_blocked`)
- `book` (table, column `copies_free`)
- `loan` (table, columns `loan_id`, `returned_on`)
- `loan_audit` (table)
- No other views or functions referenced within this file.
