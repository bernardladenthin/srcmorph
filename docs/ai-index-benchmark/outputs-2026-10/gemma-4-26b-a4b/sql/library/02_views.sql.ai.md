### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:49:18Z
- G: 
- A: 0.0.0
- X: file
---
> Provides reporting views for tracking overdue library loans, member borrowing statistics, and popular book trends.

#### Purpose
* Aggregates library data for operational reporting and analytics.
* Identifies delinquent loans, member activity levels, and high-demand books.

#### Type
View / Materialized View (PostgreSQL).

#### Schema
overdue_loans
* loan_id
* member_id
* email
* title
* due_on
* days_overdue (calculated)

member_loan_stats
* member_id
* full_name
* loans_total (calculated)
* loans_open (calculated)
* last_loan_on (calculated)

popular_books (Materialized View)
* book_id (UNIQUE INDEX)
* title
* author_name
* times_loaned (calculated)

#### Reads
* loan: loan_id, member_id, book_id, due_on, returned_on, loaned_on
* member: member_id, email, full_name
* book: book_id, title, author_id
* author: author_id, full_name

#### Writes
None

#### Relationships
* overdue_loans: loan.member_id -> member.member_id, loan.book_id -> book.book_id
* member_loan_stats: member.member_id -> loan.member_id
* popular_books: loan.book_id -> book.book_id, book.author_id -> author.author_id

#### Dependencies
* loan
* member
* book
* author
