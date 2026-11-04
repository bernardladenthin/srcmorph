### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:30:08Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Seeds a lending-library catalogue, its authors and members, and historical book-loan records.

#### Purpose
- Populate sample author, book, member, and loan data in a transaction.

#### Type
- DML script; PostgreSQL.

#### Writes
- `author`: 15 single-row `INSERT`s = 15 rows; `author_id` 1–15.
- `book`: 110 single-row `INSERT`s = 110 rows; `book_id` 1–110.
- `member`: 50 single-row `INSERT`s = 50 rows; `member_id` 1–50.
- `loan`: 150 single-row `INSERT`s = 150 rows; `loan_id` 1–150.

#### Relationships
- Seeded references: `book.author_id -> author.author_id`
- Seeded references: `loan.book_id -> book.book_id`
- Seeded references: `loan.member_id -> member.member_id`
