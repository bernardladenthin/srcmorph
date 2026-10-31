### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:18:20Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds the lending library database with authors, books, members, and loan records for seed data generation.

#### Purpose
- Populate the lending library schema with representative seed data.
- Provide test data for indexing and search operations.

#### Type
- table

#### Schema
- author: author_id integer primary key, full_name varchar, birth_year integer, country_code varchar
- book: book_id integer primary key, isbn varchar, title varchar, author_id integer foreign key, published_year integer, copies_total integer, copies_free integer
- member: member_id integer primary key, email varchar, full_name varchar, joined_on date, is_blocked boolean
- loan: loan_id integer primary key, book_id integer foreign key, member_id integer foreign key, loaned_on date, due_on date, returned_on date

#### Reads
- No explicit read statements; the script only inserts data into the tables.

#### Writes
- author: author_id, full_name, birth_year, country_code
- book: book_id, isbn, title, author_id, published_year, copies_total, copies_free
- member: member_id, email, full_name, joined_on, is_blocked
- loan: loan_id, book_id, member_id, loaned_on, due_on, returned_on

#### Relationships
- book.author_id references author.author_id
- loan.book_id references book.book_id
- loan.member_id references member.member_id

#### Routine logic
- None; the script contains only INSERT statements without procedures, functions, or triggers.

#### Dependencies
- No external objects; the seed data depends only on the defined tables.
