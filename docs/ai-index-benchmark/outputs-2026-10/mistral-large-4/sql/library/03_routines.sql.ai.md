### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:43:00Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library transaction processing: checking out book copies to members, returning loans to restore inventory, calculating capped late fees, and auditing checkout/return events.

#### Purpose
- Execute atomic checkout (decrement `book.copies_free`, insert `loan`) and return (set `loan.returned_on`, increment `book.copies_free`) operations for library members
- Compute late fees in cents (50 per overdue day, capped at 2000) and record loan lifecycle events in `loan_audit`

#### Type
- Table: `loan_audit` (PostgreSQL)
- Function: `fn_checkout_book` (PL/pgSQL), `fn_late_fee` (SQL, IMMUTABLE), `trg_loan_audit_fn` (PL/pgSQL trigger function)
- Procedure: `sp_return_book` (PL/pgSQL)
- Trigger: `trg_loan_audit` (AFTER INSERT OR UPDATE OF `returned_on` ON `loan`)

#### Schema
`loan_audit`:
- `audit_id` BIGSERIAL PRIMARY KEY
- `loan_id` BIGINT NOT NULL
- `action` VARCHAR(10) NOT NULL CHECK (`action` IN ('CHECKOUT', 'RETURN'))
- `changed_at` TIMESTAMPTZ NOT NULL DEFAULT now()

#### Reads
- `member.member_id`, `member.is_blocked` — validated in `fn_checkout_book`
- `book.book_id`, `book.copies_free` — read and updated in `fn_checkout_book`, `sp_return_book`
- `loan.loan_id`, `loan.book_id`, `loan.returned_on` — read in `sp_return_book`; `NEW`/`OLD` row values accessed in `trg_loan_audit_fn`

#### Writes
- `book.copies_free` — decremented in `fn_checkout_book`, incremented in `sp_return_book`
- `loan` — INSERT (`book_id`, `member_id`, `due_on`) in `fn_checkout_book`; UPDATE `returned_on` in `sp_return_book`
- `loan_audit` — INSERT (`loan_id`, `action`) in `trg_loan_audit_fn`

#### Relationships
- `loan.loan_id` -> `loan_audit.loan_id` (trigger copies `NEW.loan_id` into audit)
- `loan.book_id` -> `book.book_id` (checkout/return join on book inventory)
- `loan.member_id` -> `member.member_id` (checkout validates member)

#### Routine logic
`fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21) RETURNS BIGINT`:
- Raises exception if `member.is_blocked` for `p_member_id`
- `UPDATE book SET copies_free = copies_free - 1 WHERE book_id = p_book_id AND copies_free > 0`; raises exception if no row affected
- `INSERT INTO loan (book_id, member_id, due_on) VALUES (p_book_id, p_member_id, CURRENT_DATE + p_days) RETURNING loan_id`
- Returns new `loan_id`

`sp_return_book(p_loan_id BIGINT)`:
- `UPDATE loan SET returned_on = CURRENT_DATE WHERE loan_id = p_loan_id AND returned_on IS NULL RETURNING book_id`
- Raises exception if no row updated (unknown or already returned)
- `UPDATE book SET copies_free = copies_free + 1 WHERE book_id = v_book_id`

`fn_late_fee(p_due_on DATE, p_returned_on DATE) RETURNS INTEGER`:
- Returns `LEAST(GREATEST(COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, 0) * 50, 2000)`

`trg_loan_audit_fn() RETURNS TRIGGER`:
- On `TG_OP = 'INSERT'`: inserts `loan_audit(loan_id, action)` with `'CHECKOUT'`
- On update where `NEW.returned_on IS NOT NULL AND OLD.returned_on IS NULL`: inserts `loan_audit(loan_id, action)` with `'RETURN'`
- Returns `NEW`

`trg_loan_audit`:
- `AFTER INSERT OR UPDATE OF returned_on ON loan FOR EACH ROW EXECUTE FUNCTION trg_loan_audit_fn()`

#### Dependencies
- External tables: `member`, `book`, `loan`
- Local table: `loan_audit`
