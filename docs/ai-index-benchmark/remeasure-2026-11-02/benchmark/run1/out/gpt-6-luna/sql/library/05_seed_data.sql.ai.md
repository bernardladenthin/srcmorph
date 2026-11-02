### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:28:19Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Seeds a lending library with author, book, member, and loan records for catalog and circulation scenarios.

#### Purpose
- Populate sample library catalog, member accounts, and borrowing history within a single transaction.

#### Type
- PostgreSQL DML transaction.

#### Writes
- `author`: 15 single-row INSERTs = 15 rows; IDs 1–15.
- `book`: 110 single-row INSERTs = 110 rows; IDs 1–110.
- `member`: 50 single-row INSERTs = 50 rows; IDs 1–50.
- `loan`: 150 single-row INSERTs = 150 rows; IDs 1–150. `returned_on` is `NULL` for some active loans; dates use `DATE` literals plus integer day offsets.
- Transaction committed with `COMMIT`.

#### Relationships
- `book.author_id -> author.author_id` (values reference author IDs).
- `loan.book_id -> book.book_id` (values reference book IDs).
- `loan.member_id -> member.member_id` (values reference member IDs).
