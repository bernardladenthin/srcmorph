### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:21:24Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Models authors, books, library members, and book loans, including copy availability and loan due dates.

#### Purpose
- Defines the core lending-library schema for cataloging books and authors, tracking members, and recording loans.

#### Type
- PostgreSQL tables and indexes.

#### Schema
- `author`
  - `author_id BIGSERIAL` — primary key.
  - `full_name VARCHAR(200) NOT NULL`.
  - `birth_year SMALLINT` — `CHECK (birth_year BETWEEN 1000 AND 2100)`.
  - `country_code CHAR(2)`.
- `book`
  - `book_id BIGSERIAL` — primary key.
  - `isbn VARCHAR(17) NOT NULL UNIQUE`.
  - `title VARCHAR(300) NOT NULL`.
  - `author_id BIGINT NOT NULL` — foreign key to `author.author_id`.
  - `published_year SMALLINT`.
  - `copies_total INTEGER NOT NULL DEFAULT 1` — `CHECK (copies_total >= 0)`.
  - `copies_free INTEGER NOT NULL DEFAULT 1` — `CHECK (copies_free >= 0)`.
  - `book_free_le_total` — `CHECK (copies_free <= copies_total)`.
- `member`
  - `member_id BIGSERIAL` — primary key.
  - `email VARCHAR(254) NOT NULL UNIQUE`.
  - `full_name VARCHAR(200) NOT NULL`.
  - `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`.
  - `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`.
- `loan`
  - `loan_id BIGSERIAL` — primary key.
  - `book_id BIGINT NOT NULL` — foreign key to `book.book_id`.
  - `member_id BIGINT NOT NULL` — foreign key to `member.member_id`, `ON DELETE CASCADE`.
  - `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`.
  - `due_on DATE NOT NULL`.
  - `returned_on DATE`.
  - `loan_due_after_loan` — `CHECK (due_on > loaned_on)`.
- Indexes:
  - `idx_book_author` on `book (author_id)`.
  - `idx_loan_member` on `loan (member_id)`.
  - `idx_loan_open` on `loan (due_on)`, partial predicate `returned_on IS NULL`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (`ON DELETE CASCADE`)

#### Dependencies
- `author`, `book`, `member`, and `loan` are created in this file; foreign keys reference `author`, `book`, and `member`.
