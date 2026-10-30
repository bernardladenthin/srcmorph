### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:45:50Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library records authors, books, members, and loans with inventory and due-date tracking.

#### Purpose
- Define a PostgreSQL library schema for cataloging authors and books, managing member accounts, and tracking borrow/return status.
- Enforce inventory availability and due-date rules for loans.

#### Type
- PostgreSQL schema DDL.
- Tables: author, book, member, loan.
- Indexes: idx_book_author, idx_loan_member, idx_loan_open.

#### Schema
- author
  - author_id BIGSERIAL PK
  - full_name VARCHAR(200) NOT NULL
  - birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)
  - country_code CHAR(2)
- book
  - book_id BIGSERIAL PK
  - isbn VARCHAR(17) NOT NULL UNIQUE
  - title VARCHAR(300) NOT NULL
  - author_id BIGINT NOT NULL FK -> author.author_id
  - published_year SMALLINT
  - copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)
  - copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)
  - CHECK (copies_free <= copies_total)
  - Index: idx_book_author ON book(author_id)
- member
  - member_id BIGSERIAL PK
  - email VARCHAR(254) NOT NULL UNIQUE
  - full_name VARCHAR(200) NOT NULL
  - joined_on DATE NOT NULL DEFAULT CURRENT_DATE
  - is_blocked BOOLEAN NOT NULL DEFAULT FALSE
- loan
  - loan_id BIGSERIAL PK
  - book_id BIGINT NOT NULL FK -> book.book_id
  - member_id BIGINT NOT NULL FK -> member.member_id ON DELETE CASCADE
  - loaned_on DATE NOT NULL DEFAULT CURRENT_DATE
  - due_on DATE NOT NULL
  - returned_on DATE
  - CHECK (due_on > loaned_on)
  - Index: idx_loan_member ON loan(member_id)
  - Index: idx_loan_open ON loan(due_on) WHERE returned_on IS NULL

#### Reads
- None; file contains schema creation statements only.

#### Writes
- None; file contains CREATE TABLE and CREATE INDEX statements only.

#### Relationships
- book.author_id -> author.author_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id
- book.author_id JOIN author.author_id
- loan.book_id JOIN book.book_id
- loan.member_id JOIN member.member_id

#### Dependencies
- author
- book
- member
- PostgreSQL BIGSERIAL sequence-backed identity semantics for author.author_id, book.book_id, member.member_id, loan.loan_id
