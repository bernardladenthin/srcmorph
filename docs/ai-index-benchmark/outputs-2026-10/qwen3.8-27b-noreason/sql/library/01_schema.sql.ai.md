### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-28T21:08:38Z
- T: 2026-10-29T09:58:26Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the relational schema for a lending library system, modeling authors, books, members, and loan transactions with inventory tracking.

#### Purpose
- Establishes core data structures for a library management system.
- Tracks book inventory (`copies_total`, `copies_free`) and loan lifecycle (`loaned_on`, `due_on`, `returned_on`).

#### Type
- Table (PostgreSQL)

#### Schema
- `author`
  - `author_id BIGSERIAL` PRIMARY KEY
  - `full_name VARCHAR(200)` NOT NULL
  - `birth_year SMALLINT` CHECK (BETWEEN 1000 AND 2100)
  - `country_code CHAR(2)`
- `book`
  - `book_id BIGSERIAL` PRIMARY KEY
  - `isbn VARCHAR(17)` NOT NULL UNIQUE
  - `title VARCHAR(300)` NOT NULL
  - `author_id BIGINT` NOT NULL FOREIGN KEY
  - `published_year SMALLINT`
  - `copies_total INTEGER` NOT NULL DEFAULT 1 CHECK (>= 0)
  - `copies_free INTEGER` NOT NULL DEFAULT 1 CHECK (>= 0)
  - CONSTRAINT `book_free_le_total` CHECK (`copies_free` <= `copies_total`)
- `member`
  - `member_id BIGSERIAL` PRIMARY KEY
  - `email VARCHAR(254)` NOT NULL UNIQUE
  - `full_name VARCHAR(200)` NOT NULL
  - `joined_on DATE` NOT NULL DEFAULT CURRENT_DATE
  - `is_blocked BOOLEAN` NOT NULL DEFAULT FALSE
- `loan`
  - `loan_id BIGSERIAL` PRIMARY KEY
  - `book_id BIGINT` NOT NULL FOREIGN KEY
  - `member_id BIGINT` NOT NULL FOREIGN KEY ON DELETE CASCADE
  - `loaned_on DATE` NOT NULL DEFAULT CURRENT_DATE
  - `due_on DATE` NOT NULL
  - `returned_on DATE`
  - CONSTRAINT `loan_due_after_loan` CHECK (`due_on` > `loaned_on`)

#### Reads
- None (DDL only)

#### Writes
- None (DDL only)

#### Relationships
- `book.author_id` -> `author.author_id`
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id`

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
