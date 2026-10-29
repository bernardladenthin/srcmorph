### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T05:07:23Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending‑library domain (authors, books, members, loans) in PostgreSQL.

#### Purpose
- Provides a ready‑to‑use dataset for testing, demos, or benchmarking of the library schema.
- Populates the core tables (`author`, `book`, `member`, `loan`) with realistic, varied records.

#### Type
- DDL/DML statements for PostgreSQL.

#### Schema
**author**
- `author_id`   INT PRIMARY KEY
- `full_name`   VARCHAR NOT NULL
- `birth_year`  INT
- `country_code` CHAR(2) NOT NULL

**book**
- `book_id`      INT PRIMARY KEY
- `isbn`         VARCHAR NOT NULL
- `title`        VARCHAR NOT NULL
- `author_id`    INT NOT NULL REFERENCES author(author_id)
- `published_year` INT
- `copies_total` INT NOT NULL
- `copies_free`  INT NOT NULL

**member**
- `member_id`    INT PRIMARY KEY
- `email`        VARCHAR NOT NULL
- `full_name`    VARCHAR NOT NULL
- `joined_on`    DATE NOT NULL
- `is_blocked`   BOOLEAN NOT NULL

**loan**
- `loan_id`      INT PRIMARY KEY
- `book_id`      INT NOT NULL REFERENCES book(book_id)
- `member_id`    INT NOT NULL REFERENCES member(member_id)
- `loaned_on`    DATE NOT NULL
- `due_on`       DATE NOT NULL
- `returned_on`  DATE   -- NULL = not yet returned

#### Reads
- No explicit SELECTs; the statements read the table definitions to insert rows.

#### Writes
- Inserts rows into `author`, `book`, `member`, `loan`.

#### Relationships
- `book.author_id → author.author_id`
- `loan.book_id → book.book_id`
- `loan.member_id → member.member_id`

#### Routine logic
- None (pure data load).

#### Dependencies
- Tables `author`, `book`, `member`, `loan` must exist before execution.
