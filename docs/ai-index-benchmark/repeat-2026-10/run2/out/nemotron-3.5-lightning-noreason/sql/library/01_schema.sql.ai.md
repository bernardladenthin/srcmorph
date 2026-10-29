### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:00Z
- G: 
- A: 0.0.0
- X: file
---
> A core schema for a lending library that models authors, books, members, and loans with integrity constraints and indexes.

#### Purpose
- Defines the relational structure for a lending library system.
- Enforces business rules around book availability, member registration, and loan tracking.

#### Type
PostgreSQL schema (tables, indexes, constraints).

#### Schema
- **author**: author_id BIGSERIAL PK, full_name VARCHAR(200) NOT NULL, birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100), country_code CHAR(2)
- **book**: book_id BIGSERIAL PK, isbn VARCHAR(17) NOT NULL UNIQUE, title VARCHAR(300) NOT NULL, author_id BIGINT NOT NULL REFERENCES author (author_id), published_year SMALLINT, copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0), copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0), CONSTRAINT book_free_le_total CHECK (copies_free <= copies_total)
- **member**: member_id BIGSERIAL PK, email VARCHAR(254) NOT NULL UNIQUE, full_name VARCHAR(200) NOT NULL, joined_on DATE NOT NULL DEFAULT CURRENT_DATE, is_blocked BOOLEAN NOT NULL DEFAULT FALSE
- **loan**: loan_id BIGSERIAL PK, book_id BIGINT NOT NULL REFERENCES book (book_id), member_id BIGINT NOT NULL REFERENCES member (member_id) ON DELETE CASCADE, loaned_on DATE NOT NULL DEFAULT CURRENT_DATE, due_on DATE NOT NULL, returned_on DATE, CONSTRAINT loan_due_after_loan CHECK (due_on > loaned_on)
- **Indexes**: idx_book_author on book (author_id), idx_loan_member on loan (member_id), idx_loan_open on loan (due_on) WHERE returned_on IS NULL

#### Reads
- author (author_id) via book.author_id FK
- member (member_id) via loan.member_id FK

#### Writes
- author: inserted via author table
- book: inserted via book table; updated when copies_free/copies_total change
- member: inserted via member table
- loan: inserted via loan table; book_id and member_id FK references enforced

#### Relationships
- book.author_id -> author.author_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id ON DELETE CASCADE

#### Dependencies
- author, book, member tables referenced by FK constraints
- Sequences: author_id, book_id, member_id, loan_id (BIGSERIAL)
