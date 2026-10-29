### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:18:40Z
- G: 
- A: 0.0.0
- X: file
---
> A PostgreSQL database schema for a lending library, providing views for overdue loans, member loan statistics, and popular books.

#### Purpose
- Report overdue loans
- Provide member loan statistics
- Track popular books

#### Type
- View
- Materialized View
- Index (PostgreSQL)

#### Schema
##### overdue_loans
- loan_id
- member_id
- email
- title
- due_on
- days_overdue

##### member_loan_stats
- member_id
- full_name
- loans_total
- loans_open
- last_loan_on

##### popular_books
- book_id
- title
- author_name
- times_loaned

#### Reads
- loan (loan_id, member_id, book_id, due_on, returned_on, loaned_on)
- member (member_id, email, full_name)
- book (book_id, title)
- author (author_id, full_name)

#### Writes
None

#### Relationships
- loan.member_id -> member.member_id
- loan.book_id -> book.book_id
- book.author_id -> author.author_id

#### Routine logic
- overdue_loans: Joins loan, member, and book tables, filters for overdue loans
- member_loan_stats: Aggregates loan data by member
- popular_books: Aggregates loan data for books in the last year, with minimum 5 loans

#### Dependencies
- loan table
- member table
- book table
- author table
