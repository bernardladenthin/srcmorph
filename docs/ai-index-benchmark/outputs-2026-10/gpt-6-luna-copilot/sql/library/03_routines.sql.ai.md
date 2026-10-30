### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:36:05Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library book checkout and return, tracking available copies, overdue fees, and loan audit events.

#### Purpose
- Check out free book copies to unblocked members and return them to inventory.
- Calculate capped late fees and record checkout/return events.

#### Type
- PostgreSQL: table, functions, procedure, trigger function, trigger.

#### Schema
- `loan_audit.audit_id BIGSERIAL` — PK.
- `loan_audit.loan_id BIGINT NOT NULL`.
- `loan_audit.action VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN'))`.
- `loan_audit.changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.
- No other indexes or foreign keys are declared.

#### Reads
- `member.member_id`, `member.is_blocked` — checkout eligibility.
- `book.book_id`, `book.copies_free` — checkout availability.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — return processing.
- `p_due_on`, `p_returned_on` — late-fee calculation.
- Trigger transition values: `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`.

#### Writes
- `book.copies_free` — decremented at checkout; incremented at return.
- `loan.book_id`, `loan.member_id`, `loan.due_on` — inserted at checkout.
- `loan.returned_on` — set at return.
- `loan_audit.loan_id`, `loan_audit.action` — inserted by the audit trigger.

#### Relationships
- `loan.book_id -> book.book_id` — checkout and return inventory updates.
- `loan_audit.loan_id -> loan.loan_id` — audit events identify loans.
- No foreign-key constraints are declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT` — rejects blocked members or books with no free copies; decrements inventory, inserts a loan due after `p_days`, and returns `loan_id`.
- `sp_return_book(p_loan_id BIGINT)` — marks an unreturned loan returned; errors for unknown or already-returned loans; restores the book copy.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER` — immutable SQL function; charges 50 cents per overdue day, using current date if not returned, with a zero floor and 2000-cent cap.
- `trg_loan_audit_fn()` — on loan insert, records `CHECKOUT`; when `returned_on` changes from NULL to non-NULL, records `RETURN`; returns `NEW`.
- `trg_loan_audit` — row-level AFTER INSERT or UPDATE OF `loan.returned_on` trigger.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Trigger function: `trg_loan_audit_fn`.
