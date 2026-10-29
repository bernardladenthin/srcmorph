### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:30Z
- G: 
- A: 0.0.0
- X: file
---
> This script populates the database with initial data for authors, books, library members, and loan records.

#### Purpose
* Seed data insertion for the `author` table.
* Seed data insertion for the `book` table, linking books to authors.
* Seed data insertion for the `member` table.
* Seed data insertion for the `loan` table, tracking book checkouts by members.

#### Type
SQL script (Data Manipulation Language - DML)

#### Schema
author
author_id integer (PK)
full_name text
birth_year integer
country_code text

book
book_id integer (PK)
isbn text UNIQUE
title text
author_id integer (FK)
published_year integer
copies_total integer
copies_free integer

member
member_id integer (PK)
email text UNIQUE
full_name text
joined_on date
is_blocked boolean

loan
loan_id integer (PK)
book_id integer (FK)
member_id integer (FK)
loaned_on date
due_on date
returned_on date

#### Reads
author: None (Data is inserted)
book: None (Data is inserted)
member: None (Data is inserted)
loan: None (Data is inserted)

#### Writes
author: author_id, full_name, birth_year, country_code
book: book_id, isbn, title, author_id, published_year, copies_total, copies_free
member: member_id, email, full_name, joined_on, is_blocked
loan: loan_id, book_id, member_id, loaned_on, due_on, returned_on

#### Relationships
author.author_id -> book.author_id
member.member_id -> loan.member_id
book.book_id -> loan.book_id

#### Dependencies
None (This script only inserts data into existing tables)
