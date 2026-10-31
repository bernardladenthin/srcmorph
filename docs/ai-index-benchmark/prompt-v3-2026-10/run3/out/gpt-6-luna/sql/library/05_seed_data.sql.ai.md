### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:37:05Z
- G: 
- A: 0.0.0
- X: file
---
> Loads a lending-library seed dataset of authors, catalog books, library members, and loan histories.

#### Purpose
- Populates reference and transactional data for a lending-library database in one transaction.
- Seeds author and book catalog records, member accounts, and dated loans, including unreturned loans.

#### Type
- DML script; PostgreSQL.

#### Writes
- `author`: 15 rows; columns `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: 110 rows; columns `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: 50 rows; columns `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: 150 rows; columns `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`.
- Begins transaction and commits after inserts.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
