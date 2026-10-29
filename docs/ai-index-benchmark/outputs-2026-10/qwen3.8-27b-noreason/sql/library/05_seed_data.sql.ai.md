### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T09:58:44Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library system, populating authors, books, members, and loan records.

#### Purpose
- Inserts initial test data into the `author`, `book`, `member`, and `loan` tables.
- Establishes relationships between books and authors, and loans between books and members.

#### Type
- DML (Data Manipulation Language) script
- Dialect: PostgreSQL

#### Writes
- `author`: `author_id`, `full_name`, `birth_year`, `country_code`
- `book`: `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`
- `member`: `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`
- `loan`: `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`

#### Relationships
- `book.author_id` -> `author.author_id`
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id`

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
