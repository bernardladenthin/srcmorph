### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:22:08Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library database with authors, book inventory, members, and historical or active loan records.

#### Purpose
- Populate `author`, `book`, `member`, and `loan` with generated sample data.
- Insert all seed records in a single PostgreSQL transaction.

#### Type
- DML seed-data script; PostgreSQL.

#### Writes
- `author(author_id, full_name, birth_year, country_code)` — 15 author records.
- `book(book_id, isbn, title, author_id, published_year, copies_total, copies_free)` — 110 book records.
- `member(member_id, email, full_name, joined_on, is_blocked)` — 50 member records.
- `loan(loan_id, book_id, member_id, loaned_on, due_on, returned_on)` — 150 loan records; `returned_on` is NULL for some active loans.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- Target tables: `author`, `book`, `member`, `loan`.
