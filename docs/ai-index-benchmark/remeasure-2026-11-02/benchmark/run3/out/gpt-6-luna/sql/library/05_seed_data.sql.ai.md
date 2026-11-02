### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:58:49Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Seeds a lending library with authors, book inventory, member accounts, and loan histories.

#### Purpose
- Populate the library catalog, membership, and circulation records with generated sample data.
- Run all inserts within one transaction.

#### Type
- DML seed script; PostgreSQL.

#### Writes
- `author`: 15 one-row INSERTs = 15 rows; IDs 1–15; columns `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 one-row INSERTs = 110 rows; IDs 1–110; columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 one-row INSERTs = 50 rows; IDs 1–50; columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 one-row INSERTs = 150 rows; IDs 1–150; columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`; some `returned_on` values are `NULL`.
- `COMMIT` completes the transaction.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
