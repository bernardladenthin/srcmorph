### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:35:32Z
- G: 
- A: 0.0.0
- X: file
---
> Core DDL for a lending library: authors, books with copy counts, members, and loans tracking due and return dates.

#### Purpose
- Defines the core relational schema of a library lending system (PostgreSQL).
- Models authors, catalogued books with available-copy inventory, library members, and book loans.

#### Type
- Schema DDL script (CREATE TABLE / CREATE INDEX), PostgreSQL dialect (BIGSERIAL, CHECK, partial index with WHERE).

#### Schema
- **author**
  - `author_id BIGSERIAL` PK
  - `full_name VARCHAR(200)` NOT NULL
  - `birth_year SMALLINT` CHECK BETWEEN 1000 AND 2100
  - `country_code CHAR(2)`
- **book**
  - `book_id BIGSERIAL` PK
  - `isbn VARCHAR(17)` NOT NULL UNIQUE
  - `title VARCHAR(300)` NOT NULL
  - `author_id BIGINT` NOT NULL, FK -> author.author_id
  - `published_year SMALLINT`
  - `copies_total INTEGER` NOT NULL DEFAULT 1, CHECK >= 0
  - `copies_free INTEGER` NOT NULL DEFAULT 1, CHECK >= 0
  - CONSTRAINT `book_free_le_total` CHECK copies_free <= copies_total
- **member**
  - `member_id BIGSERIAL` PK
  - `email VARCHAR(254)` NOT NULL UNIQUE
  - `full_name VARCHAR(200)` NOT NULL
  - `joined_on DATE` NOT NULL DEFAULT CURRENT_DATE
  - `is_blocked BOOLEAN` NOT NULL DEFAULT FALSE
- **loan**
  - `loan_id BIGSERIAL` PK
  - `book_id BIGINT` NOT NULL, FK -> book.book_id
  - `member_id BIGINT` NOT NULL, FK -> member.member_id ON DELETE CASCADE
  - `loaned_on DATE` NOT NULL DEFAULT CURRENT_DATE
  - `due_on DATE` NOT NULL
  - `returned_on DATE` (nullable; NULL = open loan)
  - CONSTRAINT `loan_due_after_loan` CHECK due_on > loaned_on
- **Indexes**
  - `idx_book_author` on book(author_id)
  - `idx_loan_member` on loan(member_id)
  - `idx_loan_open` on loan(due_on) WHERE returned_on IS NULL (partial index for open loans)

#### Reads
- None (DDL only; no SELECT statements).

#### Writes
- Creates tables: author, book, member, loan.
- Creates indexes: idx_book_author, idx_loan_member, idx_loan_open.

#### Relationships
- book.author_id -> author.author_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id (ON DELETE CASCADE)

#### Routine logic
- Not applicable (no procedures, functions, or triggers).

#### Dependencies
- Tables: author, book, member, loan.
- Indexes: idx_book_author, idx_loan_member, idx_loan_open.
- Built-in: CURRENT_DATE, FALSE, BIGSERIAL sequences (implicit).
