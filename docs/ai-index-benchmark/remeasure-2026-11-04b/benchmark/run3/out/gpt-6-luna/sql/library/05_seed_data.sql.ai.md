### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:06:46Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Seeds a lending library with author, book, member, and loan records for development or testing.

#### Purpose
- Populate the library catalog, membership, and borrowing history with deterministic sample data.
- Insert all seed records within one transaction.

#### Type
- DML script; PostgreSQL dialect.

#### Writes
- `author`: 15 rows; one `INSERT` family, IDs 1–15; writes `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 rows; one `INSERT` family, IDs 1–110; writes `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 rows; one `INSERT` family, IDs 1–50; writes `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 rows; one `INSERT` family, IDs 1–150; writes `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- Tables: `author`, `book`, `member`, `loan`.
