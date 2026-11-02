### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:37:42Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and frequently borrowed books.

#### Purpose
- `overdue_loans` lists unreturned loans past their due date with member and book details.
- `member_loan_stats` summarizes member borrowing; `popular_books` reports books loaned at least five times in the past 365 days.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`
- PostgreSQL materialized view: `popular_books`
- PostgreSQL index: `idx_popular_books_id`

#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (`CURRENT_DATE - due_on`)
- `member_loan_stats`: `member_id`, `full_name`, `loans_total`, `loans_open`, `last_loan_on`
- `popular_books`: `book_id`, `title`, `author_name`, `times_loaned`
- Index: `idx_popular_books_id` on `popular_books (book_id)`, unique; default method (B-tree)

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.loan_id`, `loan.member_id`, `loan.returned_on`, `loan.loaned_on`
- `popular_books`: `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
- Built-in expressions: `CURRENT_DATE`, `INTERVAL`
