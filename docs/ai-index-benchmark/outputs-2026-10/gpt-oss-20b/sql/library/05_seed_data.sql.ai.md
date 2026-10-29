### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T22:50:58Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a PostgreSQL lending library, inserting authors, books, members and loan records.

#### Purpose
- Populate the `author`, `book`, `member`, and `loan` tables with initial test data.

#### Type
- SQL script (PostgreSQL DDL/DML)

#### Writes
- `author` (author_id, full_name, birth_year, country_code)
- `book` (book_id, isbn, title, author_id, published_year, copies_total, copies_free)
- `member` (member_id, email, full_name, joined_on, is_blocked)
- `loan` (loan_id, book_id, member_id, loaned_on, due_on, returned_on)

#### Dependencies
- Tables: `author`, `book`, `member`, `loan` (assumed pre‑existing with appropriate foreign keys).
