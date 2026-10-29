### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-28T21:08:38Z
- T: 2026-10-29T08:17:34Z
- G: 
- A: 0.0.0
- X: file
---
> Models the core schema for a lending library, including authors, books, members, and loans.

#### Type
PostgreSQL

#### Schema
- `author`: `author_id BIGSERIAL PRIMARY KEY`, `full_name VARCHAR(200) NOT NULL`, `birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)`, `country_code CHAR(2)`
- `book`: `book_id BIGSERIAL PRIMARY KEY`, `isbn VARCHAR(17) NOT NULL UNIQUE`, `title VARCHAR(300) NOT NULL`, `author_id BIGINT NOT NULL REFERENCES author (author_id)`, `published_year SMALLINT`, `copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)`, `copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)`, `book_free_le_total CHECK (copies_free <= copies_total)`
- `member`: `member_id BIGSERIAL PRIMARY KEY`, `email VARCHAR(254) NOT NULL UNIQUE`, `full_name VARCHAR(200) NOT NULL`, `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`, `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`
- `loan`: `loan_id BIGSERIAL PRIMARY KEY`, `book_id BIGINT NOT NULL REFERENCES book (book_id)`, `member_id BIGINT NOT NULL REFERENCES member (member_id) ON DELETE CASCADE`, `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`, `due_on DATE NOT NULL`, `returned_on DATE`, `loan_due_after_loan CHECK (due_on > loaned_on)`

#### Reads
- `author`: `author_id`, `full_name`, `birth_year`, `country_code`
- `book`: `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`
- `member`: `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`
- `loan`: `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`

#### Writes
- `author`: `author_id`, `full_name`, `birth_year`, `country_code`
- `book`: `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`
- `member`: `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`
- `loan`: `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
