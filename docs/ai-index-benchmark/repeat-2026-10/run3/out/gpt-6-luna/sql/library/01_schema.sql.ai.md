### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:21:23Z
- G: 
- A: 0.0.0
- X: file
---
> Models authors, books, library members, and loans, including book availability and loan due-date constraints.

#### Purpose
- Defines the core lending-library schema for catalog, membership, and loan records.
- Enforces identifier, uniqueness, availability, and loan-date constraints.

#### Type
- Tables and indexes; PostgreSQL.

#### Schema
- `author`: `author_id BIGSERIAL` (PK), `full_name VARCHAR(200) NOT NULL`, `birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)`, `country_code CHAR(2)`.
- `book`: `book_id BIGSERIAL` (PK), `isbn VARCHAR(17) NOT NULL UNIQUE`, `title VARCHAR(300) NOT NULL`, `author_id BIGINT NOT NULL` (FK), `published_year SMALLINT`, `copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)`, `copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)`, `CHECK (copies_free <= copies_total)` (`book_free_le_total`). Index: `idx_book_author (author_id)`.
- `member`: `member_id BIGSERIAL` (PK), `email VARCHAR(254) NOT NULL UNIQUE`, `full_name VARCHAR(200) NOT NULL`, `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`, `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`.
- `loan`: `loan_id BIGSERIAL` (PK), `book_id BIGINT NOT NULL` (FK), `member_id BIGINT NOT NULL` (FK, `ON DELETE CASCADE`), `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`, `due_on DATE NOT NULL`, `returned_on DATE`, `CHECK (due_on > loaned_on)` (`loan_due_after_loan`). Indexes: `idx_loan_member (member_id)`; partial `idx_loan_open (due_on) WHERE returned_on IS NULL`.

#### Writes
- Creates tables `author`, `book`, `member`, `loan` and indexes `idx_book_author`, `idx_loan_member`, `idx_loan_open`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (`ON DELETE CASCADE`)

#### Dependencies
- `author`, `book`, `member`, and `loan` tables; `idx_book_author`, `idx_loan_member`, and `idx_loan_open` indexes.
