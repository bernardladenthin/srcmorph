### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:22:55Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library core model for authors, books, members, and book loans with inventory counts.
#### Purpose
- Defines lending library core schema: author, book, member, loan inventory and circulation.
- Enforces copy availability, loan dates, uniqueness of isbn and member email.
#### Type
- table, PostgreSQL DDL: BIGSERIAL, CHECK, partial index, ON DELETE CASCADE, REFERENCES.
#### Schema
- author: `author_id BIGSERIAL` PK, `full_name VARCHAR(200)` NOT NULL, `birth_year SMALLINT` CHECK 1000-2100, `country_code CHAR(2)`.
- book: `book_id BIGSERIAL` PK, `isbn VARCHAR(17)` NOT NULL UNIQUE, `title VARCHAR(300)` NOT NULL, `author_id BIGINT` NOT NULL FK, `published_year SMALLINT`, `copies_total INTEGER` NOT NULL DEFAULT 1 CHECK >=0, `copies_free INTEGER` NOT NULL DEFAULT 1 CHECK >=0, CONSTRAINT `book_free_le_total` copies_free <= copies_total.
- member: `member_id BIGSERIAL` PK, `email VARCHAR(254)` NOT NULL UNIQUE, `full_name VARCHAR(200)` NOT NULL, `joined_on DATE` NOT NULL DEFAULT CURRENT_DATE, `is_blocked BOOLEAN` NOT NULL DEFAULT FALSE.
- loan: `loan_id BIGSERIAL` PK, `book_id BIGINT` NOT NULL FK, `member_id BIGINT` NOT NULL FK ON DELETE CASCADE, `loaned_on DATE` NOT NULL DEFAULT CURRENT_DATE, `due_on DATE` NOT NULL, `returned_on DATE` nullable, CONSTRAINT `loan_due_after_loan` due_on > loaned_on.
- indexes: `idx_book_author` ON book(author_id), `idx_loan_member` ON loan(member_id), `idx_loan_open` ON loan(due_on) WHERE returned_on IS NULL.
#### Relationships
- book.author_id -> author.author_id.
- loan.book_id -> book.book_id.
- loan.member_id -> member.member_id ON DELETE CASCADE.
