### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:16:07Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library reporting for overdue loans, member borrowing activity, and most-loaned books in the past year.

#### Purpose
- Reporting views for lending library overdue tracking and circulation analytics
- Exposes overdue_loans, member_loan_stats, popular_books for loan due_on, returned_on, loaned_on monitoring

#### Type
- view / materialized view / index, PostgreSQL dialect

#### Schema
- overdue_loans view: loan_id, member_id, email, title, due_on, days_overdue
- member_loan_stats view: member_id, full_name, loans_total, loans_open, last_loan_on
- popular_books materialized view: book_id, title, author_name, times_loaned
- UNIQUE INDEX idx_popular_books_id ON popular_books(book_id)

#### Reads
- loan.loan_id, loan.member_id, loan.book_id, loan.due_on, loan.returned_on, loan.loaned_on
- member.member_id, member.email, member.full_name
- book.book_id, book.title, book.author_id
- author.author_id, author.full_name

#### Relationships
- loan.member_id -> member.member_id
- loan.book_id -> book.book_id
- book.author_id -> author.author_id

#### Dependencies
- loan, member, book, author, popular_books
- CURRENT_DATE, INTERVAL '365 days', COUNT FILTER, GROUP BY, HAVING
