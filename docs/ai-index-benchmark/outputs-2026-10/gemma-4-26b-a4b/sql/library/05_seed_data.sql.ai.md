### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:51:45Z
- G: 
- A: 0.0.0
- X: file
---
> <Populates a lending library database with seed data for authors, books, members, and loan transactions.>

#### Purpose
* Provides initial reference data for a library management system.
* Models relationships between authors, their books, library members, and the history of book loans.

#### Type
DML (Data Manipulation Language) / Seed Data (PostgreSQL)

#### Schema
**author**
* `author_id` integer (PK)
* `full_name` text
* `birth_year` integer
* `country_code` text

**book**
* `book_id` integer (PK)
* `isbn` text
* `title` text
* `author_id` integer (FK)
* `published_year` integer
* `copies_total` integer
* `copies_free` integer

**member**
* `member_id` integer (PK)
* `email` text
* `full_name` text
* `joined_on` date
* `is_blocked` boolean

**loan**
* `loan_id` integer (PK)
* `book_id` integer (FK)
* `member_id` integer (FK)
* `loaned_on` date
* `due_on` date
* `returned_on` date

#### Reads
* None (This is a pure insertion script).

#### Writes
* `author` (all columns)
* `book` (all columns)
* `member` (all columns)
* `loan` (all columns)

#### Relationships
* `book.author_id -> author.author_id`
* `loan.book_id -> book.book_id`
* `loan.member_id -> member.member_id`

#### Dependencies
* `author`
* `book`
* `member`
* `loan`
