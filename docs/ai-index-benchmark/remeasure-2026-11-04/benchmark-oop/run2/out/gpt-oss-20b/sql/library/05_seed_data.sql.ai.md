### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:41:01Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Seeds the lending library database with authors, books, members, and loan records.

#### Purpose
- Populate the `author`, `book`, `member`, and `loan` tables with initial data for testing or benchmarking.

#### Type
- Data insertion script (PostgreSQL).

#### Schema
- **author**  
  - `author_id` integer PK  
  - `full_name` text NOT NULL  
  - `birth_year` integer  
  - `country_code` char(2)  
- **book**  
  - `book_id` integer PK  
  - `isbn` text NOT NULL  
  - `title` text NOT NULL  
  - `author_id` integer FK → `author(author_id)`  
  - `published_year` integer  
  - `copies_total` integer  
  - `copies_free` integer  
- **member**  
  - `member_id` integer PK  
  - `email` text NOT NULL  
  - `full_name` text NOT NULL  
  - `joined_on` date  
  - `is_blocked` boolean  
- **loan**  
  - `loan_id` integer PK  
  - `book_id` integer FK → `book(book_id)`  
  - `member_id` integer FK → `member(member_id)`  
  - `loaned_on` date  
  - `due_on` date  
  - `returned_on` date

#### Reads
- None.

#### Writes
- `author`: 15 rows  
- `book`: 150 rows  
- `member`: 50 rows  
- `loan`: 150 rows (ids 1‑150)

#### Relationships
- `book.author_id -> author.author_id`  
- `loan.book_id -> book.book_id`  
- `loan.member_id -> member.member_id`

#### Dependencies
- Tables: `author`, `book`, `member`, `loan`.
