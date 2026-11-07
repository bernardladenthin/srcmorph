### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:45:19Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Seeds a lending-library database with author, book, member, and loan records for development or testing.

#### Purpose
- Populate sample catalog, membership, and borrowing data in one transaction.

#### Type
- PostgreSQL DML seed script

#### Writes
- `author`: 15 rows; IDs 1–15; columns `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 rows; IDs 1–110; columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 rows; IDs 1–50; columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 rows; IDs 1–150; columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`.

#### Relationships
- Seeded `book.author_id` values correspond to `author.author_id`.
- Seeded `loan.book_id` values correspond to `book.book_id`.
- Seeded `loan.member_id` values correspond to `member.member_id`.

#### Dependencies
- Target tables: `author`, `book`, `member`, `loan`.
