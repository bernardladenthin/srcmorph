### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:30:05Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library: authors, books, members, and loans.

#### Purpose
- Populate a lending library database with authors, books, members, and loans.
- Provide seed data for testing and demonstration.

#### Type
- SQL script (PostgreSQL DML: INSERT statements).

#### Schema
- author: author_id integer PK, full_name text, birth_year integer, country_code text.
- book: book_id integer PK, isbn text, title text, author_id integer FK to author, published_year integer, copies_total integer, copies_free integer.
- member: member_id integer PK, email text, full_name text, joined_on date, is_blocked boolean.
- loan: loan_id integer PK, book_id integer FK to book, member_id integer FK to member, loaned_on date, due_on date, returned_on date.

#### Reads
- No reads; only INSERT operations.

#### Writes
- author: full_name, birth_year, country_code.
- book: isbn, title, author_id, published_year, copies_total, copies_free.
- member: email, full_name, joined_on, is_blocked.
- loan: book_id, member_id, loaned_on, due_on, returned_on.

#### Relationships
- book.author_id -> author.author_id.
- loan.book_id -> book.book_id.
- loan.member_id -> member.member_id.

#### Dependencies
- author, book, member, loan tables.

#### Routine logic
- None; pure data insertion.
