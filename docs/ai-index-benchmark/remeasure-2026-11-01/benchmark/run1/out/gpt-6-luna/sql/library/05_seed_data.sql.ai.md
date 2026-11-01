### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:22:07Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library catalog with authors, books, members, and loan history.

#### Purpose
- Load sample library catalog, membership, and borrowing records in one transaction.
- Populate author, book, member, and loan data.

#### Type
- PostgreSQL DML seed script; explicit transaction (`BEGIN` / `COMMIT`).

#### Writes
- `author`: 15 rows via 15 single-row `INSERT`s; columns `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 rows via 110 single-row `INSERT`s; columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 rows via 50 single-row `INSERT`s; columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 rows via 150 single-row `INSERT`s; columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`.

#### Relationships
- `book.author_id -> author.author_id` (author association represented in inserted data).
- `loan.book_id -> book.book_id` (book association represented in inserted data).
- `loan.member_id -> member.member_id` (member association represented in inserted data).

#### Dependencies
- Target tables: `author`, `book`, `member`, `loan`.
