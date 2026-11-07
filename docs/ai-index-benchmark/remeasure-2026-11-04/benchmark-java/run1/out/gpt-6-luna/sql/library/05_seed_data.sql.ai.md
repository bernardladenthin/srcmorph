### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:12:14Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Populates a lending-library database with sample authors, catalog books, members, and loan histories.

#### Purpose
- Seeds PostgreSQL lending-library data for authors, books, members, and loans.
- Runs all inserts in one transaction.

#### Type
- DML transaction; PostgreSQL.

#### Writes
- `author`: 15 single-row INSERTs = 15 rows; `author_id` 1–15, first `Radia Lovelace`, last `Grace Lamarr`.
- `book`: 110 single-row INSERTs = 110 rows; `book_id` 1–110, first `The Hidden Golden 1`, last `The Last Glass 110`; each row supplies ISBN, title, author, publication year, and total/free copy counts.
- `member`: 50 single-row INSERTs = 50 rows; `member_id` 1–50; supplies email, name, join date, and blocked status.
- `loan`: 150 single-row INSERTs = 150 rows; `loan_id` 1–150; supplies book, member, loan/due/return dates; some `returned_on` values are `NULL`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`
