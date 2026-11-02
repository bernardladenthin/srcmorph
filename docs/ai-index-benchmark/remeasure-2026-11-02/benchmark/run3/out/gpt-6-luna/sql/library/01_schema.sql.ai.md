### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:58:23Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Models a lending library’s authors, books, members, and loans, including book availability and loan-return status.

#### Purpose
- Defines the core PostgreSQL schema for library catalog and circulation data.

#### Type
- Tables; PostgreSQL.

#### Schema
- `author`
  - `author_id BIGSERIAL` — primary key (unnamed in source).
  - `full_name VARCHAR(200) NOT NULL`
  - `birth_year SMALLINT` — CHECK `birth_year BETWEEN 1000 AND 2100` (unnamed).
  - `country_code CHAR(2)`
- `book`
  - `book_id BIGSERIAL` — primary key (unnamed in source).
  - `isbn VARCHAR(17) NOT NULL UNIQUE` (unnamed constraint).
  - `title VARCHAR(300) NOT NULL`
  - `author_id BIGINT NOT NULL` — FK to `author.author_id` (unnamed).
  - `published_year SMALLINT`
  - `copies_total INTEGER NOT NULL DEFAULT 1` — CHECK `copies_total >= 0` (unnamed).
  - `copies_free INTEGER NOT NULL DEFAULT 1` — CHECK `copies_free >= 0` (unnamed).
  - CHECK `book_free_le_total`: `copies_free <= copies_total`.
- `member`
  - `member_id BIGSERIAL` — primary key (unnamed in source).
  - `email VARCHAR(254) NOT NULL UNIQUE` (unnamed constraint).
  - `full_name VARCHAR(200) NOT NULL`
  - `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`
  - `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`
- `loan`
  - `loan_id BIGSERIAL` — primary key (unnamed in source).
  - `book_id BIGINT NOT NULL` — FK to `book.book_id` (unnamed).
  - `member_id BIGINT NOT NULL` — FK to `member.member_id`, `ON DELETE CASCADE` (unnamed).
  - `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`
  - `due_on DATE NOT NULL`
  - `returned_on DATE`
  - CHECK `loan_due_after_loan`: `due_on > loaned_on`.
- Index `idx_book_author` on `book(author_id)`; default method.
- Index `idx_loan_member` on `loan(member_id)`; default method.
- Index `idx_loan_open` on `loan(due_on)`; default method; partial predicate `returned_on IS NULL`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (`ON DELETE CASCADE`)

#### Dependencies
- `author`, `book`, `member`, `loan` tables; `CURRENT_DATE` defaults.
