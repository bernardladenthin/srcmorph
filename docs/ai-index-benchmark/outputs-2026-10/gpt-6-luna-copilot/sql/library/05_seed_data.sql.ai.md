### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:36:21Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library database with sample authors, book inventory, members, and loan histories.

#### Purpose
- Populate the library catalog and lending data with deterministic sample records.
- Execute all inserts in one transaction.

#### Type
- DML seed-data script; PostgreSQL

#### Writes
- `author`: inserts `author_id`, `full_name`, `birth_year`, `country_code` (15 rows).
- `book`: inserts `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free` (110 rows).
- `member`: inserts `member_id`, `email`, `full_name`, `joined_on`, `is_blocked` (50 rows).
- `loan`: inserts `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on` (150 rows).

#### Relationships
- `book.author_id -> author.author_id` — apparent data link; no constraint declared here.
- `loan.book_id -> book.book_id` — apparent data link; no constraint declared here.
- `loan.member_id -> member.member_id` — apparent data link; no constraint declared here.

#### Dependencies
- Tables: `author`, `book`, `member`, `loan`
