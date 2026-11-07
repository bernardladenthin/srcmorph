### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:49:07Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Seeds a lending-library catalog with authors, books, members, and historical loan records.

#### Purpose
- Populate library catalog, membership, inventory availability, and borrowing history with deterministic sample data.
- Wrap all inserts in one transaction.

#### Type
- PostgreSQL DML transaction; no schema objects created.

#### Writes
- `author`: 15 rows; columns `author_id`, `full_name`, `birth_year`, `country_code` (IDs 1–15).
- `book`: 110 rows; columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free` (IDs 1–110).
- `member`: 50 rows; columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked` (IDs 1–50).
- `loan`: 150 rows; columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on` (IDs 1–150); `returned_on` is NULL for open loans.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
