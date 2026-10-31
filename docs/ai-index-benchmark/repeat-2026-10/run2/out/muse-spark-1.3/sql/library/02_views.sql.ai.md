### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:16:42Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library reporting for overdue loans, member loan activity, and most-borrowed books over the past year.
#### Purpose
- Expose `overdue_loans` for unreturned past-due `loan` items with borrower contact and lateness.
- Expose `member_loan_stats` for per-`member` total / open loan counts and last activity.
- Expose `popular_books` for yearly most-loaned `book` titles with `author` attribution.
#### Type
- view: `overdue_loans`, `member_loan_stats`
- materialized view: `popular_books`
- index: `UNIQUE INDEX idx_popular_books_id`
- Dialect: PostgreSQL
#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` derived as `CURRENT_DATE - due_on`
- `member_loan_stats`: `member_id`, `full_name`, `loans_total`, `loans_open` filtered where `returned_on IS NULL`, `last_loan_on` as `MAX(loaned_on)`
- `popular_books`: `book_id`, `title`, `author_name`, `times_loaned` as `COUNT(*)` where `loaned_on >= CURRENT_DATE - 365 days` and `HAVING COUNT(*) >= 5`
- Index: `idx_popular_books_id UNIQUE ON popular_books(book_id)`
#### Reads
- `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.loaned_on`, `loan.due_on`, `loan.returned_on`
- `member.member_id`, `member.email`, `member.full_name`
- `book.book_id`, `book.title`, `book.author_id`
- `author.author_id`, `author.full_name`
#### Relationships
- `loan.member_id -> member.member_id` via `JOIN member m`
- `loan.book_id -> book.book_id` via `JOIN book b`
- `book.author_id -> author.author_id` via `JOIN author a`
#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
- Views: `overdue_loans`, `member_loan_stats`, `popular_books`
- Index target: `popular_books(book_id)`
