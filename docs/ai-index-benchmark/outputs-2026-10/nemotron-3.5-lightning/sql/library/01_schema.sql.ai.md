### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-28T21:08:38Z
- T: 2026-10-28T23:05:21Z
- G: 
- A: 0.0.0
- X: file
---
> This schema models a lending library's core data: authors, books, members, and loans.

#### Purpose
- Core schema for a lending library managing authors, books, members, and loans.

#### Type
PostgreSQL

#### Schema
- author.author_id BIGSERIAL PRIMARY KEY
- author.full_name VARCHAR(200) NOT NULL
- author.birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)
- author.country_code CHAR(2)
- book.book_id BIGSERIAL PRIMARY KEY
- book.isbn VARCHAR(17) NOT NULL UNIQUE
- book.title VARCHAR(300) NOT NULL
- book.author_id BIGINT NOT NULL REFERENCES author (author_id)
- book.published_year SMALLINT
- book.copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)
- book.copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)
- book CONSTRAINT book_free_le_total CHECK (copies_free <= copies_total)
- member.member_id BIGSERIAL PRIMARY KEY
- member.email VARCHAR(254) NOT NULL UNIQUE
- member.full_name VARCHAR(200) NOT NULL
- member.joined_on DATE NOT NULL DEFAULT CURRENT_DATE
- member.is_blocked BOOLEAN NOT NULL DEFAULT FALSE
- loan.loan_id BIGSERIAL PRIMARY KEY
- loan.book_id BIGINT NOT NULL REFERENCES book (book_id)
- loan.member_id BIGINT NOT NULL REFERENCES member (member_id) ON DELETE CASCADE
- loan.loaned_on DATE NOT NULL DEFAULT CURRENT_DATE
- loan.due_on DATE NOT NULL
- loan.returned_on DATE
- loan CONSTRAINT loan_due_after_loan CHECK (due_on > loaned_on)
- idx_book_author ON book (author_id)
- idx_loan_member ON loan (member_id)
- idx_loan_open ON loan (due_on) WHERE returned_on IS NULL

#### Reads
- book reads author.author_id
- loan reads book.book_id
- loan reads member.member_id

#### Writes
- author writes author_id, full_name, birth_year, country_code
- book writes book_id, isbn, title, author_id, published_year, copies_total, copies_free
- member writes member_id, email, full_name, joined_on, is_blocked
- loan writes loan_id, book_id, member_id, loaned_on, due_on, returned_on

#### Relationships
- book.author_id -> author.author_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id

#### Dependencies
- author
- book
- member
