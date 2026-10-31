### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:35:53Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data script populating a lending library database (authors, books, members, loans) with 150 loan records and reference data for a PostgreSQL schema.

#### Purpose
- Loads initial demo/test data for a lending library: authors, books catalog, members, and loan transactions.
- Generated file; wrapped in a single transaction (BEGIN ... COMMIT).

#### Type
- SQL DML script (INSERT statements only), PostgreSQL dialect (BEGIN/COMMIT, `DATE 'YYYY-MM-DD' + integer` date arithmetic, TRUE/FALSE literals).

#### Schema
Inferred from INSERT column lists (no CREATE TABLE in this file):
- `author (author_id, full_name, birth_year, country_code)` - 15 rows, ids 1-15.
- `book (book_id, isbn, title, author_id, published_year, copies_total, copies_free)` - 110 rows, ids 1-110; `copies_total` always 2; `copies_free` in 0-2.
- `member (member_id, email, full_name, joined_on, is_blocked)` - 50 rows, ids 1-50; `is_blocked` TRUE for member_id 13, 15.
- `loan (loan_id, book_id, member_id, loaned_on, due_on, returned_on)` - 150 rows, ids 1-150; `returned_on` NULL for open loans (e.g., loan_id 1, 8, 9, 12, 19, 20, 28, 46, 58, 63, 66, 81, 84, 90, 92, 107, 114, 116, 117, 118, 128, 129, 137, 141, 149).

Date values are expressed as `DATE '2020-01-01' + n` (member.joined_on) or `DATE '2021-01-01' + n` (loan dates).

#### Reads
- None; INSERT-only script with literal values.

#### Writes
- `author` (INSERT, 15 rows)
- `book` (INSERT, 110 rows)
- `member` (INSERT, 50 rows)
- `loan` (INSERT, 150 rows)

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Routine logic
- Not applicable (no procedures, functions, or triggers).
- Script effect: transactional bulk load in dependency order: author, book, member, loan.
- Note: `loan.returned_on` may precede `loaned_on`-relative expectations in some rows (e.g., loan_id 3 returned before due date arithmetic); values are literal, not validated.

#### Dependencies
- Tables: `author`, `book`, `member`, `loan`.
- Columns referenced: `author_id`, `book_id`, `member_id`, `copies_total`, `copies_free`, `is_blocked`, `joined_on`, `loaned_on`, `due_on`, `returned_on`.
