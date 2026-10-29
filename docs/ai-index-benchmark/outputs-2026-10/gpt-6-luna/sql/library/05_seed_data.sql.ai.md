### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T21:21:31Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library dataset with authors, catalog books, members, and dated book-loan records.

#### Purpose
- Populate lending-library reference and transaction data for development or testing.
- Insert authors, books, members, and loans within one transaction.

#### Type
- PostgreSQL DML seed script

#### Writes
- `author`: `author_id`, `full_name`, `birth_year`, `country_code` — 15 rows
- `book`: `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free` — 110 rows
- `member`: `member_id`, `email`, `full_name`, `joined_on`, `is_blocked` — 50 rows
- `loan`: `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on` — 150 rows

#### Relationships
- Seed data associates `book.author_id` with `author.author_id`.
- Seed data associates `loan.book_id` with `book.book_id`.
- Seed data associates `loan.member_id` with `member.member_id`.

#### Routine logic
- Begins a transaction, inserts seed rows into four tables, then commits.
- Loan dates use PostgreSQL date-plus-integer expressions; `returned_on` is `NULL` for some loans.

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
