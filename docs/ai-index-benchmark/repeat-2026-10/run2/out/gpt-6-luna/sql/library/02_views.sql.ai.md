### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:50:14Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and books loaned at least five times in the past year.

#### Purpose
- Reports open overdue loans with member and book details.
- Summarizes member loan activity and identifies popular books from the prior 365 days.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`
- PostgreSQL materialized view: `popular_books`
- Unique index: `idx_popular_books_id`

#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (calculated as `CURRENT_DATE - due_on`); rows restricted to unreturned loans due before today.
- `member_loan_stats`: `member_id`, `full_name`, `loans_total`, `loans_open`, `last_loan_on`; includes members with no loans.
- `popular_books`: `book_id`, `title`, `author_name`, `times_loaned`; rows limited to loans from the past 365 days with at least five loans per book.
- `idx_popular_books_id`: unique index on `popular_books(book_id)`.

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `returned_on`, `due_on`, `loaned_on`
- `member`: `member_id`, `email`, `full_name`
- `book`: `book_id`, `title`, `author_id`
- `author`: `author_id`, `full_name`

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Base tables: `loan`, `member`, `book`, `author`
- Index: `idx_popular_books_id` on `popular_books(book_id)`
