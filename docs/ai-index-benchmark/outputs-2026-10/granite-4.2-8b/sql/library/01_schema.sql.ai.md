### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:43:29Z
- G: 
- A: 0.0.0
- X: file
---
> Core schema for a lending library managing authors, books, members, and loans with constraints on copies and loan dates.

#### Purpose
- Stores bibliographic and administrative data for books, authors, members, and loan transactions.
- Enforces consistency via constraints on copy counts, loan periods, and referential integrity.

#### Type
SQL schema (PostgreSQL) consisting of four tables and three indexes.

#### Schema
- `author`:
  - `author_id` BIGSERIAL PRIMARY KEY
  - `full_name` VARCHAR(200) NOT NULL
  - `birth_year` SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)
  - `country_code` CHAR(2)
- `book`:
  - `book_id` BIGSERIAL PRIMARY KEY
  - `isbn` VARCHAR(17) NOT NULL UNIQUE
  - `title` VARCHAR(300) NOT NULL
  - `author_id` BIGINT NOT NULL REFERENCES `author`(author_id)
  - `published_year` SMALLINT
  - `copies_total` INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)
  - `copies_free` INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)
  - CONSTRAINT `book_free_le_total` CHECK (copies_free <= copies_total)
- `member`:
  - `member_id` BIGSERIAL PRIMARY KEY
  - `email` VARCHAR(254) NOT NULL UNIQUE
  - `full_name` VARCHAR(200) NOT NULL
  - `joined_on` DATE NOT NULL DEFAULT CURRENT_DATE
  - `is_blocked` BOOLEAN NOT NULL DEFAULT FALSE
- `loan`:
  - `loan_id` BIGSERIAL PRIMARY KEY
  - `book_id` BIGINT NOT NULL REFERENCES `book`(book_id)
  - `member_id` BIGINT NOT NULL REFERENCES `member`(member_id) ON DELETE CASCADE
  - `loaned_on` DATE NOT NULL DEFAULT CURRENT_DATE
  - `due_on` DATE NOT NULL
  - `returned_on` DATE
  - CONSTRAINT `loan_due_after_loan` CHECK (due_on > loaned_on)
- Indexes:
  - `idx_book_author` on `book`(author_id)
  - `idx_loan_member` on `loan`(member_id)
  - `idx_loan_open` on `loan`(due_on) WHERE `returned_on` IS NULL

#### Reads
- `author`: read via `book.author_id` reference and `idx_book_author`.
- `book`: read via `loan.book_id` reference and `idx_book_author`.
- `member`: read via `loan.member_id` reference and `idx_loan_member`.
- `loan`: read via `idx_loan_open` filter and foreign keys to `book` and `member`.

#### Writes
- `book`: written via INSERT/UPDATE on `copies_total`, `copies_free`, `isbn`, `title`, `author_id`.
- `member`: written via INSERT/UPDATE on `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: written via INSERT/UPDATE on `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`; `ON DELETE CASCADE` affects `member` rows indirectly.
- Indexes are maintenance-only; no direct writes.

#### Relationships
- `book.author_id` → `author.author_id`
- `loan.book_id` → `book.book_id`
- `loan.member_id` → `member.member_id` (with `ON DELETE CASCADE`)

#### Routine logic
N/A (schema-only file; no procedures, views, or triggers present).

#### Dependencies
- `book` depends on `author` via `author_id` reference.
- `loan` depends on `book` via `book_id` reference and on `member` via `member_id` reference with `ON DELETE CASCADE`.
- Indexes depend on table columns (`author_id`, `member_id`, `due_on`, `returned_on`).
- No external functions or sequences referenced.
