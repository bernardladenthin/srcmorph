### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:28:03Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Implements library-book checkout and return, overdue-fee calculation, and loan checkout/return auditing.

#### Purpose
- Tracks loan audit events and provides routines to check out books, return loans, and calculate late fees.
- Audits loan creation and first-time return updates.

#### Type
- PostgreSQL: table, functions, procedure, trigger; PL/pgSQL and SQL routines.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key (constraint name not declared).
  - `loan_id BIGINT NOT NULL`.
  - `action VARCHAR(10) NOT NULL` — CHECK `action IN ('CHECKOUT', 'RETURN')` (constraint name not declared).
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`.

#### Reads
- `fn_checkout_book`: `member.member_id`, `member.is_blocked`.
- `fn_checkout_book`: `book.book_id`, `book.copies_free` (update predicate).
- `sp_return_book`: `loan.loan_id`, `loan.returned_on`, `loan.book_id` (update predicate and `RETURNING`).
- `trg_loan_audit_fn`: trigger row fields `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on`.
- `fn_late_fee`: parameters `p_due_on`, `p_returned_on`; reads `CURRENT_DATE`.

#### Writes
- `fn_checkout_book`: updates `book.copies_free`; inserts one `loan` row per successful call.
- `sp_return_book`: updates one matching unreturned `loan.returned_on`; increments `book.copies_free` for that loan.
- `trg_loan_audit_fn`: inserts one `loan_audit` row on loan insert (`CHECKOUT`) or transition from unreturned to returned (`RETURN`).
- `trg_loan_audit`: fires after `INSERT` or `UPDATE OF returned_on` on `loan`.

#### Relationships
- No declared foreign keys.
- Audit association: `loan_audit.loan_id -> loan.loan_id` (logical; not enforced in this file).

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`: rejects blocked members; decrements an available book copy or raises an exception; creates a loan due `CURRENT_DATE + p_days`; returns the new `loan_id`.
- `sp_return_book(p_loan_id BIGINT)`: marks an unreturned loan returned today; raises an exception if absent or already returned; restores the book’s free-copy count.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`: immutable; calculates overdue days using return date or today, floors at zero, charges 50 cents per day, and caps at 2000 cents.
- `trg_loan_audit_fn() RETURNS TRIGGER`: inserts `CHECKOUT` audit on loan insert and `RETURN` audit when `returned_on` changes from NULL to non-NULL; returns `NEW`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Trigger function: `trg_loan_audit_fn`.
