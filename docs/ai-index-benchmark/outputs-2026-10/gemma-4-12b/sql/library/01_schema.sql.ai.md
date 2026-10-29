### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-28T21:08:38Z
- T: 2026-10-29T04:00:05Z
- G: 
- A: 0.0.0
- X: file
---
> This schema defines the core data model for a lending library system, managing authors, books, members, and loan tracking.

#### Purpose
* Models the core entities for a library management system.
* Tracks book inventory, member status, and active/historical loan records.

#### Type
Table (PostgreSQL)

#### Schema
#### author
* author_id BIGSERIAL PRIMARY KEY
* full_name VARCHAR(200) NOT NULL
* birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)
* country_code CHAR(2)

#### book
* book_id BIGSERIAL PRIMARY KEY
* isbn VARCHAR(17) NOT NULL UNIQUE
* title VARCHAR(300) NOT NULL
* author_id BIGINT NOT NULL REFERENCES author (author_id)
* published_year SMALLINT
* copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)
* copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)
* CONSTRAINT book_free_le_total CHECK (copies_free <= copies_total)
* INDEX idx_book_author (author_id)

#### member
* member_id BIGSERIAL PRIMARY KEY
* email VARCHAR(254) NOT NULL UNIQUE
* full_name VARCHAR(200) NOT NULL
* joined_on DATE NOT NULL DEFAULT CURRENT_DATE
* is_blocked BOOLEAN NOT NULL DEFAULT FALSE

#### loan
* loan_id BIGSERIAL PRIMARY KEY
* book_id BIGINT NOT NULL REFERENCES book (book_id)
* member_id BIGINT NOT NULL REFERENCES member (member_id) ON DELETE CASCADE
* loaned_on DATE NOT NULL DEFAULT CURRENT_DATE
* due_on DATE NOT NULL
* returned_on DATE
* CONSTRAINT loan_due_after_loan CHECK (due_on > loaned_on)
* INDEX idx_loan_member (member_id)
* INDEX idx_loan_open (due_on) WHERE returned_on IS NULL

#### Relationships
* author.author_id -> book.author_id
* book.book_id -> loan.book_id
* member.member_id -> loan.member_id
