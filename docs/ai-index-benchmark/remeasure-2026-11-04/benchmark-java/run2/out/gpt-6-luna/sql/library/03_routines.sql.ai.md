### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:29:50Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Implements library book checkout and return, calculates overdue fees, and records loan checkout/return events for auditing.

#### Purpose
- Prevents blocked members or unavailable books from checking out; updates inventory and loan records.
- Calculates capped late fees and audits loan creation and return.

#### Type
- PostgreSQL: table, function, procedure, trigger function, trigger.
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`
- `sp_return_book(p_loan_id BIGINT)`
- `fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER` (`IMMUTABLE`)
- `trg_loan_audit_fn() RETURNS TRIGGER`
- `trg_loan_audit`: row-level `AFTER INSERT OR UPDATE OF returned_on` trigger on `loan`.

#### Schema
- `loan_audit`
  - `audit_id BIGSERIAL` — primary key, `NOT NULL`; constraint `loan_audit_pkey` (backing primary-key index).
  - `loan_id BIGINT NOT NULL`
  - `action VARCHAR(10) NOT NULL` — CHECK `loan_audit_action_check`: value is `CHECKOUT` or `RETURN`.
  - `changed_at TIMESTAMPTZ NOT NULL DEFAULT now()`

#### Reads
- `member.member_id`, `member.is_blocked` — checkout blocked-member check.
- `book.book_id`, `book.copies_free` — checkout availability; return inventory update.
- `loan.loan_id`, `loan.returned_on`, `loan.book_id` — return processing.
- `CURRENT_DATE`; `fn_late_fee` also uses `p_due_on`, `p_returned_on`.

#### Writes
- `book.copies_free` — checkout decrements when a copy is available; return increments.
- `loan`: checkout inserts `book_id`, `member_id`, `due_on` (1 row per successful call); return updates `returned_on`.
- `loan_audit`: trigger inserts `loan_id`, `action` (1 row for an `INSERT` event as `CHECKOUT`; 1 row when `returned_on` changes from NULL to non-NULL as `RETURN`).

#### Relationships
- `loan_audit.loan_id -> loan.loan_id` — audit association; no foreign key declared.
- `loan.book_id -> book.book_id` — checkout/return inventory association.
- `loan.member_id -> member.member_id` — checkout membership association.

#### Routine logic
- `fn_checkout_book`: rejects blocked members and books without free copies; decrements inventory, inserts a loan due `p_days` after `CURRENT_DATE`, returns `loan_id`.
- `sp_return_book`: marks an unreturned loan returned on `CURRENT_DATE`; errors for unknown/already-returned loans; restores one free copy.
- `fn_late_fee`: computes overdue days using `p_returned_on` or `CURRENT_DATE`, floors at zero, charges 50 cents per day, caps at 2000 cents.
- `trg_loan_audit_fn`: inserts a `CHECKOUT` audit record on loan insertion or a `RETURN` record on first transition to returned; returns `NEW`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Built-ins: `now()`, `CURRENT_DATE`.
