### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:49:42Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and frequently loaned books.

#### Purpose
- Reports currently overdue loans with member, book, due-date, and days-overdue details.
- Summarizes member loan activity and identifies books loaned at least five times in the past year.

#### Type
- PostgreSQL: views `overdue_loans`, `member_loan_stats`; materialized view `popular_books`; index `idx_popular_books_id`.

#### Schema
- `overdue_loans`: `loan_id` (`l.loan_id`), `member_id` (`m.member_id`), `email` (`m.email`), `title` (`b.title`), `due_on` (`l.due_on`), `days_overdue` (`CURRENT_DATE - l.due_on`).
- `member_loan_stats`: `member_id` (`m.member_id`), `full_name` (`m.full_name`), `loans_total` (`COUNT(l.loan_id)`), `loans_open` (`COUNT(l.loan_id)` filtered to `l.returned_on IS NULL`), `last_loan_on` (`MAX(l.loaned_on)`).
- `popular_books`: `book_id` (`b.book_id`), `title` (`b.title`), `author_name` (`a.full_name`), `times_loaned` (`COUNT(*)`); restricted to loans from the last 365 days and groups with `COUNT(*) >= 5`.
- Index `idx_popular_books_id`: unique B-tree index on `popular_books (book_id)`.

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `returned_on`, `due_on`, `loaned_on`.
- `member`: `member_id`, `email`, `full_name`.
- `book`: `book_id`, `title`, `author_id`.
- `author`: `author_id`, `full_name`.
- `CURRENT_DATE`; interval `'365 days'`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- Index `idx_popular_books_id` depends on materialized view `popular_books`.
