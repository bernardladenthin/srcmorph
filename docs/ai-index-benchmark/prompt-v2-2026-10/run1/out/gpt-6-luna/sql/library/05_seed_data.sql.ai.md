### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:32:53Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library catalog, author and member records, and book-loan history.

#### Purpose
- Populate sample authors, books, library members, and loans within one transaction.

#### Type
- PostgreSQL DML seed script

#### Writes
- `author`: 15 rows; columns `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 rows; columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 rows; columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 rows; columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`.

#### Relationships
- `book.author_id -> author.author_id` (seeded author IDs)
- `loan.book_id -> book.book_id` (seeded book IDs)
- `loan.member_id -> member.member_id` (seeded member IDs)

#### Dependencies
- Tables: `author`, `book`, `member`, `loan`
