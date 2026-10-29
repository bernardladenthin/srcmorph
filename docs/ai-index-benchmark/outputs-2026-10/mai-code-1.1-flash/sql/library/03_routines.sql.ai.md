### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:46:09Z
- G: 
- A: 0.0.0
- X: file
---
> This file models the library lending workflow for checkout, return, late-fee calculation, and loan audit logging.

#### Purpose
- Tracks loan lifecycle events for library books and member borrowing.
- Provides PostgreSQL routines to validate checkout/return, update available copies, calculate overdue charges, and audit loan actions.

#### Type
- Dialect: PostgreSQL / PL/pgSQL.
- Objects in file:
  - Table: `loan_audit`
  - Function: `fn_checkout_book`
  - Procedure: `sp_return_book`
  - Function: `fn_late_fee`
  - Function: `trg_loan_audit_fn`
  - Trigger: `trg_loan_audit`

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` PK
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`
  - PK: `loan_audit.audit_id`
  - Indexes: none declared

#### Reads
- `fn_checkout_book`
  - Reads `member.member_id` and `member.is_blocked` in `IF EXISTS (SELECT 1 FROM member WHERE member_id = p_member_id AND is_blocked)`.
  - Reads `book.book_id` and `book.copies_free` in `UPDATE book SET copies_free = copies_free - 1 WHERE book_id = p_book_id AND copies_free > 0`.
  - Reads function inputs `p_member_id`, `p_book_id`, `p_days`.
- `sp_return_book`
  - Reads `loan.loan_id`, `loan.returned_on`, and `loan.book_id` in `UPDATE loan SET returned_on = CURRENT_DATE WHERE loan_id = p_loan_id AND returned_on IS NULL RETURNING book_id INTO v_book_id`.
  - Reads `book.book_id` and `book.copies_free` in `UPDATE book SET copies_free = copies_free + 1 WHERE book_id = v_book_id`.
  - Reads function input `p_loan_id`.
- `fn_late_fee`
  - Reads function inputs `p_due_on`, `p_returned_on`.
  - Uses `CURRENT_DATE` as fallback for returned date.
- `trg_loan_audit_fn`
  - Reads trigger state `TG_OP`, `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`.
- `trg_loan_audit`
  - Fires on `loan` after `INSERT` or `UPDATE OF returned_on`.

#### Writes
- `fn_checkout_book`
  - Updates `book.copies_free` by decrement: `UPDATE book SET copies_free = copies_free - 1 WHERE book_id = p_book_id AND copies_free > 0`.
  - Inserts into `loan` (`loan.book_id`, `loan.member_id`, `loan.due_on`) with `INSERT INTO loan (book_id, member_id, due_on) VALUES (...)`.
- `sp_return_book`
  - Updates `loan.returned_on` with `UPDATE loan SET returned_on = CURRENT_DATE WHERE loan_id = p_loan_id AND returned_on IS NULL`.
  - Updates `book.copies_free` by increment: `UPDATE book SET copies_free = copies_free + 1 WHERE book_id = v_book_id`.
- `trg_loan_audit_fn`
  - Inserts into `loan_audit (loan_id, action)` with `CHECKOUT` and `RETURN` actions.
- Trigger target:
  - `loan` receives `AFTER INSERT OR UPDATE OF returned_on` events.

#### Relationships
- No explicit `FOREIGN KEY` constraints are declared in this file.
- Code-based relationships:
  - `loan.loan_id -> loan_audit.loan_id` in `trg_loan_audit_fn`: `INSERT INTO loan_audit (loan_id, action) VALUES (NEW.loan_id, ...)`.
  - `book.book_id -> loan.book_id` in `fn_checkout_book`: `INSERT INTO loan (book_id, member_id, due_on) VALUES (p_book_id, p_member_id, ...)`.
  - `loan.book_id -> book.book_id` in `sp_return_book`: `UPDATE book SET copies_free = copies_free + 1 WHERE book_id = v_book_id` after `RETURNING book_id INTO v_book_id`.
  - `member.member_id -> member.member_id` check condition in `fn_checkout_book`: `WHERE member_id = p_member_id AND is_blocked`.
  - `loan.loan_id -> loan.loan_id` check condition in `sp_return_book`: `WHERE loan_id = p_loan_id AND returned_on IS NULL`.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises exception if `member` row exists with `member_id = p_member_id` and `is_blocked = true`.
  - Decrements `book.copies_free` for `book_id = p_book_id` when a free copy exists; raises exception otherwise.
  - Inserts into `loan` with `book_id = p_book_id`, `member_id = p_member_id`, and `due_on = CURRENT_DATE + p_days`.
  - Returns the inserted `loan.loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Updates `loan.returned_on = CURRENT_DATE` for matching `loan_id` where `returned_on IS NULL`, returning `book_id`.
  - Raises exception if no matching active loan is found.
  - Increments `book.copies_free` for the returned book.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Computes overdue days as `COALESCE(p_returned_on, CURRENT_DATE) - p_due_on`, clamps at zero, multiplies by 50, caps at 2000.
  - Returns amount in cents.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - On `INSERT`, inserts `loan_audit (loan_id, action) = (NEW.loan_id, 'CHECKOUT')`.
  - On `UPDATE`, when `NEW.returned_on IS NOT NULL` and `OLD.returned_on IS NULL`, inserts `loan_audit (loan_id, action) = (NEW.loan_id, 'RETURN')`.
  - Returns `NEW`.
- `trg_loan_audit`
  - `AFTER INSERT OR UPDATE OF returned_on ON loan FOR EACH ROW`
  - Executes `trg_loan_audit_fn()`.

#### Dependencies
- Tables referenced:
  - `member`
  - `book`
  - `loan`
  - `loan_audit`
- Sequence behavior:
  - `loan_audit.audit_id` uses `BIGSERIAL`; implicit PostgreSQL sequence generation is in effect, but no explicit sequence name is declared in this file.
- Trigger context referenced:
  - `TG_OP`, `NEW`, `OLD`
- Date/time functions referenced:
  - `CURRENT_DATE`
  - `now()`
