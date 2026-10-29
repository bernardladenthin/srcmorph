### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:04:55Z
- G: 
- A: 0.0.0
- X: file
---
> Inserts initial seed data for authors, books, members, and loan transactions into a PostgreSQL lending library database.

#### Purpose
- Populates reference data for authors and books, and transactional data for library members and loans.
- Establishes a baseline dataset for testing and benchmarking the lending library application.

#### Type
PostgreSQL script (DML: INSERT statements within a transaction block).

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
- `author` table
- `book` table
- `member` table
- `loan` table
