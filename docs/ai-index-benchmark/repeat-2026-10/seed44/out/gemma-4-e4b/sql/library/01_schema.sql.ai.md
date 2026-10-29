### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:04:58Z
- G: 
- A: 0.0.0
- X: file
---
> This schema defines the core entities and relationships for a lending library system, tracking authors, books, members, and loan transactions.

#### Purpose
* Defines the structure for authors, books, library members, and loan records.
* Establishes constraints and relationships to manage lending operations.

#### Type
table (PostgreSQL)

#### Schema
author
author_id BIGSERIAL PRIMARY KEY
full_name VARCHAR(200) NOT NULL
birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)
country_code CHAR(2)

book
book_id BIGSERIAL PRIMARY KEY
isbn VARCHAR(17) NOT NULL UNIQUE
title VARCHAR(300) NOT NULL
author_id BIGINT NOT NULL REFERENCES author (author_id)
published_year SMALLINT
copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)
copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)
CONSTRAINT book_free_le_total CHECK (copies_free <= copies_total)

member
member_id BIGSERIAL PRIMARY KEY
email VARCHAR(254) NOT NULL UNIQUE
full_name VARCHAR(200) NOT NULL
joined_on DATE NOT NULL DEFAULT CURRENT_DATE
is_blocked BOOLEAN NOT NULL DEFAULT FALSE

loan
loan_id BIGSERIAL PRIMARY KEY
book_id BIGINT NOT NULL REFERENCES book (book_id)
member_id BIGINT NOT NULL REFERENCES member (member_id) ON DELETE CASCADE
loaned_on DATE NOT NULL DEFAULT CURRENT_DATE
due_on DATE NOT NULL
returned_on DATE
CONSTRAINT loan_due_after_loan CHECK (due_on > loaned_on)

Index: idx_book_author on book (author_id)
Index: idx_loan_member on loan (member_id)
Index: idx_loan_open on loan (due_on) WHERE returned_on IS NULL

#### Reads
book (author_id)
loan (book_id, member_id)

#### Writes
author (author_id)
book (book_id, author_id)
member (member_id)
loan (loan_id, book_id, member_id)

#### Relationships
author.author_id -> book.author_id
member.member_id -> loan.member_id
book.book_id -> loan.book_id

#### Dependencies
author
book
member
loan
