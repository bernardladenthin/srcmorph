### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:21:35Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Manages library book checkouts and returns, updates copy availability, calculates overdue fees, and records loan audit events.

#### Purpose
- Provides checkout and return operations for member loans and book-copy inventory.
- Calculates capped late fees and audits checkout/return changes.

#### Type
- PostgreSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key (unnamed constraint).
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')` (unnamed constraint).
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.
- Indexes: none declared.

#### Reads
- `member.member_id`, `member.is_blocked` — checkout blocked-member existence check.
- `book.book_id`, `book.copies_free` — checkout availability predicate.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — return eligibility/update and book lookup.
- `fn_late_fee`: parameters `p_due_on`, `p_returned_on`; uses `CURRENT_DATE`.

#### Writes
- `book.copies_free` — checkout decrements available copies; return increments available copies.
- `loan`: checkout inserts `book_id`, `member_id`, `due_on` (1 row per call); return updates `returned_on`.
- `loan_audit`: trigger inserts one row per checkout event (`CHECKOUT`) or first transition from unreturned to returned (`RETURN`).

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` — audit association; no foreign-key constraint declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Rejects blocked members; decrements one free copy or raises an exception.
  - Inserts a loan due `p_days` from `CURRENT_DATE`; returns the new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `loan.returned_on` to `CURRENT_DATE` only if not already returned; raises an exception if unknown or already returned.
  - Restores one free copy to the associated book.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Returns 50 cents per overdue day, with no charge before the due date; uses current date when return date is null and caps the fee at 2000.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - On loan insert, records `CHECKOUT`; when `returned_on` changes from null to non-null, records `RETURN`; returns `NEW`.
- `trg_loan_audit`
  - Row-level `AFTER INSERT OR UPDATE OF returned_on` trigger on `loan`, executing `trg_loan_audit_fn`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-ins: `now()`, `CURRENT_DATE`, `LEAST`, `GREATEST`, `COALESCE`.
