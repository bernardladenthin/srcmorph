### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:32:17Z
- G: 
- A: 0.0.0
- X: file
---
> Models authors, books, library members, and loans, with constraints for book availability, loan dates, and member-linked records.

#### Purpose
- Defines the core lending-library schema for cataloging books and authors, tracking members, and recording loans.
- Enforces identifier, availability, and loan-date constraints; indexes common author and loan lookups.

#### Type
- Tables and indexes; PostgreSQL.

#### Schema
- `author`
  - `author_id BIGSERIAL` — primary key (constraint name not declared).
  - `full_name VARCHAR(200) NOT NULL`.
  - `birth_year SMALLINT` — unnamed `CHECK (birth_year BETWEEN 1000 AND 2100)`.
  - `country_code CHAR(2)`.
- `book`
  - `book_id BIGSERIAL` — primary key (constraint name not declared).
  - `isbn VARCHAR(17) NOT NULL UNIQUE` (constraint name not declared).
  - `title VARCHAR(300) NOT NULL`.
  - `author_id BIGINT NOT NULL` — FK to `author.author_id` (constraint name not declared).
  - `published_year SMALLINT`.
  - `copies_total INTEGER NOT NULL DEFAULT 1` — unnamed `CHECK (copies_total >= 0)`.
  - `copies_free INTEGER NOT NULL DEFAULT 1` — unnamed `CHECK (copies_free >= 0)`.
  - `book_free_le_total` — `CHECK (copies_free <= copies_total)`.
  - Index: `idx_book_author (author_id)`.
- `member`
  - `member_id BIGSERIAL` — primary key (constraint name not declared).
  - `email VARCHAR(254) NOT NULL UNIQUE` (constraint name not declared).
  - `full_name VARCHAR(200) NOT NULL`.
  - `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`.
  - `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`.
- `loan`
  - `loan_id BIGSERIAL` — primary key (constraint name not declared).
  - `book_id BIGINT NOT NULL` — FK to `book.book_id` (constraint name not declared).
  - `member_id BIGINT NOT NULL` — FK to `member.member_id`, `ON DELETE CASCADE` (constraint name not declared).
  - `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`.
  - `due_on DATE NOT NULL`.
  - `returned_on DATE`.
  - `loan_due_after_loan` — `CHECK (due_on > loaned_on)`.
  - Index: `idx_loan_member (member_id)`.
  - Partial index: `idx_loan_open (due_on) WHERE returned_on IS NULL`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (member deletion cascades to loans)
