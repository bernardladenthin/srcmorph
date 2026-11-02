### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:01:21Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library dataset with authors, catalog books, members, and loan history.

#### Purpose
- Populate sample library catalog, member, and borrowing records in one transaction.
- Seed 15 authors, 110 books, 50 members, and 150 loans.

#### Type
- PostgreSQL data-seed script; transaction uses `BEGIN` and `COMMIT`.

#### Writes
- `author` (`author_id`, `full_name`, `birth_year`, `country_code`): 15 rows.
- `book` (`book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`): 110 rows.
- `member` (`member_id`, `email`, `full_name`, `joined_on`, `is_blocked`): 50 rows.
- `loan` (`loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`): 150 rows.

#### Dependencies
- Target tables: `author`, `book`, `member`, `loan`.
