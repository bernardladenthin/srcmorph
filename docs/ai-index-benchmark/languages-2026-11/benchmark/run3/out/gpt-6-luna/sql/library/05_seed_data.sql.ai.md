### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:59:36Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library database with author, book inventory, member, and loan records for development or testing.

#### Purpose
- Populate a lending-library dataset with authors, catalog entries, members, and borrowing history.
- Run all seed inserts in one transaction.

#### Type
- PostgreSQL data-loading script; transaction uses `BEGIN` and `COMMIT`.

#### Writes
- `author(author_id, full_name, birth_year, country_code)`: 15 rows.
- `book(book_id, isbn, title, author_id, published_year, copies_total, copies_free)`: 110 rows.
- `member(member_id, email, full_name, joined_on, is_blocked)`: 50 rows.
- `loan(loan_id, book_id, member_id, loaned_on, due_on, returned_on)`: 150 rows.

#### Relationships
- `book.author_id -> author.author_id` — author IDs are supplied in book seed data.
- `loan.book_id -> book.book_id` — book IDs are supplied in loan seed data.
- `loan.member_id -> member.member_id` — member IDs are supplied in loan seed data.

#### Dependencies
- Writes to tables `author`, `book`, `member`, and `loan`.
