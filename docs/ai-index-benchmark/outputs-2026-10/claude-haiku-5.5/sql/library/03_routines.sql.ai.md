### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:31:44Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library routines in PostgreSQL that check out and return book copies, compute late fees, and log loan events to an audit table.

#### Purpose
- Checkout and return of library book copies with member block checks and stock counting.
- Late fee calculation (50 cents per overdue day, capped at 2000 cents) and an audit trail of loan events.

#### Type
- Table: `loan_audit` (PostgreSQL)
- Functions: `fn_checkout_book` (returns BIGINT), `fn_late_fee` (IMMUTABLE SQL, returns INTEGER), `trg_loan_audit_fn` (trigger function, PL/pgSQL)
- Procedure: `sp_return_book` (PL/pgSQL)
- Trigger: `trg_loan_audit` (AFTER INSERT OR UPDATE OF returned_on ON `loan`, FOR EACH ROW)

#### Schema
- `loan_audit`
  - `audit_id` BIGSERIAL, PK
  - `loan_id` BIGINT, NOT NULL
  - `action` VARCHAR(10), NOT NULL, CHECK IN ('CHECKOUT', 'RETURN')
  - `changed_at` TIMESTAMPTZ, NOT NULL, DEFAULT now()
- No indexes declared.

#### Reads
- `member.member_id`, `member.is_blocked` (checkout block check)
- `book.book_id`, `book.copies_free` (availability check)
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` (return lookup; `NEW` and `OLD` row values in trigger)
- Function input `p_due_on`, `p_returned_on` (`fn_late_fee`)

#### Writes
- `book.copies_free`: decremented in `fn_checkout_book`, incremented in `sp_return_book`
- `loan`: INSERT (`book_id`, `member_id`, `due_on`) in `fn_checkout_book`; UPDATE `returned_on` in `sp_return_book`
- `loan_audit`: INSERT (`loan_id`, `action`) with 'CHECKOUT' or 'RETURN' from `trg_loan_audit_fn`

#### Relationships
- `loan.book_id -> book.book_id` (implied by usage, no FK declared)
- `loan.member_id -> member.member_id` (implied by usage, no FK declared)
- `loan_audit.loan_id -> loan.loan_id` (implied by usage, no FK declared)

#### Routine logic
- `fn_checkout_book(p_member_id, p_book_id, p_days DEFAULT 21)`
  - Raises exception if member is blocked.
  - Decrements `book.copies_free` where copies_free > 0; raises exception if none free.
  - Inserts `loan` with `due_on` = CURRENT_DATE + p_days; returns new `loan_id`.
- `sp_return_book(p_loan_id)`
  - Sets `loan.returned_on` = CURRENT_DATE where not yet returned; returns `book_id`.
  - Raises exception if loan unknown or already returned.
  - Increments `book.copies_free` for that book.
- `fn_late_fee(p_due_on, p_returned_on)`
  - Days = COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, floored at 0.
  - Fee = days × 50, capped at 2000 via LEAST/GREATEST.
- `trg_loan_audit_fn()`
  - On INSERT: logs 'CHECKOUT' to `loan_audit`.
  - On UPDATE where `returned_on` changes from NULL to not NULL: logs 'RETURN'.
  - Returns NEW.
- `trg_loan_audit` fires `trg_loan_audit_fn` on `loan`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Functions: `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`
- Procedure: `sp_return_book`
- Trigger: `trg_loan_audit`
- Sequence: implicit from `loan_audit.audit_id` BIGSERIAL
- Built-ins: `now()`, `CURRENT_DATE`, `LEAST`, `GREATEST`, `COALESCE`
