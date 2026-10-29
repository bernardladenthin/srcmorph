### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:46:35Z
- G: 
- A: 0.0.0
- X: file
---
> This file seeds a lending-library catalog and circulation dataset for authors, books, members, and loans in a PostgreSQL database.

#### Purpose
- Seeds initial library reference and lending data for author, book, member, and loan records.
- Establishes a realistic dataset for catalog browsing, membership, and borrowing lifecycle tracking.

#### Type
- PostgreSQL seed data script / DML.
- Tables populated via INSERT INTO: author, book, member, loan.
- Transaction wrapper: BEGIN; ... COMMIT;

#### Schema
- author: author_id integer; full_name text; birth_year integer; country_code text.
  - Constraints: not declared in this file; data only.
- book: book_id integer; isbn text; title text; author_id integer; published_year integer; copies_total integer; copies_free integer.
  - Constraints: not declared in this file; data only.
- member: member_id integer; email text; full_name text; joined_on date; is_blocked boolean.
  - Constraints: not declared in this file; data only.
- loan: loan_id integer; book_id integer; member_id integer; loaned_on date; due_on date; returned_on date NULLABLE.
  - Constraints: not declared in this file; data only.

#### Reads
- None.
- No SELECT, FROM, JOIN, subquery, or function input references are present in this file.

#### Writes
- author: author_id, full_name, birth_year, country_code.
  - 15 rows inserted.
- book: book_id, isbn, title, author_id, published_year, copies_total, copies_free.
  - 110 rows inserted.
- member: member_id, email, full_name, joined_on, is_blocked.
  - 50 rows inserted.
- loan: loan_id, book_id, member_id, loaned_on, due_on, returned_on.
  - 150 rows inserted.

#### Relationships
- book.author_id -> author.author_id.
- loan.book_id -> book.book_id.
- loan.member_id -> member.member_id.
- Data linkage present in seed values: author IDs are referenced by book.author_id; book IDs are referenced by loan.book_id; member IDs are referenced by loan.member_id.

#### Dependencies
- author
- book
- member
- loan
- None other tables, views, functions, or sequences are referenced in the file.
