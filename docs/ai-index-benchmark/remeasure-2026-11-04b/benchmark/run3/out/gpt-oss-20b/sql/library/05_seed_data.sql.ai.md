### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:10:39Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Seed data for a PostgreSQL library system: authors, books, members, and loans.

#### Purpose
- Populate the `author`, `book`, `member`, and `loan` tables with initial test data.

#### Type
- SQL script (INSERT statements) for PostgreSQL.

#### Schema
- No tables or constraints are defined in this file; it only inserts rows.

#### Reads
- None.

#### Writes
- `author`: 15 rows  
- `book`: 150 rows (book_id 1‑150)  
- `member`: 50 rows (member_id 1‑50)  
- `loan`: 150 rows (loan_id 1‑150)

#### Relationships
- `book.author_id` → `author.author_id`  
- `loan.book_id` → `book.book_id`  
- `loan.member_id` → `member.member_id`

#### Dependencies
- Tables: `author`, `book`, `member`, `loan`.
