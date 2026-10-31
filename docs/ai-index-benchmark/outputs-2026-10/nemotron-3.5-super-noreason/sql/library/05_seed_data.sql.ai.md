### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:02:58Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a PostgreSQL lending library database with authors, books, members, and loan records for testing and development.

#### Purpose
- Populates the `author`, `book`, `member`, and `loan` tables with initial seed data.
- Wrapped in a `BEGIN`/`COMMIT` transaction for atomic loading.

#### Type
DML seed script for PostgreSQL.

#### Schema
- `author`: `author_id`, `full_name`, `birth_year`, `country_code`
- `book`: `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`
- `member`: `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`
- `loan`: `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`

#### Reads
None. This file only inserts data.

#### Writes
- `author` (`author_id`, `full_name`, `birth_year`, `country_code`) — 15 rows
- `book` (`book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`) — 110 rows
- `member` (`member_id`, `email`, `full_name`, `joined_on`, `is_blocked`) — 50 rows
- `loan` (`loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`) — 150 rows

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- `author` table
- `book` table
- `member` table
- `loan` table
