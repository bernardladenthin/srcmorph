### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T08:57:11Z
- G: 
- A: 0.0.0
- X: file
---
> Populates a lending library database with initial authors, books, members, and loan records for testing.

#### Purpose
- Inserts seed data into `author`, `book`, `member`, and `loan` tables to initialize a library system.
- Establishes relationships between authors, books, and members via `loan` transactions.

#### Type
PostgreSQL DML script (transactional `INSERT` statements within `BEGIN`/`COMMIT`).

#### Schema
**author**
- `author_id` integer (PK)
- `full_name` text
- `birth_year` integer
- `country_code` text

**book**
- `book_id` integer (PK)
- `isbn` text
- `title` text
- `author_id` integer (FK to author)
- `published_year` integer
- `copies_total` integer
- `copies_free` integer

**member**
- `member_id` integer (PK)
- `email` text
- `full_name` text
- `joined_on` date
- `is_blocked` boolean

**loan**
- `loan_id` integer (PK)
- `book_id` integer (FK to book)
- `member_id` integer (FK to member)
- `loaned_on` date
- `due_on` date
- `returned_on` date (nullable)

#### Reads
- None (pure data insertion).

#### Writes
- `author` (all columns)
- `book` (all columns)
- `member` (all columns)
- `loan` (all columns)

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
