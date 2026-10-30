### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:31:33Z
- G: 
- A: 0.0.0
- X: file
---
> Relational schema for a lending library tracking authors, books with copy inventory, members, and loans.

#### Purpose
- Core DDL for a PostgreSQL lending library: catalog of authors and books, member registry, and loan transactions.
- Enforces copy inventory bounds (`copies_free <= copies_total`) and loan validity (`due_on > loaned_on`).

#### Type
- Set of tables and indexes (DDL), dialect: PostgreSQL (`BIGSERIAL`, `BOOLEAN`, partial index `WHERE`).

#### Schema
- **author**
  - `author_id BIGSERIAL` PK
  - `full_name VARCHAR(200)` NOT NULL
  - `birth_year SMALLINT` CHECK `BETWEEN 1000 AND 2100`
  - `country_code CHAR(2)`
- **book**
  - `book_id BIGSERIAL` PK
  - `isbn VARCHAR(17)` NOT NULL UNIQUE
  - `title VARCHAR(300)` NOT NULL
  - `author_id BIGINT` NOT NULL FK -> `author.author_id`
  - `published_year SMALLINT`
  - `copies_total INTEGER` NOT NULL DEFAULT 1, CHECK `>= 0`
  - `copies_free INTEGER` NOT NULL DEFAULT 1, CHECK `>= 0`
  - CONSTRAINT `book_free_le_total` CHECK `copies_free <= copies_total`
- **member**
  - `member_id BIGSERIAL` PK
  - `email VARCHAR(254)` NOT NULL UNIQUE
  - `full_name VARCHAR(200)` NOT NULL
  - `joined_on DATE` NOT NULL DEFAULT `CURRENT_DATE`
  - `is_blocked BOOLEAN` NOT NULL DEFAULT `FALSE`
- **loan**
  - `loan_id BIGSERIAL` PK
  - `book_id BIGINT` NOT NULL FK -> `book.book_id`
  - `member_id BIGINT` NOT NULL FK -> `member.member_id` ON DELETE CASCADE
  - `loaned_on DATE` NOT NULL DEFAULT `CURRENT_DATE`
  - `due_on DATE` NOT NULL
  - `returned_on DATE` (NULL = open loan)
  - CONSTRAINT `loan_due_after_loan` CHECK `due_on > loaned_on`
- **Indexes**
  - `idx_book_author` on `book(author_id)`
  - `idx_loan_member` on `loan(member_id)`
  - `idx_loan_open` on `loan(due_on)` WHERE `returned_on IS NULL` (partial, open loans)

#### Reads
- None (DDL only; no SELECT sources).

#### Writes
- Creates tables: `author`, `book`, `member`, `loan`.
- Creates indexes on `book.author_id`, `loan.member_id`, `loan.due_on` (partial).

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (ON DELETE CASCADE)

#### Dependencies
- Tables: `author`, `book`, `member`, `loan`
- Indexes: `idx_book_author`, `idx_loan_member`, `idx_loan_open`
- Built-in: `CURRENT_DATE`, sequences implied by `BIGSERIAL` (`author_author_id_seq`, `book_book_id_seq`, `member_member_id_seq`, `loan_loan_id_seq`)
