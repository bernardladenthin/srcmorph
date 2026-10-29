### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:50:35Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds a lending-library database with author, book, member, and loan records for catalog, inventory, membership, and circulation scenarios.

#### Purpose
- Populate library reference and operational data with generated sample records.
- Insert 15 authors, 110 books, 50 members, and 150 loans in one transaction.

#### Type
- Data seeding script; PostgreSQL.

#### Writes
- `author(author_id, full_name, birth_year, country_code)` — inserts author records.
- `book(book_id, isbn, title, author_id, published_year, copies_total, copies_free)` — inserts catalog and copy inventory.
- `member(member_id, email, full_name, joined_on, is_blocked)` — inserts member profiles and block status.
- `loan(loan_id, book_id, member_id, loaned_on, due_on, returned_on)` — inserts loan history; `returned_on` is `NULL` for some open loans.
- Transaction boundaries: `BEGIN` / `COMMIT`.

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- Target tables: `author`, `book`, `member`, `loan`.
