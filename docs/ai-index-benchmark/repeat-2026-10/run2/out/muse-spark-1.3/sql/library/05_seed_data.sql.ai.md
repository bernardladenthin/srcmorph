### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:17:14Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library seed dataset populating authors, books, members, and book loans for testing.
#### Purpose
- Seed `author`, `book`, `member`, `loan` with deterministic test data for lending library
- Provide PostgreSQL `BEGIN` / `COMMIT` bulk `INSERT` fixture, generated, do-not-edit
#### Type
- DML seed-data script, PostgreSQL dialect (`DATE '2020-01-01' + N`, `DATE '2021-01-01' + N`, `TRUE`/`FALSE`/`NULL`)
#### Writes
- `author(author_id, full_name, birth_year, country_code)` — 15 rows, author_id 1-15
- `book(book_id, isbn, title, author_id, published_year, copies_total, copies_free)` — 110 rows, book_id 1-110, isbn `978-3-*`, copies_total 2
- `member(member_id, email, full_name, joined_on, is_blocked)` — 50 rows, member_id 1-50, email `*@example.org`, joined_on derived, is_blocked mostly FALSE with TRUE for member_id 13, 15
- `loan(loan_id, book_id, member_id, loaned_on, due_on, returned_on)` — 150 rows, loan_id 1-150, loaned_on/due_on derived, returned_on nullable for active/overdue loans
#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
#### Dependencies
- Tables `author`, `book`, `member`, `loan`
