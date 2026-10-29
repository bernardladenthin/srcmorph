### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:44:01Z
- G: 
- A: 0.0.0
- X: file
---
> Populates a lending library database with seed data for authors, books, members, and loan transactions.

#### Purpose
- Inserts initial reference data for authors and books into the library catalog.
- Creates member accounts and historical loan records for testing and benchmarking.

#### Type
PostgreSQL DML script (INSERT statements within a transaction).

#### Schema
**Table: author**
- `author_id` integer (PK)
- `full_name` text
- `birth_year` integer
- `country_code` text

**Table: book**
- `book_id` integer (PK)
- `isbn` text
- `title` text
- `author_id` integer (FK to author)
- `published_year` integer
- `copies_total` integer
- `copies_free` integer

**Table: member**
- `member_id` integer (PK)
- `email` text
- `full_name` text
- `joined_on` date
- `is_blocked` boolean

**Table: loan**
- `loan_id` integer (PK)
- `book_id` integer (FK to book)
- `member_id` integer (FK to member)
- `loaned_on` date
- `due_on` date
- `returned_on` date (nullable)

#### Reads
None (pure data insertion).

#### Writes
- `author` (columns: `author_id`, `full_name`, `birth_year`, `country_code`)
- `book` (columns: `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`)
- `member` (columns: `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`)
- `loan` (columns: `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`)

#### Relationships
- `book.author_id` -> `author.author_id`
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id`

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
