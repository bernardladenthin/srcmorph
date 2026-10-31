### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:28:50Z
- G: 
- A: 0.0.0
- X: file
---
> Reporting views for a lending library: overdue loans, member loan statistics, and popular books.

#### Purpose
- Overdue loans view: list active loans past due date with member and book details.
- Member loan stats view: aggregate loan counts and last loan date per member.
- Popular books materialized view: count recent loans per book and author.

#### Type
- View: overdue_loans, member_loan_stats
- Materialized view: popular_books
- Index: idx_popular_books_id (unique on book_id)
- Dialect: PostgreSQL

#### Schema
- overdue_loans: loan_id, member_id, email, title, due_on, days_overdue
- member_loan_stats: member_id, full_name, loans_total, loans_open, last_loan_on
- popular_books: book_id, title, author_name, times_loaned
- Constraints: popular_books book_id unique via idx_popular_books_id

#### Reads
- overdue_loans: loan, member, book
- member_loan_stats: member, loan
- popular_books: loan, book, author

#### Writes
- None

#### Relationships
- loan.member_id -> member.member_id
- loan.book_id -> book.book_id
- book.author_id -> author.author_id

#### Routine logic
- overdue_loans: filter loans where returned_on is null and due_on before current date; compute days_overdue as current date minus due_on
- member_loan_stats: left join loans to members; count total and open loans; max loaned_on as last loan date
- popular_books: filter loans within last 365 days; group by book and author; having count >= 5; materialized view

#### Dependencies
- loan, member, book, author, current_date, interval
