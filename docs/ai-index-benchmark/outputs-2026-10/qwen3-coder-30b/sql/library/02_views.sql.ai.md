### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:17:09Z
- G: 
- A: 0.0.0
- X: file
---
> Provides reporting views for overdue loans, member loan statistics, and popular books in a lending library system.

#### Purpose
- Overdue loans report
- Member loan activity summary
- Popular books ranking

#### Type
- view
- view
- materialized view
- index

#### Schema
- overdue_loans.loan_id integer
- overdue_loans.member_id integer
- overdue_loans.email character varying
- overdue_loans.title character varying
- overdue_loans.due_on date
- overdue_loans.days_overdue integer

- member_loan_stats.member_id integer
- member_loan_stats.full_name character varying
- member_loan_stats.loans_total bigint
- member_loan_stats.loans_open bigint
- member_loan_stats.last_loan_on date

- popular_books.book_id integer
- popular_books.title character varying
- popular_books.author_name character varying
- popular_books.times_loaned bigint

#### Reads
- overdue_loans FROM loan, member, book
- member_loan_stats FROM member, loan
- popular_books FROM loan, book, author

#### Writes
- None

#### Relationships
- loan.member_id -> member.member_id
- loan.book_id -> book.book_id
- book.author_id -> author.author_id

#### Dependencies
- loan
- member
- book
- author
- CURRENT_DATE
- INTERVAL '365 days'
