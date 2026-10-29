### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T00:04:21Z
- G: 
- A: 0.0.0
- X: file
---
> Populates the initial dataset for a lending library system with authors, books, members, and loan records.

#### Purpose
- Initializes 15 authors and 110 books with ISBNs, titles, and copy counts.
- Registers 50 members with email addresses and join dates.
- Creates 150 loan records linking books to members with due dates and return statuses.

#### Type
Table (PostgreSQL)

#### Schema
**author**
- `author_id` INT (PK)
- `full_name` TEXT
- `birth_year` INT
- `country_code` CHAR(2)

**book**
- `book_id` INT (PK)
- `isbn` TEXT
- `title` TEXT
- `author_id` INT (FK -> author.author_id)
- `published_year` INT
- `copies_total` INT
- `copies_free` INT

**member**
- `member_id` INT (PK)
- `email` TEXT
- `full_name` TEXT
- `joined_on` DATE
- `is_blocked` BOOLEAN

**loan**
- `loan_id` INT (PK)
- `book_id` INT (FK -> book.book_id)
- `member_id` INT (FK -> member.member_id)
- `loaned_on` DATE
- `due_on` DATE
- `returned_on` DATE

#### Reads
- `author` table (author_id, full_name, birth_year, country_code)
- `book` table (book_id, isbn, title, author_id, published_year, copies_total, copies_free)
- `member` table (member_id, email, full_name, joined_on, is_blocked)

#### Writes
- `author` table (author_id, full_name, birth_year, country_code)
- `book` table (book_id, isbn, title, author_id, published_year, copies_total, copies_free)
- `member` table (member_id, email, full_name, joined_on, is_blocked)
- `loan` table (loan_id, book_id, member_id, loaned_on, due_on, returned_on)

#### Relationships
- `book.author_id` -> `author.author_id`
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id`

#### Routine logic
- No routine logic; this file contains only DML INSERT statements for seed data.

#### Dependencies
- `author` table
- `book` table
- `member` table
- `loan` table
