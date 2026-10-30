### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:43:04Z
- G: 
- A: 0.0.0
- X: file
---
I will index the provided SQL schema file.> Lending-library catalog of authors, books, members, and book loans, including available-copy counts and open-loan due dates.

#### Purpose
- Core PostgreSQL schema for a lending library: authors, books with stock, members, and loans.
- Tracks ISBN uniqueness, free vs total copies, membership block status, and whether a loan is still open.

#### Type
- Tables and indexes. Dialect: PostgreSQL (`BIGSERIAL`, partial index `WHERE`).

#### Schema
- `author`: `author_id BIGSERIAL` PK; `full_name VARCHAR(200) NOT NULL`; `birth_year SMALLINT` CHECK `BETWEEN 1000 AND 2100`; `country_code CHAR(2)`.
- `book`: `book_id BIGSERIAL` PK; `isbn VARCHAR(17) NOT NULL UNIQUE`; `title VARCHAR(300) NOT NULL`; `author_id BIGINT NOT NULL` FK to `author(author_id)`; `published_year SMALLINT`; `copies_total INTEGER NOT NULL DEFAULT 1` CHECK `>= 0`; `copies_free INTEGER NOT NULL DEFAULT 1` CHECK `>= 0`; constraint `book_free_le_total` CHECK `copies_free <= copies_total`. Index `idx_book_author` on `book(author_id)`.
- `member`: `member_id BIGSERIAL` PK; `email VARCHAR(254) NOT NULL UNIQUE`; `full_name VARCHAR(200) NOT NULL`; `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`; `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`.
- `loan`: `loan_id BIGSERIAL` PK; `book_id BIGINT NOT NULL` FK to `book(book_id)`; `member_id BIGINT NOT NULL` FK to `member(member_id) ON DELETE CASCADE`; `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`; `due_on DATE NOT NULL`; `returned_on DATE`; constraint `loan_due_after_loan` CHECK `due_on > loaned_on`. Index `idx_loan_member` on `loan(member_id)`. Partial index `idx_loan_open` on `loan(due_on)` WHERE `returned_on IS NULL`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (`ON DELETE CASCADE`)

#### Dependencies
- Tables: `author`, `book`, `member`, `loan`
- Indexes: `idx_book_author`, `idx_loan_member`, `idx_loan_open`
- Constraints: `book_free_le_total`, `loan_due_after_loan`
