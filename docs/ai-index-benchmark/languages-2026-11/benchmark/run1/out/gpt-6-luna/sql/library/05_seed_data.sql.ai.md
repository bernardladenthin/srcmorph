### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:15:41Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library database with authors, book inventory, library members, and loan histories for testing and development.

#### Purpose
- Populate library catalog, membership, and borrowing records with deterministic sample data.
- Insert all seed records within one transaction.

#### Type
- PostgreSQL DML transaction; no schema objects created.

#### Writes
- `author`: 15 rows in 15 single-row INSERTs; columns `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 rows in 110 single-row INSERTs; columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 rows in 50 single-row INSERTs; columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 rows in 150 single-row INSERTs; columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- Target tables: `author`, `book`, `member`, `loan`.
