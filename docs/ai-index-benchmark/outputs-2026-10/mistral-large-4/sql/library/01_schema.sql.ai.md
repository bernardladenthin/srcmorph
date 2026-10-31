### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:42:12Z
- G: 
- A: 0.0.0
- X: file
---
> Models a lending library's core operations: author cataloging, book inventory with copy availability tracking, member registration with blocking, and loan lifecycle management from checkout through due date to return.

#### Purpose
- Defines the foundational relational schema for a lending library: authors, book catalog with physical copy inventory (copies_total vs copies_free), member registry, and loan transactions tracking borrowing, due dates, and returns.

#### Type
- Tables (author, book, member, loan) and indexes (idx_book_author, idx_loan_member, idx_loan_open); PostgreSQL dialect.

#### Schema
- **author**: author_id BIGSERIAL PRIMARY KEY; full_name VARCHAR(200) NOT NULL; birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100); country_code CHAR(2)
- **book**: book_id BIGSERIAL PRIMARY KEY; isbn VARCHAR(17) NOT NULL UNIQUE; title VARCHAR(300) NOT NULL; author_id BIGINT NOT NULL REFERENCES author(author_id); published_year SMALLINT; copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0); copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0); CONSTRAINT book_free_le_total CHECK (copies_free <= copies_total)
- **member**: member_id BIGSERIAL PRIMARY KEY; email VARCHAR(254) NOT NULL UNIQUE; full_name VARCHAR(200) NOT NULL; joined_on DATE NOT NULL DEFAULT CURRENT_DATE; is_blocked BOOLEAN NOT NULL DEFAULT FALSE
- **loan**: loan_id BIGSERIAL PRIMARY KEY; book_id BIGINT NOT NULL REFERENCES book(book_id); member_id BIGINT NOT NULL REFERENCES member(member_id) ON DELETE CASCADE; loaned_on DATE NOT NULL DEFAULT CURRENT_DATE; due_on DATE NOT NULL; returned_on DATE; CONSTRAINT loan_due_after_loan CHECK (due_on > loaned_on)
- **Indexes**: idx_book_author ON book(author_id); idx_loan_member ON loan(member_id); idx_loan_open ON loan(due_on) WHERE returned_on IS NULL (partial index for open loans)

#### Writes
- Creates tables: author, book, member, loan
- Creates indexes: idx_book_author, idx_loan_member, idx_loan_open

#### Relationships
- book.author_id -> author.author_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id (ON DELETE CASCADE)

#### Dependencies
- author (FK target from book.author_id)
- book (FK target from loan.book_id)
- member (FK target from loan.member_id)
