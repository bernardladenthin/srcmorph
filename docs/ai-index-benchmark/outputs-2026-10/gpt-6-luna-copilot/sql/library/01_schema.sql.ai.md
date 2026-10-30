### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:35:52Z
- G: 
- A: 0.0.0
- X: file
---
> Models a lending library’s authors, books, members, and loans, including copy availability and loan due-date rules.

#### Purpose
- Defines core library catalog, member, and loan data.
- Enforces key, reference, availability, and due-date constraints.

#### Type
- PostgreSQL DDL: tables and indexes

#### Schema
- `author`: `author_id BIGSERIAL` (PK), `full_name VARCHAR(200)` (NOT NULL), `birth_year SMALLINT` (CHECK 1000–2100), `country_code CHAR(2)`
- `book`: `book_id BIGSERIAL` (PK), `isbn VARCHAR(17)` (NOT NULL, UNIQUE), `title VARCHAR(300)` (NOT NULL), `author_id BIGINT` (NOT NULL, FK), `published_year SMALLINT`, `copies_total INTEGER` (NOT NULL, DEFAULT 1, CHECK >= 0), `copies_free INTEGER` (NOT NULL, DEFAULT 1, CHECK >= 0); CHECK `copies_free <= copies_total`
- `member`: `member_id BIGSERIAL` (PK), `email VARCHAR(254)` (NOT NULL, UNIQUE), `full_name VARCHAR(200)` (NOT NULL), `joined_on DATE` (NOT NULL, DEFAULT CURRENT_DATE), `is_blocked BOOLEAN` (NOT NULL, DEFAULT FALSE)
- `loan`: `loan_id BIGSERIAL` (PK), `book_id BIGINT` (NOT NULL, FK), `member_id BIGINT` (NOT NULL, FK with ON DELETE CASCADE), `loaned_on DATE` (NOT NULL, DEFAULT CURRENT_DATE), `due_on DATE` (NOT NULL), `returned_on DATE`; CHECK `due_on > loaned_on`
- Indexes: `idx_book_author` on `book(author_id)`; `idx_loan_member` on `loan(member_id)`; partial `idx_loan_open` on `loan(due_on)` WHERE `returned_on IS NULL`

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (ON DELETE CASCADE)

#### Dependencies
- `author`, `book`, `member`, `loan` tables; indexes `idx_book_author`, `idx_loan_member`, `idx_loan_open`
