### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:23:04Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library overdue loans, member activity, and popular-book reporting views.
#### Purpose
- Expose overdue_loans for unreturned past-due loans with days_overdue
- Expose member_loan_stats for per-member loans_total, loans_open, last_loan_on
- Expose popular_books yearly most-loaned titles with times_loaned
#### Type
- view / materialized view / index, PostgreSQL dialect
- VIEW overdue_loans, VIEW member_loan_stats, MATERIALIZED VIEW popular_books, UNIQUE INDEX idx_popular_books_id
#### Schema
- overdue_loans: loan_id, member_id, email, title, due_on, days_overdue as CURRENT_DATE - due_on
- member_loan_stats: member_id, full_name, loans_total, loans_open FILTER WHERE returned_on IS NULL, last_loan_on as MAX(loaned_on)
- popular_books: book_id, title, author_name, times_loaned as COUNT(*), GROUP BY book_id, title, full_name, HAVING COUNT(*) >= 5, WHERE loaned_on >= CURRENT_DATE - 365 days
- idx_popular_books_id UNIQUE INDEX ON popular_books(book_id)
#### Reads
- loan.loan_id, loan.member_id, loan.book_id, loan.due_on, loan.returned_on, loan.loaned_on
- member.member_id, member.email, member.full_name
- book.book_id, book.title, book.author_id
- author.author_id, author.full_name
#### Relationships
- member.member_id -> loan.member_id
- book.book_id -> loan.book_id
- author.author_id -> book.author_id
#### Dependencies
- loan, member, book, author
- popular_books, idx_popular_books_id
