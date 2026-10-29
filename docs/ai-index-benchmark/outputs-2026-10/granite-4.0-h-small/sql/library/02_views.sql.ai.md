### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T05:05:13Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library reporting views for overdue loans, member loan statistics, and popular books.

#### Purpose
- Provide reporting views for overdue loans, member loan statistics, and popular books.

#### Type
- PostgreSQL views and materialized view.

#### Schema
**overdue_loans**
- loan_id INT
- member_id INT
- email VARCHAR
- title VARCHAR
- due_on DATE
- days_overdue INT

**member_loan_stats**
- member_id INT
- full_name VARCHAR
- loans_total INT
- loans_open INT
- last_loan_on DATE

**popular_books**
- book_id INT
- title VARCHAR
- author_name VARCHAR
- times_loaned INT

#### Reads
- overdue_loans: loan, member, book
- member_loan_stats: member, loan
- popular_books: loan, book, author

#### Writes
- None

#### Relationships
- overdue_loans: loan.member_id -> member.member_id, loan.book_id -> book.book_id
- member_loan_stats: member.member_id -> loan.member_id
- popular_books: loan.book_id -> book.book_id, book.author_id -> author.author_id

#### Routine logic
- overdue_loans: Selects overdue loans where returned_on is null and due_on is before current date.
- member_loan_stats: Aggregates loan counts and open loans per member.
- popular_books: Aggregates loan counts for books loaned in the last year with at least 5 loans.

#### Dependencies
- loan, member, book, author tables.
