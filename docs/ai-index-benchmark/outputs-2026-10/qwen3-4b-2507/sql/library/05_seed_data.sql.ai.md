### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T08:13:33Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds initial data for a lending library system, populating authors, books, members, and loans with realistic fictional records.

#### Purpose
- Initializes the lending library database with sample data for authors, books, members, and their loan history.
- Provides realistic, fictional data to support testing, development, and demonstration of library operations.

#### Type
SQL script (DDL/DML) containing INSERT statements for tables: `author`, `book`, `member`, and `loan`.

#### Schema
- `author (author_id, full_name, birth_year, country_code)`
  - `author_id` INT PRIMARY KEY
  - `full_name` VARCHAR(255) NOT NULL
  - `birth_year` INT
  - `country_code` CHAR(2)
- `book (book_id, isbn, title, author_id, published_year, copies_total, copies_free)`
  - `book_id` INT PRIMARY KEY
  - `isbn` VARCHAR(20) NOT NULL
  - `title` VARCHAR(255) NOT NULL
  - `author_id` INT REFERENCES author(author_id)
  - `published_year` INT
  - `copies_total` INT NOT NULL DEFAULT 2
  - `copies_free` INT NOT NULL DEFAULT 2
- `member (member_id, email, full_name, joined_on, is_blocked)`
  - `member_id` INT PRIMARY KEY
  - `email` VARCHAR(255) NOT NULL UNIQUE
  - `full_name` VARCHAR(255) NOT NULL
  - `joined_on` DATE NOT NULL
  - `is_blocked` BOOLEAN NOT NULL DEFAULT FALSE
- `loan (loan_id, book_id, member_id, loaned_on, due_on, returned_on)`
  - `loan_id` INT PRIMARY KEY
  - `book_id` INT REFERENCES book(book_id)
  - `member_id` INT REFERENCES member(member_id)
  - `loaned_on` DATE NOT NULL
  - `due_on` DATE NOT NULL
  - `returned_on` DATE

#### Reads
- `author` (via `author_id` in `book`)
- `book` (via `book_id` in `loan`)
- `member` (via `member_id` in `loan`)

#### Writes
- `author` (INSERTs 15 records)
- `book` (INSERTs 110 records)
- `member` (INSERTs 50 records)
- `loan` (INSERTs 150 records)

#### Relationships
- `book.author_id → author.author_id`
- `loan.book_id → book.book_id`
- `loan.member_id → member.member_id`

#### Routine logic
- Executes a transactional block to insert seed data into four core tables.
- Each insert is a standalone statement with no procedural logic.
- Data is generated with realistic patterns: book titles, author names, member join dates, and loan timelines.
- All inserts are wrapped in `BEGIN; ... COMMIT;` to ensure atomicity.

#### Dependencies
- `author`, `book`, `member`, `loan` tables (all defined in the schema)
