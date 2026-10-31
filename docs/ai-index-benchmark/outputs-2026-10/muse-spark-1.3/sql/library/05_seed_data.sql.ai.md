### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:16:35Z
- G: 
- A: 0.0.0
- X: file
---
> Populates a lending library with authors, books, members, and loan history for testing/demo use.
#### Purpose
- Seed initial lending-library dataset in PostgreSQL
- Provide queryable `author`, `book`, `member`, `loan` rows for development/benchmarking
#### Type
- DML seed-data script, PostgreSQL dialect (`BEGIN` / `COMMIT`, `DATE '2021-01-01' + N`)
#### Writes
- `author(author_id, full_name, birth_year, country_code)` — 15 rows, ids 1-15
- `book(book_id, isbn, title, author_id, published_year, copies_total, copies_free)` — 110 rows, ids 1-110
- `member(member_id, email, full_name, joined_on, is_blocked)` — 50 rows, ids 1-50, `joined_on` as `DATE '2020-01-01' + offset`, `is_blocked` mostly FALSE
- `loan(loan_id, book_id, member_id, loaned_on, due_on, returned_on)` — 150 rows, ids 1-150, `loaned_on/due_on` as `DATE '2021-01-01' + offset`, `returned_on` nullable for active loans
#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
#### Dependencies
- Tables `author`, `book`, `member`, `loan`
