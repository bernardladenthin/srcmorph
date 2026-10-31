### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:54:50Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library: authors, books, members, and loan records.

#### Purpose
- Populate the `author`, `book`, `member`, and `loan` tables with initial data for testing or demo purposes.

#### Type
- SQL script (PostgreSQL).  
- No extensions or custom types defined.

#### Schema (inferred from INSERT statements)
- **author**  
  - `author_id` integer  
  - `full_name` text  
  - `birth_year` integer  
  - `country_code` char(2)  
- **book**  
  - `book_id` integer  
  - `isbn` text  
  - `title` text  
  - `author_id` integer  
  - `published_year` integer  
  - `copies_total` integer  
  - `copies_free` integer  
- **member**  
  - `member_id` integer  
  - `email` text  
  - `full_name` text  
  - `joined_on` date  
  - `is_blocked` boolean  
- **loan**  
  - `loan_id` integer  
  - `book_id` integer  
  - `member_id` integer  
  - `loaned_on` date  
  - `due_on` date  
  - `returned_on` date (nullable)

#### Reads
- None. The script only performs INSERT operations.

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
- Tables referenced: `author`, `book`, `member`, `loan`.  
- No additional functions, sequences, or triggers are invoked.
