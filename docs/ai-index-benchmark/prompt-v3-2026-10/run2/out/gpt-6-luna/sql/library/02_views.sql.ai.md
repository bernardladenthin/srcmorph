### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:11:56Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and books loaned frequently in the past year.

#### Purpose
- Exposes overdue-loan details and per-member loan statistics.
- Materializes popular books with at least five loans in the past 365 days.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`
- PostgreSQL materialized view: `popular_books`
- PostgreSQL index: `idx_popular_books_id`

#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (`CURRENT_DATE - l.due_on`)
- `member_loan_stats`: `member_id`, `full_name`, `loans_total`, `loans_open`, `last_loan_on`
- `popular_books`: `book_id`, `title`, `author_name`, `times_loaned`
- Index `idx_popular_books_id` on `popular_books (book_id)`; unique; method not specified

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`
- `member`: `member_id`, `email`, `full_name`
- `book`: `book_id`, `title`, `author_id`
- `author`: `author_id`, `full_name`
- `CURRENT_DATE` and `INTERVAL '365 days'` used in view expressions and filters

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
