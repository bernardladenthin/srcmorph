### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:48:33Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Models library authors, books, members, and loans, including availability counts and overdue-loan tracking.

#### Purpose
- Defines the core lending-library schema for catalog, membership, and loan records.
- Enforces book availability, loan dates, and author/book/member references.

#### Type
- PostgreSQL tables: `author`, `book`, `member`, `loan`.

#### Schema
- `author`: `author_id BIGSERIAL` (PK); `full_name VARCHAR(200) NOT NULL`; `birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)`; `country_code CHAR(2)`.
- `book`: `book_id BIGSERIAL` (PK); `isbn VARCHAR(17) NOT NULL UNIQUE`; `title VARCHAR(300) NOT NULL`; `author_id BIGINT NOT NULL` (FK); `published_year SMALLINT`; `copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)`; `copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)`; named constraint `book_free_le_total CHECK (copies_free <= copies_total)`.
- `member`: `member_id BIGSERIAL` (PK); `email VARCHAR(254) NOT NULL UNIQUE`; `full_name VARCHAR(200) NOT NULL`; `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`; `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`.
- `loan`: `loan_id BIGSERIAL` (PK); `book_id BIGINT NOT NULL` (FK); `member_id BIGINT NOT NULL` (FK, `ON DELETE CASCADE`); `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`; `due_on DATE NOT NULL`; `returned_on DATE`; named constraint `loan_due_after_loan CHECK (due_on > loaned_on)`.
- Index `idx_book_author`: `book(author_id)`, default method.
- Index `idx_loan_member`: `loan(member_id)`, default method.
- Index `idx_loan_open`: `loan(due_on)`, default method, predicate `returned_on IS NULL`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (cascade on member deletion)
