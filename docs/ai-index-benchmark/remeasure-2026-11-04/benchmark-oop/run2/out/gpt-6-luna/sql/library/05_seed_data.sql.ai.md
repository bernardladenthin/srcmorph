### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:37:23Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Provides deterministic sample lending-library catalog, member, and borrowing records for development and testing.

#### Purpose
- Seeds authors and book inventory, members, and loan histories with fixed IDs and dates.
- Wraps all inserts in one transaction (`BEGIN` / `COMMIT`).

#### Type
- PostgreSQL DML seed script.

#### Writes
- `author`: 15 rows in 15 single-row INSERTs (IDs 1–15).
- `book`: 110 rows in 110 single-row INSERTs (IDs 1–110).
- `member`: 50 rows in 50 single-row INSERTs (IDs 1–50); `joined_on` values are date arithmetic from `DATE '2020-01-01'`; some members are blocked.
- `loan`: 150 rows in 150 single-row INSERTs (IDs 1–150); `returned_on` is `NULL` for some active loans.
- No reads or updates/deletes.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
