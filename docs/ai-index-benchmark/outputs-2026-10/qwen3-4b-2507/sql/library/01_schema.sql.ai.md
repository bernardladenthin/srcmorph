### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-28T21:08:38Z
- T: 2026-10-29T08:13:12Z
- G: 
- A: 0.0.0
- X: file
---
> Models the core lending library data including books, authors, members, and loans with ownership, availability, and borrowing constraints.

#### Purpose
- Defines entities for a lending library: books, authors, members, and loans.
- Enforces business rules such as book availability, due dates after loan dates, and copy counts.

#### Type
Table (PostgreSQL)

#### Schema
- author.author_id (BIGSERIAL, PRIMARY KEY)
- author.full_name (VARCHAR(200), NOT NULL)
- author.birth_year (SMALLINT, CHECK (birth_year BETWEEN 1000 AND 2100))
- author.country_code (CHAR(2))

- book.book_id (BIGSERIAL, PRIMARY KEY)
- book.isbn (VARCHAR(17), NOT NULL, UNIQUE)
- book.title (VARCHAR(300), NOT NULL)
- book.author_id (BIGINT, NOT NULL, REFERENCES author(author_id))
- book.published_year (SMALLINT)
- book.copies_total (INTEGER, NOT NULL, DEFAULT 1, CHECK (copies_total >= 0))
- book.copies_free (INTEGER, NOT NULL, DEFAULT 1, CHECK (copies_free >= 0))
- book.copies_free_le_total (CONSTRAINT, CHECK (copies_free <= copies_total))

- member.member_id (BIGSERIAL, PRIMARY KEY)
- member.email (VARCHAR(254), NOT NULL, UNIQUE)
- member.full_name (VARCHAR(200), NOT NULL)
- member.joined_on (DATE, NOT NULL, DEFAULT CURRENT_DATE)
- member.is_blocked (BOOLEAN, NOT NULL, DEFAULT FALSE)

- loan.loan_id (BIGSERIAL, PRIMARY KEY)
- loan.book_id (BIGINT, NOT NULL, REFERENCES book(book_id))
- loan.member_id (BIGINT, NOT NULL, REFERENCES member(member_id) ON DELETE CASCADE)
- loan.loaned_on (DATE, NOT NULL, DEFAULT CURRENT_DATE)
- loan.due_on (DATE, NOT NULL)
- loan.returned_on (DATE)
- loan.loan_due_after_loan (CONSTRAINT, CHECK (due_on > loaned_on))

#### Reads
- author.full_name, author.birth_year, author.country_code
- book.title, book.isbn, book.published_year, book.copies_total, book.copies_free, book.author_id
- member.full_name, member.email, member.joined_on, member.is_blocked
- loan.loaned_on, loan.due_on, loan.returned_on, loan.member_id, loan.book_id

#### Writes
- book.author_id, book.copies_total, book.copies_free
- member.email, member.full_name, member.is_blocked
- loan.book_id, loan.member_id, loan.loaned_on, loan.due_on, loan.returned_on

#### Relationships
- book.author_id → author.author_id
- book.book_id → loan.book_id
- loan.member_id → member.member_id

#### Routine logic
- None (static schema definition)

#### Dependencies
author, book, member, loan
