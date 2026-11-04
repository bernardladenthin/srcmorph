### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:16:44Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Defines the core tables for a library system: authors, books, members, and loans, with constraints and indexes.

#### Purpose
- Stores library catalog and member data.
- Tracks book loans and availability.

#### Type
- Table: `author`
- Table: `book`
- Table: `member`
- Table: `loan`
- Index: `idx_book_author`
- Index: `idx_loan_member`
- Index: `idx_loan_open`

#### Schema
- **author**
  - `author_id BIGSERIAL PRIMARY KEY`
  - `full_name VARCHAR(200) NOT NULL`
  - `birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)`
  - `country_code CHAR(2)`
- **book**
  - `book_id BIGSERIAL PRIMARY KEY`
  - `isbn VARCHAR(17) NOT NULL UNIQUE`
  - `title VARCHAR(300) NOT NULL`
  - `author_id BIGINT NOT NULL REFERENCES author (author_id)`
  - `published_year SMALLINT`
  - `copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)`
  - `copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)`
  - `CONSTRAINT book_free_le_total CHECK (copies_free <= copies_total)`
- **member**
  - `member_id BIGSERIAL PRIMARY KEY`
  - `email VARCHAR(254) NOT NULL UNIQUE`
  - `full_name VARCHAR(200) NOT NULL`
  - `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`
  - `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`
- **loan**
  - `loan_id BIGSERIAL PRIMARY KEY`
  - `book_id BIGINT NOT NULL REFERENCES book (book_id)`
  - `member_id BIGINT NOT NULL REFERENCES member (member_id) ON DELETE CASCADE`
  - `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`
  - `due_on DATE NOT NULL`
  - `returned_on DATE`
  - `CONSTRAINT loan_due_after_loan CHECK (due_on > loaned_on)`
- **Indexes**
  - `idx_book_author` on `book (author_id)`
  - `idx_loan_member` on `loan (member_id)`
  - `idx_loan_open` on `loan (due_on)` WHERE `returned_on IS NULL`

#### Reads
- `book.author_id` referenced by `loan.book_id`.
- `member.member_id` referenced by `loan.member_id`.

#### Writes
- Inserts/updates/deletes on `author`, `book`, `member`, `loan`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- `author`, `book`, `member`, `loan` tables referenced by foreign keys and indexes.
