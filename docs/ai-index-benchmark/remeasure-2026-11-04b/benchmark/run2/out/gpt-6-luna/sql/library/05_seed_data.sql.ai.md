### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:58:34Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Seeds a lending-library database with catalog, author, member, and loan records for development and testing.

#### Purpose
- Populate author, book, member, and loan data in one transaction.
- Insert records with explicit IDs and generated date offsets.

#### Type
- PostgreSQL DML transaction.

#### Writes
- `author`: 15 rows; IDs 1–15; columns `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 rows; IDs 1–110; columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 rows; IDs 1–50; columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 rows; IDs 1–150; columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`; some `returned_on` values are `NULL`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- Tables: `author`, `book`, `member`, `loan`.
