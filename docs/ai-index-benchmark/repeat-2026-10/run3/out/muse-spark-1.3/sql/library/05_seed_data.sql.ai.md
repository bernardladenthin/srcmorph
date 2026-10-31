### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:23:33Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending library with authors, books, members, and book loans for testing and development.
#### Purpose
- Seed initial lending-library dataset in PostgreSQL transaction `BEGIN` to `COMMIT`
- Populates catalog and circulation: `author` + `book` inventory with `copies_total` / `copies_free`, `member` accounts with `is_blocked`, `loan` history with `loaned_on` / `due_on` / `returned_on`
#### Type
- DML seed-data script, PostgreSQL dialect (`DATE '2021-01-01' + n`, `TRUE`/`FALSE`, `NULL` open loans)
#### Writes
- `author(author_id, full_name, birth_year, country_code)` — 15 rows, `author_id` 1-15
- `book(book_id, isbn, title, author_id, published_year, copies_total, copies_free)` — 110 rows, `book_id` 1-110, `isbn` 978-3-..., `author_id` 1-15
- `member(member_id, email, full_name, joined_on, is_blocked)` — 50 rows, `member_id` 1-50, `email` example.org, `joined_on` DATE arithmetic, `is_blocked` mostly FALSE
- `loan(loan_id, book_id, member_id, loaned_on, due_on, returned_on)` — 150 rows, `loan_id` 1-150, `returned_on` NULL for active/overdue loans
#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
#### Dependencies
- `author`, `book`, `member`, `loan`
