### 03_routines.sql
- H: 1.0
- C: 14E34698
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:43:16Z
- G: 
- A: 0.0.0
- X: file
---
I will index the provided SQL source file following the specified format.

I am extracting the schema details from the SQL file. The `loan_audit` table contains an auto-incrementing primary key, a foreign loan ID, an action field restricted to CHECKOUT or RETURN, and a timestamp column defaulting to the current time.

The `fn_checkout_book` function checks member status and available copies before lending. It inserts a loan record with a due date and returns the new loan ID. The `sp_return_book` procedure marks a loan as returned and restores one free copy to the book inventory.

The return procedure raises an exception if the loan is unknown or already returned.

`fn_late_fee` calculates late fees in cents at 50 per day overdue, capped at 2000.

The audit trigger records CHECKOUT on insert and RETURN when a loan is first marked as returned.

No explicit foreign key constraints appear in the source. The conceptual relationships between loan, book, member, and loan_audit are inferred from the function joins.

The file contains multiple SQL object kinds: a table, functions, a procedure, and a trigger. The dialect is PostgreSQL.

I am cataloging the exact columns each routine reads from other tables. The checkout function reads member status and book inventory. The return procedure reads loan state and book copies. The audit trigger references loan identifiers and returned dates.

The late fee function reads no tables. It uses only parameters and the current date.

The checkout routine writes to the book inventory, inserts into the loan table, and returns the new loan ID. The return routine updates the loan record and increments the book copy count.

Dependencies include the member, book, loan, and loan_audit tables, plus the audit trigger function and built-in date functions.> Lending-library checkout, return, overdue late-fee calculation, and loan audit trail.

#### Purpose
- Records `CHECKOUT` and `RETURN` events on `loan_audit` and changes book availability plus loan status.
- Calculates a capped per-day late fee and writes an audit row when a loan is created or marked returned.

#### Type
- Table `loan_audit`; functions `fn_checkout_book`, `fn_late_fee`, `trg_loan_audit_fn`; procedure `sp_return_book`; trigger `trg_loan_audit`.
- Dialect: PostgreSQL (PL/pgSQL and SQL).

#### Schema
- `loan_audit.audit_id` `BIGSERIAL` `PRIMARY KEY`.
- `loan_audit.loan_id` `BIGINT` `NOT NULL`.
- `loan_audit.action` `VARCHAR(10)` `NOT NULL` `CHECK (action IN ('CHECKOUT', 'RETURN'))`.
- `loan_audit.changed_at` `TIMESTAMPTZ` `NOT NULL` `DEFAULT now()`.
- No secondary indexes and no declared foreign keys in this file.

#### Reads
- `fn_checkout_book` reads `member.member_id`, `member.is_blocked` and `book.book_id`, `book.copies_free`.
- `sp_return_book` reads `loan.loan_id`, `loan.returned_on`, `loan.book_id`.
- `trg_loan_audit_fn` reads `NEW.loan_id`, `NEW.returned_on`, `OLD.returned_on` on `loan`.
- `fn_late_fee` reads only parameters `p_due_on`, `p_returned_on` (and `CURRENT_DATE` when return date is null).

#### Writes
- `fn_checkout_book` decrements `book.copies_free` and inserts `loan.book_id`, `loan.member_id`, `loan.due_on`, returning `loan.loan_id`.
- `sp_return_book` sets `loan.returned_on` and increments `book.copies_free`.
- `trg_loan_audit_fn` inserts `loan_audit.loan_id`, `loan_audit.action` (`CHECKOUT` or `RETURN`); `changed_at` uses `DEFAULT now()`.

#### Relationships
- No `FOREIGN KEY` constraints declared.
- Checkout links `loan.member_id` to `member.member_id` and `loan.book_id` to `book.book_id`.
- Return links `loan.book_id` to `book.book_id`.
- Audit links `loan_audit.loan_id` to `loan.loan_id`.

#### Routine logic
- `fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21)` returns `BIGINT`: reject blocked `member.is_blocked`; decrement `book.copies_free` only if `copies_free > 0`; insert `loan` with `due_on = CURRENT_DATE + p_days`; return new `loan_id`. Raises if member blocked or no free copy.
- `sp_return_book(p_loan_id BIGINT)`: set `loan.returned_on = CURRENT_DATE` only when `returned_on IS NULL`; increment `book.copies_free` for returned `book_id`. Raises if loan unknown or already returned.
- `fn_late_fee(p_due_on DATE, p_returned_on DATE)` returns `INTEGER`, `LANGUAGE sql`, `IMMUTABLE`: `LEAST(GREATEST(COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, 0) * 50, 2000)` cents (50 per overdue day, cap 2000).
- `trg_loan_audit_fn()` returns `TRIGGER`: on `INSERT`, audit `CHECKOUT`; on update, audit `RETURN` when `NEW.returned_on` becomes non-null and `OLD.returned_on` was null; returns `NEW`.
- `trg_loan_audit` is `AFTER INSERT OR UPDATE OF returned_on ON loan` `FOR EACH ROW`, executing `trg_loan_audit_fn()`.

#### Dependencies
- Tables: `member`, `book`, `loan`, `loan_audit`.
- Routines: `fn_checkout_book`, `sp_return_book`, `fn_late_fee`, `trg_loan_audit_fn`, `trg_loan_audit`.
- Built-ins: `now()`, `CURRENT_DATE`.
