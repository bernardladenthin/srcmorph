### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:32:49Z
- G: 
- A: 0.0.0
- X: file
---
> This file defines the core database schema for a lending library, modeling authors, books, members, and book loans (PostgreSQL).

#### Type
- Table / schema DDL (PostgreSQL, `BIGSERIAL` / `CHECK` / partial index syntax).

#### Schema
- `author` — library authors
  - `author_id BIGSERIAL PRIMARY KEY`
  - `full_name VARCHAR(200) NOT NULL`
  - `birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)`
  - `country_code CHAR(2)`
- `book` — library books with copy counts
  - `book_id BIGSERIAL PRIMARY KEY`
  - `isbn VARCHAR(17) NOT NULL UNIQUE`
  - `title VARCHAR(300) NOT NULL`
  - `author_id BIGINT NOT NULL REFERENCES author (author_id)`
  - `published_year SMALLINT`
  - `copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)`
  - `copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)`
  - `CONSTRAINT book_free_le_total CHECK (copies_free <= copies_total)`
- `member` — library patrons
  - `member_id BIGSERIAL PRIMARY KEY`
  - `email VARCHAR(254) NOT NULL UNIQUE`
  - `full_name VARCHAR(200) NOT NULL`
  - `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`
  - `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`
- `loan` — book loans / transactions
  - `loan_id BIGSERIAL PRIMARY KEY`
  - `book_id BIGINT NOT NULL REFERENCES book (book_id)`
  - `member_id BIGINT NOT NULL REFERENCES member (member_id) ON DELETE CASCADE`
  - `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`
  - `due_on DATE NOT NULL`
  - `returned_on DATE`
  - `CONSTRAINT loan_due_after_loan CHECK (due_on > loaned_on)`

#### Reads
- None (schema-only DDL; no SQL queries).

#### Writes
- Creates tables `author`, `book`, `member`, `loan`.
- Creates indexes on `book` and `loan`.

#### Relationships
- `book.author_id -> author.author_id` (FK, NOT NULL)
- `loan.book_id -> book.book_id` (FK, NOT NULL)
- `loan.member_id -> member.member_id` (FK, NOT NULL, ON DELETE CASCADE)

#### Dependencies
- `author` (referenced by `book.author_id`)
- `book` (referenced by `loan.book_id`)
- `member` (referenced by `loan.member_id`)

#### Indexes
- `idx_book_author` on `book (author_id)`
- `idx_loan_member` on `loan (member_id)`
- `idx_loan_open` partial index on `loan (due_on) WHERE returned_on IS NULL`
