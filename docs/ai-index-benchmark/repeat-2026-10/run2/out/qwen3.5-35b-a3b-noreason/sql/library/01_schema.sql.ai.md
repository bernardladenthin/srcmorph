### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:04:32Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the core relational schema for a lending library, modeling authors, books, members, and loan transactions with integrity constraints.

#### Purpose
- Establishes the foundational data structures for tracking library inventory, patrons, and borrowing activity.
- Enforces business rules for book availability, member status, and loan date validity.

#### Type
Table / Index (PostgreSQL)

#### Schema
**Table: author**
- `author_id` BIGSERIAL PRIMARY KEY
- `full_name` VARCHAR(200) NOT NULL
- `birth_year` SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100)
- `country_code` CHAR(2)

**Table: book**
- `book_id` BIGSERIAL PRIMARY KEY
- `isbn` VARCHAR(17) NOT NULL UNIQUE
- `title` VARCHAR(300) NOT NULL
- `author_id` BIGINT NOT NULL (FK to author)
- `published_year` SMALLINT
- `copies_total` INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0)
- `copies_free` INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0)
- `book_free_le_total` CHECK (copies_free <= copies_total)

**Table: member**
- `member_id` BIGSERIAL PRIMARY KEY
- `email` VARCHAR(254) NOT NULL UNIQUE
- `full_name` VARCHAR(200) NOT NULL
- `joined_on` DATE NOT NULL DEFAULT CURRENT_DATE
- `is_blocked` BOOLEAN NOT NULL DEFAULT FALSE

**Table: loan**
- `loan_id` BIGSERIAL PRIMARY KEY
- `book_id` BIGINT NOT NULL (FK to book)
- `member_id` BIGINT NOT NULL (FK to member, ON DELETE CASCADE)
- `loaned_on` DATE NOT NULL DEFAULT CURRENT_DATE
- `due_on` DATE NOT NULL
- `returned_on` DATE
- `loan_due_after_loan` CHECK (due_on > loaned_on)

**Indexes**
- `idx_book_author` on `book (author_id)`
- `idx_loan_member` on `loan (member_id)`
- `idx_loan_open` on `loan (due_on)` WHERE `returned_on IS NULL`

#### Reads
- No explicit reads defined in DDL; schema defines structure for downstream queries.

#### Writes
- `author`: Inserts new author records.
- `book`: Inserts new book records with inventory counts.
- `member`: Inserts new member records.
- `loan`: Inserts new loan records; updates `returned_on` on completion.

#### Relationships
- `book.author_id` -> `author.author_id`
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id`

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
