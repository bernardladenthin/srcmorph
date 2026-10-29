### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:43:34Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library checkout, return, late-fee calculation, and audit-trail routines for managing book loans to members.

#### Purpose
- Implements the core loan lifecycle: checking out a free book copy to a member, returning it, computing overdue fees, and recording an audit trail.
- Enforces business rules (blocked members, available copies, single-return) via PL/pgSQL routines.

#### Type
- PostgreSQL (PL/pgSQL dialect)
- Objects: 1 table, 2 functions (PL/pgSQL + SQL), 1 procedure, 1 trigger function, 1 trigger

#### Schema

**loan_audit**

| Column | Type | Constraints |
|--------|------|-------------|
| audit_id | BIGSERIAL | PRIMARY KEY |
| loan_id | BIGINT | NOT NULL |
| action | VARCHAR(10) | NOT NULL, CHECK (action IN ('CHECKOUT','RETURN')) |
| changed_at | TIMESTAMPTZ | NOT NULL, DEFAULT now() |

#### Reads

- `fn_checkout_book`: `member.member_id`, `member.is_blocked`; `book.book_id`, `book.copies_free`
- `sp_return_book`: `loan.loan_id`, `loan.returned_on`, `loan.book_id`
- `trg_loan_audit_fn`: `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`
- `fn_late_fee`: parameters only (no table reads)

#### Writes

- `fn_checkout_book`: `book.copies_free` (decrement); `loan` (INSERT: book_id, member_id, due_on)
- `sp_return_book`: `loan.returned_on` (set to CURRENT_DATE); `book.copies_free` (increment)
- `trg_loan_audit_fn`: `loan_audit` (INSERT: loan_id, action)

#### Relationships

- `loan_audit.loan_id -> loan.loan_id` (logical FK, no constraint declared)
- `loan.book_id -> book.book_id` (referenced in routines)
- `loan.member_id -> member.member_id` (referenced in routines)

#### Routine logic

**fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) → BIGINT**
- Raises exception if member is blocked (`member.is_blocked`).
- Decrements `book.copies_free` where `copies_free > 0`; raises if no free copy.
- INSERTs into `loan` with `due_on = CURRENT_DATE + p_days`.
- Returns new `loan_id`.

**sp_return_book(p_loan_id BIGINT)**
- Sets `loan.returned_on = CURRENT_DATE` where `returned_on IS NULL`; raises if loan unknown or already returned.
- Increments `book.copies_free` for the returned book.

**fn_late_fee(p_due_on DATE, p_returned_on DATE) → INTEGER**
- IMMUTABLE SQL function.
- Computes `LEAST(GREATEST(COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, 0) * 50, 2000)` — 50 cents/day overdue, capped at 2000.

**trg_loan_audit_fn() → TRIGGER**
- On INSERT into `loan`: writes `('CHECKOUT')` row to `loan_audit`.
- On UPDATE where `returned_on` transitions NULL → non-NULL: writes `('RETURN')` row to `loan_audit`.
- Returns NEW.

**trg_loan_audit**
- AFTER INSERT OR UPDATE OF returned_on ON loan, FOR EACH ROW.

#### Dependencies

- `member` (table, external)
- `book` (table, external)
- `loan` (table, external)
- `loan_audit` (table, defined in this file)
