### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:37:35Z
- G: 
- A: 0.0.0
- X: file
---
> Models a lending library’s authors, books, members, and loans, including book-copy availability and loan due dates.

#### Purpose
- Defines the core lending-library schema for catalog, membership, and loan records.
- Enforces identifiers, required fields, availability limits, and loan-date validity.

#### Type
- PostgreSQL tables and indexes.

#### Schema
- `author`
  - `author_id BIGSERIAL` — primary key (unnamed constraint)
  - `full_name VARCHAR(200) NOT NULL`
  - `birth_year SMALLINT` — unnamed `CHECK (birth_year BETWEEN 1000 AND 2100)`
  - `country_code CHAR(2)`
- `book`
  - `book_id BIGSERIAL` — primary key (unnamed constraint)
  - `isbn VARCHAR(17) NOT NULL UNIQUE` (unnamed constraint)
  - `title VARCHAR(300) NOT NULL`
  - `author_id BIGINT NOT NULL` — FK to `author.author_id` (unnamed constraint)
  - `published_year SMALLINT`
  - `copies_total INTEGER NOT NULL DEFAULT 1` — unnamed `CHECK (copies_total >= 0)`
  - `copies_free INTEGER NOT NULL DEFAULT 1` — unnamed `CHECK (copies_free >= 0)`
  - `book_free_le_total CHECK (copies_free <= copies_total)`
- `member`
  - `member_id BIGSERIAL` — primary key (unnamed constraint)
  - `email VARCHAR(254) NOT NULL UNIQUE` (unnamed constraint)
  - `full_name VARCHAR(200) NOT NULL`
  - `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`
  - `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`
- `loan`
  - `loan_id BIGSERIAL` — primary key (unnamed constraint)
  - `book_id BIGINT NOT NULL` — FK to `book.book_id` (unnamed constraint)
  - `member_id BIGINT NOT NULL` — FK to `member.member_id ON DELETE CASCADE` (unnamed constraint)
  - `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`
  - `due_on DATE NOT NULL`
  - `returned_on DATE`
  - `loan_due_after_loan CHECK (due_on > loaned_on)`
- Indexes:
  - `idx_book_author` on `book (author_id)`, default method
  - `idx_loan_member` on `loan (member_id)`, default method
  - `idx_loan_open` on `loan (due_on)`, default method; partial predicate `returned_on IS NULL`

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (delete cascades)

#### Dependencies
- `book` references `author`.
- `loan` references `book` and `member`.
