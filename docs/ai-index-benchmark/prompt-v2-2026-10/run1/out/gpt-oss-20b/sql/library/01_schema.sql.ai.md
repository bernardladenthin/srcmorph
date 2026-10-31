### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:38:43Z
- G: 
- A: 0.0.0
- X: file
---
> Defines core tables for a lending library system, modeling authors, books, members, and loans with constraints and indexes.

#### Purpose
- Create the foundational library schema: authors, books, members, loans.  
- Enforce data integrity via primary keys, foreign keys, unique constraints, check constraints, defaults, and indexes.

#### Type
- PostgreSQL DDL: tables and indexes.

#### Schema
- **author**  
  - `author_id BIGSERIAL PRIMARY KEY`  
  - `full_name VARCHAR(200) NOT NULL`  
  - `birth_year SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)`  
  - `country_code CHAR(2)`  

- **book**  
  - `book_id BIGSERIAL PRIMARY KEY`  
  - `isbn VARCHAR(17) NOT NULL UNIQUE`  
  - `title VARCHAR(300) NOT NULL`  
  - `author_id BIGINT NOT NULL REFERENCES author (author_id)`  
  - `published_year SMALLINT`  
  - `copies_total INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)`  
  - `copies_free INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)`  
  - `CONSTRAINT book_free_le_total CHECK (copies_free <= copies_total)`  

- **member**  
  - `member_id BIGSERIAL PRIMARY KEY`  
  - `email VARCHAR(254) NOT NULL UNIQUE`  
  - `full_name VARCHAR(200) NOT NULL`  
  - `joined_on DATE NOT NULL DEFAULT CURRENT_DATE`  
  - `is_blocked BOOLEAN NOT NULL DEFAULT FALSE`  

- **loan**  
  - `loan_id BIGSERIAL PRIMARY KEY`  
  - `book_id BIGINT NOT NULL REFERENCES book (book_id)`  
  - `member_id BIGINT NOT NULL REFERENCES member (member_id) ON DELETE CASCADE`  
  - `loaned_on DATE NOT NULL DEFAULT CURRENT_DATE`  
  - `due_on DATE NOT NULL`  
  - `returned_on DATE`  
  - `CONSTRAINT loan_due_after_loan CHECK (due_on > loaned_on)`  

- **Indexes**  
  - `idx_book_author ON book (author_id)`  
  - `idx_loan_member ON loan (member_id)`  
  - `idx_loan_open ON loan (due_on) WHERE returned_on IS NULL`  

#### Relationships
- `book.author_id -> author.author_id` (FK)  
- `loan.book_id -> book.book_id` (FK)  
- `loan.member_id -> member.member_id` (FK, ON DELETE CASCADE)

#### Dependencies
- Tables referenced by foreign keys: `author`, `book`, `member`.  
- Indexes depend on tables: `idx_book_author` (book), `idx_loan_member` (loan), `idx_loan_open` (loan).
