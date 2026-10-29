### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T23:07:20Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library inserting authors, books, members, and loan records.

#### Purpose
- Populates initial author, book, member, and loan tables for the lending library.
- Provides reference data for testing and demonstration.

#### Type
SQL object kind: seed data script (PostgreSQL).

#### Schema
- author: author_id, full_name, birth_year, country_code
- book: book_id, isbn, title, author_id, published_year, copies_total, copies_free
- member: member_id, email, full_name, joined_on, is_blocked
- loan: loan_id, book_id, member_id, loaned_on, due_on, returned_on

#### Writes
- Inserts into author (author_id, full_name, birth_year, country_code)
- Inserts into book (book_id, isbn, title, author_id, published_year, copies_total, copies_free)
- Inserts into member (member_id, email, full_name, joined_on, is_blocked)
- Inserts into loan (loan_id, book_id, member_id, loaned_on, due_on, returned_on)

#### Relationships
- book.author_id -> author.author_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id

#### Dependencies
- author table
- book table
- member table
