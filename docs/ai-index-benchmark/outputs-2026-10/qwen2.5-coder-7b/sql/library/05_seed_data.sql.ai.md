### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T08:17:46Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds the lending library database with initial data for authors, books, and members.

#### Schema
- `author (author_id int, full_name varchar, birth_year int, country_code varchar)`: PK `author_id`
- `book (book_id int, isbn varchar, title varchar, author_id int, published_year int, copies_total int, copies_free int)`: PK `book_id`, FK `author_id -> author.author_id`
- `member (member_id int, email varchar, full_name varchar, joined_on date, is_blocked boolean)`: PK `member_id`
- `loan (loan_id int, book_id int, member_id int, loaned_on date, due_on date, returned_on date)`: PK `loan_id`, FK `book_id -> book.book_id`, FK `member_id -> member.member_id`

#### Reads
- `author (author_id, full_name, birth_year, country_code)`
- `book (book_id, isbn, title, author_id, published_year, copies_total, copies_free)`
- `member (member_id, email, full_name, joined_on, is_blocked)`
- `loan (loan_id, book_id, member_id, loaned_on, due_on, returned_on)`

#### Writes
- `author (author_id, full_name, birth_year, country_code)`
- `book (book_id, isbn, title, author_id, published_year, copies_total, copies_free)`
- `member (member_id, email, full_name, joined_on, is_blocked)`
- `loan (loan_id, book_id, member_id, loaned_on, due_on, returned_on)`

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
