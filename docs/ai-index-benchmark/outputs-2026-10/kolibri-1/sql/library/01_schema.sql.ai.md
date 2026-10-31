### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:28:19Z
- G: 
- A: 0.0.0
- X: file
---
> A lending library schema modeling authors, books, members, and loans with availability tracking.

#### Purpose
- Core schema for a lending library: authors, books, members, loans.
- Tracks book copies, loan status, and member blocking.

#### Type
- PostgreSQL DDL: tables, indexes.

#### Schema
- author: author_id BIGSERIAL PK, full_name VARCHAR(200) NOT NULL, birth_year SMALLINT CHECK (1000-2100), country_code CHAR(2).
- book: book_id BIGSERIAL PK, isbn VARCHAR(17) NOT NULL UNIQUE, title VARCHAR(300) NOT NULL, author_id BIGINT NOT NULL FK -> author(author_id), published_year SMALLINT, copies_total INTEGER NOT NULL DEFAULT 1 CHECK (>=0), copies_free INTEGER NOT NULL DEFAULT 1 CHECK (>=0), CHECK (copies_free <= copies_total).
- member: member_id BIGSERIAL PK, email VARCHAR(254) NOT NULL UNIQUE, full_name VARCHAR(200) NOT NULL, joined_on DATE NOT NULL DEFAULT CURRENT_DATE, is_blocked BOOLEAN NOT NULL DEFAULT FALSE.
- loan: loan_id BIGSERIAL PK, book_id BIGINT NOT NULL FK -> book(book_id), member_id BIGINT NOT NULL FK -> member(member_id) ON DELETE CASCADE, loaned_on DATE NOT NULL DEFAULT CURRENT_DATE, due_on DATE NOT NULL, returned_on DATE, CHECK (due_on > loaned_on).
- Indexes: idx_book_author on book(author_id), idx_loan_member on loan(member_id), idx_loan_open on loan(due_on) WHERE returned_on IS NULL.

#### Reads
- book: author_id, isbn, title, published_year, copies_total, copies_free.
- loan: book_id, member_id, loaned_on, due_on, returned_on.
- member: member_id, email, full_name, joined_on, is_blocked.
- author: author_id, full_name, birth_year, country_code.

#### Writes
- book: book_id, isbn, title, author_id, published_year, copies_total, copies_free.
- member: member_id, email, full_name, joined_on, is_blocked.
- loan: loan_id, book_id, member_id, loaned_on, due_on, returned_on.

#### Relationships
- book.author_id -> author.author_id.
- loan.book_id -> book.book_id.
- loan.member_id -> member.member_id ON DELETE CASCADE.

#### Routine logic
- No procedures, functions, or triggers present.

#### Dependencies
- author, book, member, loan.
