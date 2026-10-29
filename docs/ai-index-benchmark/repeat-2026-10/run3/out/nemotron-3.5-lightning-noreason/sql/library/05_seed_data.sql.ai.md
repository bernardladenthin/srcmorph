### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:03Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library: populates author, book, member, and loan tables with initial records.

#### Purpose
- Populates core entity tables (author, book, member, loan) with seed records for a lending library database.
- Provides initial author, book, member, and loan data for PostgreSQL environment.

#### Type
- Seed data script (PostgreSQL)
- Contains INSERT statements for tables: author, book, member, loan

#### Schema

**author**
- author_id int PK
- full_name varchar
- birth_year int
- country_code char(2)

**book**
- book_id int PK
- isbn varchar
- title varchar
- author_id int FK -> author.author_id
- published_year int
- copies_total int
- copies_free int

**member**
- member_id int PK
- email varchar
- full_name varchar
- joined_on date
- is_blocked boolean

**loan**
- loan_id int PK
- book_id int FK -> book.book_id
- member_id int FK -> member.member_id
- loaned_on date
- due_on date
- returned_on date (nullable)

#### Reads
- author (author_id, full_name, birth_year, country_code)
- book (book_id, isbn, title, author_id, published_year, copies_total, copies_free)
- member (member_id, email, full_name, joined_on, is_blocked)
- loan (loan_id, book_id, member_id, loaned_on, due_on, returned_on)

#### Writes
- author: INSERT author_id, full_name, birth_year, country_code
- book: INSERT book_id, isbn, title, author_id, published_year, copies_total, copies_free
- member: INSERT member_id, email, full_name, joined_on, is_blocked
- loan: INSERT loan_id, book_id, member_id, loaned_on, due_on, returned_on

#### Relationships
- book.author_id -> author.author_id (foreign key)
- loan.book_id -> book.book_id (foreign key)
- loan.member_id -> member.member_id (foreign key)

#### Dependencies
- author table
- book table
- member table
- loan table
