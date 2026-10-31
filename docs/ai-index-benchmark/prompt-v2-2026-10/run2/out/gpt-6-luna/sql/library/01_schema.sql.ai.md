### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:45:46Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the core lending-library data model for authors, books, members, and loans, including copy availability and loan tracking.

#### Purpose
- Stores library authors, catalogued books, members, and book loans.
- Enforces identifiers, required fields, copy-count limits, and loan due-date validity.

#### Type
- PostgreSQL schema DDL; creates tables and indexes.

#### Schema
- `author`
  - `author_id BIGSERIAL` — primary key (`author_pkey`).
  - `full_name VARCHAR(200) NOT NULL`.
  - `birth_year SMALLINT` — CHECK (`author_birth_year_check`): between 1000 and 2100.
  - `country_code CHAR(2)`.
- `book`
  - `book_id BIGSERIAL` — primary key (`book_pkey`).
  - `isbn VARCHAR(17) NOT NULL UNIQUE` — unique constraint (`book_isbn_key`).
  - `title VARCHAR(300) NOT NULL`.
  - `author_id BIGINT NOT NULL` — FK (`book_author_id_fkey`) to `author.author_id`.
  - `published_year SMALLINT`.
  - `copies_total INTEGER NOT NULL DEFAULT 1` — CHECK (`book_copies_total_check`): nonnegative.
  - `copies_free INTEGER NOT NULL DEFAULT 1` — CHECK (`book_copies_free_check`): nonnegative.
  - CHECK (`book_free_le_total`): `copies_free <= copies_total`.
- `member`
  - `member_id BIGSERIAL` — primary key (`member_pkey`).
  - `email VARCHAR(254) NOT NULL UNIQUE` — unique constraint (`member_email_key`).
  - `full_name VARCHAR(200) NOT NULL`.
  - `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`.
  - `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`.
- `loan`
  - `loan_id BIGSERIAL` — primary key (`loan_pkey`).
  - `book_id BIGINT NOT NULL` — FK (`loan_book_id_fkey`) to `book.book_id`.
  - `member_id BIGINT NOT NULL` — FK (`loan_member_id_fkey`) to `member.member_id`, `ON DELETE CASCADE`.
  - `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`.
  - `due_on DATE NOT NULL`.
  - `returned_on DATE`.
  - CHECK (`loan_due_after_loan`): `due_on > loaned_on`.
- Indexes: `idx_book_author` on `book(author_id)`; `idx_loan_member` on `loan(member_id)`; partial index `idx_loan_open` on `loan(due_on)` where `returned_on IS NULL`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (`ON DELETE CASCADE`)
