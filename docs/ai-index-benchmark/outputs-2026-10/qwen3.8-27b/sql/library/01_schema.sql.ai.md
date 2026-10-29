### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-28T21:08:38Z
- T: 2026-10-28T22:43:28Z
- G: 
- A: 0.0.0
- X: file
---
> Core lending-library schema defining authors, books, members, and loan transactions with availability tracking.

#### Purpose
- Models a library lending system: catalog (author, book), patrons (member), and circulation (loan).
- Tracks book copy availability via `copies_total` / `copies_free` and enforces referential integrity across all entities.

#### Type
- PostgreSQL DDL — four tables, three indexes.

#### Schema

**author**
- `author_id BIGSERIAL` — PK
- `full_name VARCHAR(200)` — NOT NULL
- `birth_year SMALLINT` — CHECK (BETWEEN 1000 AND 2100)
- `country_code CHAR(2)`

**book**
- `book_id BIGSERIAL` — PK
- `isbn VARCHAR(17)` — NOT NULL, UNIQUE
- `title VARCHAR(300)` — NOT NULL
- `author_id BIGINT` — NOT NULL, FK → author(author_id)
- `published_year SMALLINT`
- `copies_total INTEGER` — NOT NULL, DEFAULT 1, CHECK (>= 0)
- `copies_free INTEGER` — NOT NULL, DEFAULT 1, CHECK (>= 0)
- CHECK `book_free_le_total`: copies_free <= copies_total

**member**
- `member_id BIGSERIAL` — PK
- `email VARCHAR(254)` — NOT NULL, UNIQUE
- `full_name VARCHAR(200)` — NOT NULL
- `joined_on DATE` — NOT NULL, DEFAULT CURRENT_DATE
- `is_blocked BOOLEAN` — NOT NULL, DEFAULT FALSE

**loan**
- `loan_id BIGSERIAL` — PK
- `book_id BIGINT` — NOT NULL, FK → book(book_id)
- `member_id BIGINT` — NOT NULL, FK → member(member_id) ON DELETE CASCADE
- `loaned_on DATE` — NOT NULL, DEFAULT CURRENT_DATE
- `due_on DATE` — NOT NULL
- `returned_on DATE` — nullable
- CHECK `loan_due_after_loan`: due_on > loaned_on

**Indexes**
- `idx_book_author` ON book(author_id)
- `idx_loan_member` ON loan(member_id)
- `idx_loan_open` ON loan(due_on) WHERE returned_on IS NULL (partial index)

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id` (ON DELETE CASCADE)

#### Dependencies
- `author`, `book`, `member`, `loan` (all defined in this file)
