### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:19:25Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a PostgreSQL lending library: authors, books, members and loan records.

#### Type
- SQL script (PostgreSQL)

#### Writes
- `author`: 15 rows
- `book`: 150 rows
- `member`: 50 rows
- `loan`: 150 rows

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
