### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:46:21Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library database with author, book, member, and loan records, including inventory availability and loan return status.

#### Purpose
- Populate library catalog, author, member, and borrowing data for a PostgreSQL environment.
- Wrap all seed inserts in a transaction.

#### Type
- DML seed script; PostgreSQL.

#### Writes
- `author`: 15 rows; writes `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 rows; writes `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 rows; writes `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 rows; writes `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`.
- Transaction commits after inserts.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
