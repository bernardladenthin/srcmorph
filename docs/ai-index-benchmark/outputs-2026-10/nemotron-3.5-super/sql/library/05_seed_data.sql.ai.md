### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:51:17Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a PostgreSQL lending library database with author, book, member, and loan records for benchmark testing.

#### Purpose
- Inserts reference and transactional seed data into the lending library tables: author, book, member, and loan.
- Provides 15 authors, 110 books, 50 members, and 150 loans for a PostgreSQL lending library benchmark.

#### Type
- DML seed script (PostgreSQL), wrapped in BEGIN / COMMIT transaction.

#### Schema
- author: author_id, full_name, birth_year, country_code (15 rows inserted; column types and constraints are not declared in this seed file)
- book: book_id, isbn, title, author_id, published_year, copies_total, copies_free (110 rows inserted; column types and constraints are not declared in this seed file)
- member: member_id, email, full_name, joined_on, is_blocked (50 rows inserted; column types and constraints are not declared in this seed file)
- loan: loan_id, book_id, member_id, loaned_on, due_on, returned_on (150 rows inserted; column types and constraints are not declared in this seed file)

#### Reads
- None. This file performs only INSERT writes; no SELECT, FROM, JOIN, or subquery sources.

#### Writes
- author: author_id, full_name, birth_year, country_code
- book: book_id, isbn, title, author_id, published_year, copies_total, copies_free
- member: member_id, email, full_name, joined_on, is_blocked
- loan: loan_id, book_id, member_id, loaned_on, due_on, returned_on

#### Relationships
- book.author_id -> author.author_id (implied by seed data values)
- loan.book_id -> book.book_id (implied by seed data values)
- loan.member_id -> member.member_id (implied by seed data values)

#### Dependencies
- Tables: author, book, member, loan must exist prior to execution.
- No sequences, functions, or views referenced.
