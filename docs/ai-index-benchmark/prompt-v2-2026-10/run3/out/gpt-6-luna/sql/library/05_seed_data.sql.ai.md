### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:59:27Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library database with author, book, member, and loan records for catalog and circulation data.

#### Purpose
- Populate the lending-library dataset with authors, catalog titles and copy counts, members, and loan histories.
- Wrap all seed inserts in a single transaction.

#### Type
- PostgreSQL DML transaction.

#### Writes
- `author`: inserts 15 rows into `author_id`, `full_name`, `birth_year`, `country_code`.
- `book`: inserts 110 rows into `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
- `member`: inserts 50 rows into `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
- `loan`: inserts 150 rows into `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`.
- Transaction commits after inserts.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
