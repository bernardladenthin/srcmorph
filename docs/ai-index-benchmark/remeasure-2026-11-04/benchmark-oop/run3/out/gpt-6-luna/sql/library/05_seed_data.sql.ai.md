### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:52:18Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Seeds a lending-library catalog with authors, books, members, and loan-history records.

#### Purpose
- Populate lending-library data for catalog, membership, and borrowing scenarios.
- Execute all seed inserts within one transaction.

#### Type
- PostgreSQL DML seed script.

#### Writes
- `author`: 15 rows; IDs 1–15.
- `book`: 110 rows; IDs 1–110.
- `member`: 50 rows; IDs 1–50.
- `loan`: 150 rows; IDs 1–150.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- Target tables: `author`, `book`, `member`, `loan`.
