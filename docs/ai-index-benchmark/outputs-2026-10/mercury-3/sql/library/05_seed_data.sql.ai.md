### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:32:55Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending library database with initial catalog and operational rows — authors, books, members, and loan records — as a single transactional batch.

#### Purpose
- Populates the `author`, `book`, `member`, and `loan` tables with baseline data for a lending-library domain.
- All statements wrapped in a `BEGIN`/`COMMIT` transaction for atomic load.

#### Type
- DML seed data (PostgreSQL dialect; uses `DATE '...' + integer` date arithmetic and `FALSE`/`TRUE` booleans).

#### Schema
Literal column sets as used in the INSERT statements (constraints not defined in this file):

- `author`: `author_id` (key), `full_name`, `birth_year`, `country_code`
- `book`: `book_id` (key), `isbn`, `title`, `author_id` (references author), `published_year`, `copies_total`, `copies_free`
- `member`: `member_id` (key), `email`, `full_name`, `joined_on` (date), `is_blocked` (boolean)
- `loan`: `loan_id` (key), `book_id` (references book), `member_id` (references member), `loaned_on` (date), `due_on` (date), `returned_on` (date, nullable)

#### Writes
- `INSERT INTO author` — 15 rows (`author_id` 1–15).
- `INSERT INTO book` — 110 rows (`book_id` 1–110).
- `INSERT INTO member` — 50 rows (`member_id` 1–50).
- `INSERT INTO loan` — 150 rows (`loan_id` 1–150).

#### Relationships
- `book.author_id -> author.author_id` (author of a book)
- `loan.book_id -> book.book_id` (book borrowed)
- `loan.member_id -> member.member_id` (member borrowing)

#### Dependencies
- Tables referenced as insert targets: `author`, `book`, `member`, `loan`.
