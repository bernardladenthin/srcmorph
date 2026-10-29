### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T04:01:54Z
- G: 
- A: 0.0.0
- X: file
---
> This file provides reporting views for a lending library to track overdue loans, member activity statistics, and popular book trends.

#### Purpose
* Provides reporting views for library operations.
* Tracks overdue items, member loan history, and high-frequency book loans.

#### Type
View, Materialized View (PostgreSQL)

#### Schema
overdue_loans
* loan_id (from loan)
* member_id (from member)
* email (from member)
* title (from book)
* due_on (from loan)
* days_overdue (calculated: CURRENT_DATE - l.due_on)

member_loan_stats
* member_id (from member)
* full_name (from member)
* loans_total (count of l.loan_id)
* loans_open (count of l.loan_id where returned_on IS NULL)
* last_loan_on (max of l.loaned_on)

popular_books
* book_id (from book)
* title (from book)
* author_name (from author)
* times_loaned (count of l.loan_id)
* UNIQUE INDEX idx_popular_books_id on book_id

#### Reads
* loan: loan_id, member_id, book_id, due_on, returned_on, loaned_on
* member: member_id, email, full_name
* book: book_id, title, author_id
* author: author_id, full_name

#### Writes
* None (Read-only views)

#### Relationships
* loan.member_id -> member.member_id
* loan.book_id -> book.book_id
* book.author_id -> author.author_id

#### Dependencies
* loan
* member
* book
* author
