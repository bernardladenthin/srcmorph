### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:12:21Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library dataset with authors, catalog books, members, and loan history, including active and returned loans.

#### Purpose
- Populate lending-library reference and transaction data in one transaction.
- Seed author profiles, book inventory, member accounts, and loan dates/status.

#### Type
- DML script; PostgreSQL dialect.

#### Writes
- `author`: 15 rows in 15 single-row INSERTs; columns `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 rows in 110 single-row INSERTs; columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 rows in 50 single-row INSERTs; columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 rows in 150 single-row INSERTs; columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`; some `returned_on` values are `NULL`.
- All inserts execute between `BEGIN` and `COMMIT`.

#### Dependencies
- Target tables: `author`, `book`, `member`, `loan`.
