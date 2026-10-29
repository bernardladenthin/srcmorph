### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:21:44Z
- G: 
- A: 0.0.0
- X: file
---
> Implements library book checkout and return, overdue fee calculation, and loan activity auditing.

#### Purpose
- Tracks checkout and return actions in `loan_audit`.
- Provides routines to check out and return books and calculate capped late fees.

#### Type
- PostgreSQL: table, PL/pgSQL functions, procedure, trigger

#### Schema
- `loan_audit.audit_id BIGSERIAL` — PK
- `loan_audit.loan_id BIGINT NOT NULL`
- `loan_audit.action VARCHAR(10) NOT NULL` — CHECK: `action IN ('CHECKOUT', 'RETURN')`
- `loan_audit.changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`
- No indexes beyond the primary key are defined.

#### Reads
- `fn_checkout_book`: `member.member_id`, `member.is_blocked`; `book.book_id`, `book.copies_free`
- `sp_return_book`: `loan.loan_id`, `loan.returned_on`, `loan.book_id`
- `fn_late_fee`: parameters `p_due_on`, `p_returned_on`; uses `CURRENT_DATE` when `p_returned_on` is null
- `trg_loan_audit_fn`: trigger row values `TG_OP`, `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`

#### Writes
- `fn_checkout_book`: updates `book.copies_free`; inserts `loan(book_id, member_id, due_on)`
- `sp_return_book`: updates `loan.returned_on`; updates `book.copies_free`
- `trg_loan_audit_fn`: inserts `loan_audit(loan_id, action)`

#### Relationships
- No foreign key constraints are declared.
- `loan_audit.loan_id` stores the loan identifier used by the loan audit trigger.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
  - Rejects blocked members; decrements `book.copies_free` only when positive, otherwise raises an exception.
  - Inserts a loan due `p_days` after `CURRENT_DATE`; returns the new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`
  - Marks an unreturned loan returned on `CURRENT_DATE`; raises an exception if unknown or already returned.
  - Increments the corresponding book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`
  - Immutable SQL function; calculates 50 cents per overdue day, with a zero floor and 2000-cent cap.
- `trg_loan_audit_fn() RETURNS TRIGGER`
  - On loan insert, records `CHECKOUT`; when `returned_on` changes from null to non-null, records `RETURN`.
  - Returns `NEW`.
- `trg_loan_audit`
  - AFTER INSERT or UPDATE OF `loan.returned_on`, executes once per row.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`
- Trigger function: `trg_loan_audit_fn`
- Built-ins: `CURRENT_DATE`, `now()`, `COALESCE`, `GREATEST`, `LEAST`
