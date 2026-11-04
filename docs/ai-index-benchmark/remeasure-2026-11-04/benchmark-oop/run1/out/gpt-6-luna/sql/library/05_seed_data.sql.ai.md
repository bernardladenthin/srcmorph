### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:21:51Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Seeds a lending-library database with authors, book inventory, members, and loan history.

#### Purpose
- Populate the library catalog, member registry, and borrowing records with sample data.
- Run all seed inserts within one transaction.

#### Type
- PostgreSQL DML script; transaction uses `BEGIN` and `COMMIT`.

#### Writes
- `author`: 15 rows in 15 single-row INSERTs; `author_id` 1–15.
- `book`: 110 rows in 110 single-row INSERTs; `book_id` 1–110.
- `member`: 50 rows in 50 single-row INSERTs; `member_id` 1–50.
- `loan`: 150 rows in 150 single-row INSERTs; `loan_id` 1–150; rows include both `returned_on` dates and `NULL`.
- Inserted columns:
  - `author`: `author_id`, `full_name`, `birth_year`, `country_code`
  - `book`: `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`
  - `member`: `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`
  - `loan`: `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- Target tables: `author`, `book`, `member`, `loan`.
