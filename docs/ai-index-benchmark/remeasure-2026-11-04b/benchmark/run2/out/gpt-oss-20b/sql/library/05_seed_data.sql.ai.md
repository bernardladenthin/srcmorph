### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:02:36Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Seed data for a library system: authors, books, members, and loan records.

#### Purpose
- Populate the `author`, `book`, `member`, and `loan` tables with initial data for testing or demo purposes.

#### Type
- SQL script (PostgreSQL).  
- No tables, views, or functions are created; only `INSERT` statements.

#### Writes
- `author`: 15 rows  
- `book`: 150 rows  
- `member`: 50 rows  
- `loan`: 150 rows  

#### Relationships
- `book.author_id` → `author.author_id`  
- `loan.book_id` → `book.book_id`  
- `loan.member_id` → `member.member_id`  

#### Dependencies
- Tables referenced: `author`, `book`, `member`, `loan`.
