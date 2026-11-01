### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:27:12Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library: inserts authors, books, members and loan records into PostgreSQL tables.

#### Purpose
- Populate the `author`, `book`, `member`, and `loan` tables with initial test data.

#### Type
- PostgreSQL data script (INSERT statements).  
- No tables, views, procedures, or extensions are created in this file.

#### Schema (columns used in INSERTs)
- **author**: `author_id`, `full_name`, `birth_year`, `country_code`
- **book**: `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`
- **member**: `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`
- **loan**: `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`

#### Reads
- None.

#### Writes
- `author`: 15 rows  
- `book`: 110 rows  
- `member`: 50 rows  
- `loan`: 150 rows  

#### Relationships
- `book.author_id -> author.author_id`  
- `loan.book_id -> book.book_id`  
- `loan.member_id -> member.member_id`  

#### Dependencies
- Tables: `author`, `book`, `member`, `loan` (assumed to exist prior to running this script).
