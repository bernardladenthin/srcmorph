### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:50:07Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library database with author, book, member, and loan records, including inventory counts and loan/return dates.

#### Purpose
- Populate a lending-library dataset with authors, catalogued books, members, and borrowing history.
- Execute all seed inserts in one transaction.

#### Type
- DML seed script; PostgreSQL dialect.

#### Reads
- None; the script contains no `SELECT` statements or source queries.

#### Writes
- `author`: 15 single-row `INSERT`s (1 row each) = **15 rows**; columns `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 single-row `INSERT`s (1 row each) = **110 rows**; columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 single-row `INSERT`s (1 row each) = **50 rows**; columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 single-row `INSERT`s (1 row each) = **150 rows**; columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`.

#### Dependencies
- Insert targets: `author`, `book`, `member`, `loan`.
