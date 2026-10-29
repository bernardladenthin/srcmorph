### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T23:36:12Z
- G: 
- A: 0.0.0
- X: file
---
> Initializes library catalog and transaction records with sample authors, books, members, and loan history.

#### Purpose
- Seeds initial data for library system tables to enable testing and demonstration.
- Establishes reference data for authors and members, plus transactional data for books and loans.

#### Type
SQL script (PostgreSQL DML)

#### Schema
- `author`: `author_id` integer, `full_name` text, `birth_year` integer, `country_code` text
- `book`: `book_id` integer, `isbn` text, `title` text, `author_id` integer, `published_year` integer, `copies_total` integer, `copies_free` integer
- `member`: `member_id` integer, `email` text, `full_name` text, `joined_on` date, `is_blocked` boolean
- `loan`: `loan_id` integer, `book_id` integer, `member_id` integer, `loaned_on` date, `due_on` date, `returned_on` date

#### Writes
- `author` table via `INSERT` into `author_id`, `full_name`, `birth_year`, `country_code`
- `book` table via `INSERT` into `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`
- `member` table via `INSERT` into `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`
- `loan` table via `INSERT` into `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`

#### Relationships
- `book.author_id` -> `author.author_id`
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id`

#### Routine logic
- Wraps all operations in `BEGIN` / `COMMIT` transaction block.
- Inserts 15 rows into `author` table.
- Inserts 110 rows into `book` table.
- Inserts 50 rows into `member` table.
- Inserts 150 rows into `loan` table.

#### Dependencies
- `author` table (must exist prior to execution)
- `book` table (must exist prior to execution)
- `member` table (must exist prior to execution)
- `loan` table (must exist prior to execution)
