### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-28T21:08:38Z
- T: 2026-10-28T22:17:01Z
- G: 
- A: 0.0.0
- X: file
---
> Manages the core data model for a lending library system, including authors, books, members, and loans.

#### Purpose
- Stores and manages information about library authors, books, members, and their borrowing history
- Enforces data integrity through constraints and foreign key relationships

#### Type
table

#### Schema
author.author_id BIGSERIAL PRIMARY KEY
author.full_name VARCHAR(200) NOT NULL
author.birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)
author.country_code CHAR(2)

book.book_id BIGSERIAL PRIMARY KEY
book.isbn VARCHAR(17) NOT NULL UNIQUE
book.title VARCHAR(300) NOT NULL
book.author_id BIGINT NOT NULL REFERENCES author (author_id)
book.published_year SMALLINT
book.copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)
book.copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)
book.book_free_le_total CHECK (copies_free <= copies_total)

member.member_id BIGSERIAL PRIMARY KEY
member.email VARCHAR(254) NOT NULL UNIQUE
member.full_name VARCHAR(200) NOT NULL
member.joined_on DATE NOT NULL DEFAULT CURRENT_DATE
member.is_blocked BOOLEAN NOT NULL DEFAULT FALSE

loan.loan_id BIGSERIAL PRIMARY KEY
loan.book_id BIGINT NOT NULL REFERENCES book (book_id)
loan.member_id BIGINT NOT NULL REFERENCES member (member_id) ON DELETE CASCADE
loan.loaned_on DATE NOT NULL DEFAULT CURRENT_DATE
loan.due_on DATE NOT NULL
loan.returned_on DATE
loan.loan_due_after_loan CHECK (due_on > loaned_on)

#### Reads
None

#### Writes
None

#### Relationships
book.author_id -> author.author_id
loan.book_id -> book.book_id
loan.member_id -> member.member_id

#### Dependencies
author
book
member
loan
