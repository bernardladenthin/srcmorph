### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:48:59Z
- G: 
- A: 0.0.0
- X: file
---
> Populates a lending library database with authors, books, members, and loan records for testing and demonstration.

#### Purpose
Inserts seed data for authors, books, members, and loans in a PostgreSQL schema.

#### Type
SQL script (seed data); contains only `INSERT` statements.

#### Schema
* `author`: `author_id` (int, PK), `full_name` (text), `birth_year` (int), `country_code` (text)
* `book`: `book_id` (int, PK), `isbn` (text), `title` (text), `author_id` (int, FK), `published_year` (int), `copies_total` (int), `copies_free` (int)
* `member`: `member_id` (int, PK), `email` (text), `full_name` (text), `joined_on` (date), `is_blocked` (boolean)
* `loan`: `loan_id` (int, PK), `book_id` (int, FK), `member_id` (int, FK), `loaned_on` (date), `due_on` (date), `returned_on` (date, nullable)

#### Reads
No reads; only writes via `INSERT`.

#### Writes
Writes to `author`, `book`, `member`, and `loan` tables via `INSERT` statements.

#### Relationships
* `book.author_id` → `author.author_id`
* `loan.book_id` → `book.book_id`
* `loan.member_id` → `member.member_id`

#### Routine logic
No procedural logic; each statement inserts one row into a table with literal values. No queries or updates.

#### Dependencies
No other tables, views, functions, or sequences are referenced; all data is self-contained.
