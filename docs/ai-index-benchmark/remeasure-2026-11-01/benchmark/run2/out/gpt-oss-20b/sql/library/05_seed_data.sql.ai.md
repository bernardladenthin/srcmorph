### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:47:18Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library: authors, books, members, and loan records.

#### Purpose
- Populate the `author`, `book`, `member`, and `loan` tables with initial data for testing or demo purposes.

#### Type
- PostgreSQL data‑insertion script (DDL/DML).  
- No extensions or custom types are created.

#### Writes
- **author**: 15 rows  
  `author_id, full_name, birth_year, country_code`  
- **book**: 150 rows  
  `book_id, isbn, title, author_id, published_year, copies_total, copies_free`  
- **member**: 50 rows  
  `member_id, email, full_name, joined_on, is_blocked`  
- **loan**: 150 rows  
  `loan_id, book_id, member_id, loaned_on, due_on, returned_on`

#### Relationships
- `book.author_id -> author.author_id`  
- `loan.book_id -> book.book_id`  
- `loan.member_id -> member.member_id`

#### Dependencies
- None beyond the referenced tables (`author`, `book`, `member`, `loan`).
