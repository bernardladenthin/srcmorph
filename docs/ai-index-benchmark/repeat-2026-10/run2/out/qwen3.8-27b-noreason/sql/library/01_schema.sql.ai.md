### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:07:55Z
- G: 
- A: 0.0.0
- X: file
---
> Core relational schema for a lending library system modeling authors, books, members, and loan transactions.

#### Purpose
- Defines the foundational data model for a library management system, tracking book inventory, authorship, member accounts, and circulation history.
- Enforces data integrity through constraints on copy availability, loan validity periods, and referential integrity.

#### Type
- SQL DDL (Data Definition Language)
- Dialect: PostgreSQL (evidenced by `BIGSERIAL`, `CHECK` constraints, and partial index syntax)

#### Schema
**Table: `author`**
- `author_id BIGSERIAL` PRIMARY KEY
- `full_name VARCHAR(200)` NOT NULL
- `birth_year SMALLINT` CHECK (`birth_year` BETWEEN 1000 AND 2100)
- `country_code CHAR(2)`

**Table: `book`**
- `book_id BIGSERIAL` PRIMARY KEY
- `isbn VARCHAR(17)` NOT NULL UNIQUE
- `title VARCHAR(300)` NOT NULL
- `author_id BIGINT` NOT NULL REFERENCES `author(author_id)`
- `published_year SMALLINT`
- `copies_total INTEGER` NOT NULL DEFAULT 1 CHECK (`copies_total` >= 0)
- `copies_free INTEGER` NOT NULL DEFAULT 1 CHECK (`copies_free` >= 0)
- CONSTRAINT `book_free_le_total` CHECK (`copies_free` <= `copies_total`)

**Table: `member`**
- `member_id BIGSERIAL` PRIMARY KEY
- `email VARCHAR(254)` NOT NULL UNIQUE
- `full_name VARCHAR(200)` NOT NULL
- `joined_on DATE` NOT NULL DEFAULT `CURRENT_DATE`
- `is_blocked BOOLEAN` NOT NULL DEFAULT FALSE

**Table: `loan`**
- `loan_id BIGSERIAL` PRIMARY KEY
- `book_id BIGINT` NOT NULL REFERENCES `book(book_id)`
- `member_id BIGINT` NOT NULL REFERENCES `member(member_id)` ON DELETE CASCADE
- `loaned_on DATE` NOT NULL DEFAULT `CURRENT_DATE`
- `due_on DATE` NOT NULL
- `returned_on DATE`
- CONSTRAINT `loan_due_after_loan` CHECK (`due_on` > `loaned_on`)

**Indexes**
- `idx_book_author` ON `book(author_id)`
- `idx_loan_member` ON `loan(member_id)`
- `idx_loan_open` ON `loan(due_on)` WHERE `returned_on` IS NULL (Partial Index)

#### Relationships
- `book.author_id` -> `author.author_id`
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id` (ON DELETE CASCADE)

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
