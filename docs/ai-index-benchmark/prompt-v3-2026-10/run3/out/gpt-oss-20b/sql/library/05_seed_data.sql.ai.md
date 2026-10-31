### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:43:55Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library: authors, books, members, and loan records.

#### Purpose
- Populate the `author`, `book`, `member`, and `loan` tables with initial data for testing or demo purposes.

#### Type
- SQL script (PostgreSQL) containing `INSERT` statements.

#### Schema (columns referenced)
- **author**: `author_id`, `full_name`, `birth_year`, `country_code`
- **book**: `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`
- **member**: `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`
- **loan**: `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`

#### Reads
- None (only INSERT operations).

#### Writes
- `author`: 15 rows  
- `book`: 151 rows  
- `member`: 50 rows  
- `loan`: 150 rows  

#### Relationships
- `book.author_id -> author.author_id`  
- `loan.book_id -> book.book_id`  
- `loan.member_id -> member.member_id`  

#### Dependencies
- Tables `author`, `book`, `member`, `loan` must exist prior to running this script.
