### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:08:37Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a PostgreSQL lending library: authors, books, members, and loan records.

#### Purpose
- Populate `author`, `book`, `member`, and `loan` tables with initial data for testing or demo purposes.

#### Type
- Data insertion script (PostgreSQL).

#### Writes
- `author` – 15 rows inserted.
- `book` – 150 rows inserted.
- `member` – 50 rows inserted.
- `loan` – 150 rows inserted.

#### Relationships
- `book.author_id` → `author.author_id`
- `loan.book_id` → `book.book_id`
- `loan.member_id` → `member.member_id`

#### Dependencies
- None (only inserts into existing tables).
