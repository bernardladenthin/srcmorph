### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:54:30Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library: populates authors, books, members, and loan records.

#### Purpose
- Populate `author`, `book`, `member`, and `loan` tables with initial test data.

#### Type
- Data seed script (PostgreSQL).

#### Writes
- `author` (author_id, full_name, birth_year, country_code)
- `book` (book_id, isbn, title, author_id, published_year, copies_total, copies_free)
- `member` (member_id, email, full_name, joined_on, is_blocked)
- `loan` (loan_id, book_id, member_id, loaned_on, due_on, returned_on)

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- Tables: `author`, `book`, `member`, `loan` (PostgreSQL).
