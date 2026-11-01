### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:43:41Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library catalog, author and member records, and book-loan history with generated sample data.

#### Purpose
- Populate the lending library with sample authors, books, members, and loans.
- Insert all seed records within one PostgreSQL transaction.

#### Type
- DML script; PostgreSQL.

#### Writes
- `author`: 15 rows; columns `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 rows; columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 rows; columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 rows; columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`.

#### Dependencies
- Target tables: `author`, `book`, `member`, `loan`.
