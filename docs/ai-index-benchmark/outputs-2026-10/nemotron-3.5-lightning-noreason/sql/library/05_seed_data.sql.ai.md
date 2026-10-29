### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T08:53:42Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library, populating author, book, member, and loan tables with initial records.

#### Purpose
- Populates initial reference data (authors, books, members) and circulation records (loans) for the lending library system.

#### Type
- PostgreSQL seed script (DDL-agnostic INSERT statements).

#### Schema
- **author**: author_id PK, full_name, birth_year, country_code
- **book**: book_id PK, isbn, title, author_id FK, published_year, copies_total, copies_free
- **member**: member_id PK, email, full_name, joined_on, is_blocked
- **loan**: loan_id PK, book_id FK, member_id FK, loaned_on, due_on, returned_on

#### Reads
- author (by author_id)
- book (by book_id)
- member (by member_id)

#### Writes
- author: inserts 50 rows
- book: inserts 110 rows
- member: inserts 50 rows
- loan: inserts 150 rows

#### Relationships
- book.author_id -> author.author_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id

#### Dependencies
- No external dependencies; self-contained seed data.
