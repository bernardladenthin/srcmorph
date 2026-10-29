### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-28T21:08:38Z
- T: 2026-10-28T21:33:23Z
- G: 
- A: 0.0.0
- X: file
---
> Core data model for a lending library system managing authors, books, members, and borrowing transactions.

#### Purpose
* Define the relational structure for tracking library inventory, member registration, and loan history.

#### Type
Table (PostgreSQL).

#### Schema
* `author`: `author_id BIGSERIAL PK`, `full_name VARCHAR(200) NOT NULL`, `birth_year SMALLINT CHECK (1000-2100)`, `country_code CHAR(2)`.
* `book`: `book_id BIGSERIAL PK`, `isbn VARCHAR(17) NOT NULL UNIQUE`, `title VARCHAR(300) NOT NULL`, `author_id BIGINT NOT NULL FK`, `published_year SMALLINT`, `copies_total INTEGER NOT NULL DEFAULT 1 CHECK (>=0)`, `copies_free INTEGER NOT NULL DEFAULT 1 CHECK (>=0)`, `CONSTRAINT book_free_le_total (copies_free <= copies_total)`. Index: `idx_book_author` on `author_id`.
* `member`: `member_id BIGSERIAL PK`, `email VARCHAR(254) NOT NULL UNIQUE`, `full_name VARCHAR(200) NOT NULL`, `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`, `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`.
* `loan`: `loan_id BIGSERIAL PK`, `book_id BIGINT NOT NULL FK`, `member_id BIGINT NOT NULL FK`, `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`, `due_on DATE NOT NULL`, `returned_on DATE`, `CONSTRAINT loan_due_after_loan (due_on > loaned_on)`. Indexes: `idx_loan_member` on `member_id`, `idx_loan_open` on `due_on` where `returned_on IS NULL`.

#### Relationships
* `book.author_id -> author.author_id`
* `loan.book_id -> book.book_id`
* `loan.member_id -> member.member_id` (ON DELETE CASCADE)
