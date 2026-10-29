### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:12Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library, populating author, book, member, and loan tables with initial records.

#### Purpose
- Populates core entity tables (author, book, member) and transaction table (loan) with seed records for a lending library system.

#### Type
- PostgreSQL seed script (DDL-insert script)

#### Schema
- **author**: author_id int PK, full_name text, birth_year int, country_code char(2)
- **book**: book_id int PK, isbn char(17), title text, author_id int FK->author.author_id, published_year int, copies_total int, copies_free int
- **member**: member_id int PK, email text, full_name text, joined_on date, is_blocked boolean
- **loan**: loan_id int PK, book_id int FK->book.book_id, member_id int FK->member.member_id, loaned_on date, due_on date, returned_on date

#### Reads
- author (via author_id FK in book)
- book (via book_id FK in loan)
- member (via member_id FK in loan)

#### Writes
- author (15 INSERT rows)
- book (100 INSERT rows)
- member (50 INSERT rows)
- loan (150 INSERT rows)

#### Relationships
- book.author_id -> author.author_id
- loan.book_id -> book.book_id
- loan.member_id -> member.member_id

#### Dependencies
- author, book, member tables referenced by INSERT statements and FK constraints.
