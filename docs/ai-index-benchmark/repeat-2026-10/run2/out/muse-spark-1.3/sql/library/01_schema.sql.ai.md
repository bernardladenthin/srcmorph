### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:16:33Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library core data model for authors, books, members, and book loans.
#### Purpose
- Define core lending-library schema: author, book, member, loan entities
- Enforce book inventory counts, loan due-date rules, member identity
#### Type
- table DDL, PostgreSQL dialect: BIGSERIAL, CHECK, partial index with WHERE
#### Schema
- author: `author_id BIGSERIAL` PK, `full_name VARCHAR(200)` NOT NULL, `birth_year SMALLINT` CHECK 1000-2100, `country_code CHAR(2)`
- book: `book_id BIGSERIAL` PK, `isbn VARCHAR(17)` NOT NULL UNIQUE, `title VARCHAR(300)` NOT NULL, `author_id BIGINT` NOT NULL FK, `published_year SMALLINT`, `copies_total INTEGER` NOT NULL DEFAULT 1 CHECK >=0, `copies_free INTEGER` NOT NULL DEFAULT 1 CHECK >=0, CONSTRAINT `book_free_le_total` CHECK copies_free <= copies_total
- member: `member_id BIGSERIAL` PK, `email VARCHAR(254)` NOT NULL UNIQUE, `full_name VARCHAR(200)` NOT NULL, `joined_on DATE` NOT NULL DEFAULT CURRENT_DATE, `is_blocked BOOLEAN` NOT NULL DEFAULT FALSE
- loan: `loan_id BIGSERIAL` PK, `book_id BIGINT` NOT NULL FK, `member_id BIGINT` NOT NULL FK ON DELETE CASCADE, `loaned_on DATE` NOT NULL DEFAULT CURRENT_DATE, `due_on DATE` NOT NULL, `returned_on DATE` NULL, CONSTRAINT `loan_due_after_loan` CHECK due_on > loaned_on
- indexes: `idx_book_author` ON book(author_id), `idx_loan_member` ON loan(member_id), `idx_loan_open` ON loan(due_on) WHERE returned_on IS NULL partial index for open loans
#### Relationships
- book.author_id -> author.author_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id ON DELETE CASCADE
#### Dependencies
- author, book, member, loan via REFERENCES FKs
