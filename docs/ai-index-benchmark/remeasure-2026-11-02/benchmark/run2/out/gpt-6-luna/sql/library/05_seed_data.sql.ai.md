### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:44:47Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Seeds a lending library with author, book, member, and loan records, including copy availability and loan-return status.

#### Purpose
- Populate a lending library with sample catalog, membership, and borrowing data in one transaction.

#### Type
- PostgreSQL DML seed script.

#### Writes
- `author`: 15 rows; IDs 1–15.
- `book`: 110 rows; IDs 1–110.
- `member`: 50 rows; IDs 1–50.
- `loan`: 150 rows; IDs 1–150. `returned_on` is `NULL` for some loans.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- `author`, `book`, `member`, `loan` (insert targets).
