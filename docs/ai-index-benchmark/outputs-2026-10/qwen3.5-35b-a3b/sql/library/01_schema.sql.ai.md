### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-28T21:08:38Z
- T: 2026-10-28T23:28:31Z
- G: 
- A: 0.0.0
- X: file
---
> Defines core relational schema for a lending library tracking authors, books, members, and loan transactions.

#### Purpose
*   Establishes persistent storage for library inventory and circulation data.
*   Enforces data integrity via constraints and foreign keys.

#### Type
*   PostgreSQL DDL (tables, indexes).

#### Schema
*   `author`: `author_id BIGSERIAL PRIMARY KEY`, `full_name VARCHAR(200) NOT NULL`, `birth_year SMALLINT CHECK (1000-2100)`, `country_code CHAR(2)`.
*   `book`: `book_id BIGSERIAL PRIMARY KEY`, `isbn VARCHAR(17) NOT NULL UNIQUE`, `title VARCHAR(300) NOT NULL`, `author_id BIGINT NOT NULL`, `published_year SMALLINT`, `copies_total INTEGER NOT NULL DEFAULT 1 CHECK >= 0`, `copies_free INTEGER NOT NULL DEFAULT 1 CHECK >= 0`, `CONSTRAINT book_free_le_total`.
*   `member`: `member_id BIGSERIAL PRIMARY KEY`, `email VARCHAR(254) NOT NULL UNIQUE`, `full_name VARCHAR(200) NOT NULL`, `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`, `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`.
*   `loan`: `loan_id BIGSERIAL PRIMARY KEY`, `book_id BIGINT NOT NULL`, `member_id BIGINT NOT NULL`, `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`, `due_on DATE NOT NULL`, `returned_on DATE`, `CONSTRAINT loan_due_after_loan`.
*   Indexes: `idx_book_author ON book (author_id)`, `idx_loan_member ON loan (member_id)`, `idx_loan_open ON loan (due_on) WHERE returned_on IS NULL`.

#### Relationships
*   `book.author_id -> author.author_id`
*   `loan.book_id -> book.book_id`
*   `loan.member_id -> member.member_id`
