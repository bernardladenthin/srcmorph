### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-28T21:08:38Z
- T: 2026-10-29T00:03:46Z
- G: 
- A: 0.0.0
- X: file
---
> Core lending library schema defining authors, books, members, and loans with constraints for availability tracking and lifecycle management.

#### Purpose
*   Models the lending library domain with entities for authors, books, members, and loans.
*   Enforces business rules via constraints on book inventory, loan validity, and member status.

#### Type
Table (PostgreSQL)

#### Schema
**author**
*   `author_id` BIGSERIAL PRIMARY KEY
*   `full_name` VARCHAR(200) NOT NULL
*   `birth_year` SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)
*   `country_code` CHAR(2)

**book**
*   `book_id` BIGSERIAL PRIMARY KEY
*   `isbn` VARCHAR(17) NOT NULL UNIQUE
*   `title` VARCHAR(300) NOT NULL
*   `author_id` BIGINT NOT NULL REFERENCES author (author_id)
*   `published_year` SMALLINT
*   `copies_total` INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)
*   `copies_free` INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)
*   `book_free_le_total` CHECK (copies_free <= copies_total)

**member**
*   `member_id` BIGSERIAL PRIMARY KEY
*   `email` VARCHAR(254) NOT NULL UNIQUE
*   `full_name` VARCHAR(200) NOT NULL
*   `joined_on` DATE NOT NULL DEFAULT CURRENT_DATE
*   `is_blocked` BOOLEAN NOT NULL DEFAULT FALSE

**loan**
*   `loan_id` BIGSERIAL PRIMARY KEY
*   `book_id` BIGINT NOT NULL REFERENCES book (book_id)
*   `member_id` BIGINT NOT NULL REFERENCES member (member_id) ON DELETE CASCADE
*   `loaned_on` DATE NOT NULL DEFAULT CURRENT_DATE
*   `due_on` DATE NOT NULL
*   `returned_on` DATE
*   `loan_due_after_loan` CHECK (due_on > loaned_on)

#### Reads
*   `author` table for author details (full_name, birth_year, country_code).
*   `book` table for book details (isbn, title, published_year, copies_total, copies_free).
*   `member` table for member details (email, full_name, joined_on, is_blocked).
*   `loan` table for loan records (loan_id, book_id, member_id, loaned_on, due_on, returned_on).

#### Writes
*   `author` table (INSERT/UPDATE) for author records.
*   `book` table (INSERT/UPDATE) for book inventory and metadata.
*   `member` table (INSERT/UPDATE) for member registration and blocking status.
*   `loan` table (INSERT/UPDATE/DELETE) for loan transactions and returns.

#### Relationships
*   `book.author_id` -> `author.author_id`
*   `loan.book_id` -> `book.book_id`
*   `loan.member_id` -> `member.member_id`

#### Routine logic
*   None (DDL schema only).

#### Dependencies
*   `author`
*   `book`
*   `member`
*   `loan`
*   `idx_book_author`
*   `idx_loan_member`
*   `idx_loan_open`
