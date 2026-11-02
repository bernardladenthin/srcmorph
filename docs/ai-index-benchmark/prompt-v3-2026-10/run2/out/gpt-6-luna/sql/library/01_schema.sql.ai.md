### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:11:49Z
- G: 
- A: 0.0.0
- X: file
---
> Models authors, books, library members, and book loans, with availability, due-date, and membership constraints.

#### Purpose
- Defines the core lending-library schema for catalog, authors, members, and loans.
- Adds indexes for author lookup and member/open-loan lookup.

#### Type
- PostgreSQL tables and indexes.

#### Schema
- `author`: `author_id BIGSERIAL` (PK), `full_name VARCHAR(200)` (NOT NULL), `birth_year SMALLINT` (CHECK `birth_year BETWEEN 1000 AND 2100`), `country_code CHAR(2)`.
- `book`: `book_id BIGSERIAL` (PK), `isbn VARCHAR(17)` (NOT NULL, UNIQUE), `title VARCHAR(300)` (NOT NULL), `author_id BIGINT` (NOT NULL, FK → `author.author_id`), `published_year SMALLINT`, `copies_total INTEGER` (NOT NULL, DEFAULT 1, CHECK `copies_total >= 0`), `copies_free INTEGER` (NOT NULL, DEFAULT 1, CHECK `copies_free >= 0`); named constraint `book_free_le_total` checks `copies_free <= copies_total`.
- `member`: `member_id BIGSERIAL` (PK), `email VARCHAR(254)` (NOT NULL, UNIQUE), `full_name VARCHAR(200)` (NOT NULL), `joined_on DATE` (NOT NULL, DEFAULT `CURRENT_DATE`), `is_blocked BOOLEAN` (NOT NULL, DEFAULT `FALSE`).
- `loan`: `loan_id BIGSERIAL` (PK), `book_id BIGINT` (NOT NULL, FK → `book.book_id`), `member_id BIGINT` (NOT NULL, FK → `member.member_id`, ON DELETE CASCADE), `loaned_on DATE` (NOT NULL, DEFAULT `CURRENT_DATE`), `due_on DATE` (NOT NULL), `returned_on DATE`; named constraint `loan_due_after_loan` checks `due_on > loaned_on`.
- Index `idx_book_author`: table `book`, column `author_id`, default method.
- Index `idx_loan_member`: table `loan`, column `member_id`, default method.
- Index `idx_loan_open`: table `loan`, column `due_on`, default method; partial predicate `returned_on IS NULL`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (ON DELETE CASCADE)

#### Dependencies
- `author`, `book`, `member`, `loan` tables.
