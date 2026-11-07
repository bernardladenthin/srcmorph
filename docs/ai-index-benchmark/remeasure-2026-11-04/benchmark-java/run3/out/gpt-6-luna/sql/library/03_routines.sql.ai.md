### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:45:01Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Manages library book checkout and return, tracks loan audit events, and calculates capped overdue fees.

#### Purpose
- Lends available book copies to unblocked members and records loan returns.
- Audits checkouts and returns; computes late fees in cents.

#### Type
- PostgreSQL: table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key (unnamed constraint).
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL` — unnamed CHECK: `action IN ('CHECKOUT', 'RETURN')`.
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.

#### Reads
- `member.member_id`, `member.is_blocked` — checkout eligibility.
- `book.book_id`, `book.copies_free` — checkout availability.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — return processing.
- `fn_late_fee` inputs: `p_due_on`, `p_returned_on`; uses `CURRENT_DATE`.

#### Writes
- `fn_checkout_book`: updates `book.copies_free`; inserts `loan(book_id, member_id, due_on)` (one row per successful call).
- `sp_return_book`: updates `loan.returned_on`; updates `book.copies_free`.
- `trg_loan_audit_fn`: inserts `loan_audit(loan_id, action)` for checkout or first return (one row per qualifying trigger event).

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` — audit association; no foreign key declared.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Raises an exception for blocked members or unavailable copies.
  - Decrements free-copy count, inserts a loan due `p_days` from today, returns `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Sets `returned_on` to today for an unreturned loan and retrieves its `book_id`.
  - Raises an exception if the loan is unknown or already returned; otherwise increments the book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Immutable SQL function; charges 50 cents per overdue day, using today when return date is null; floors at zero and caps at 2000.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - Inserts `CHECKOUT` audit on loan insertion; inserts `RETURN` when `returned_on` changes from null to non-null; returns `NEW`.
- `trg_loan_audit`
  - After insert or update of `loan.returned_on`, executes `trg_loan_audit_fn` once per row.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-ins: `now()`, `CURRENT_DATE`, `COALESCE`, `GREATEST`, `LEAST`.
