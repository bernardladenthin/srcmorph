### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:58:31Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Provides lending-library reports for overdue loans, member borrowing activity, and books loaned frequently within the past year.

#### Purpose
- `overdue_loans` identifies unreturned loans past their due date.
- `member_loan_stats` summarizes member borrowing; `popular_books` lists books loaned at least five times in the past 365 days.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats` (views); `popular_books` (materialized view).

#### Schema
- `overdue_loans`: `loan_id` (from `loan.loan_id`), `member_id` (from `member.member_id`), `email` (from `member.email`), `title` (from `book.title`), `due_on` (from `loan.due_on`), `days_overdue` (`CURRENT_DATE - loan.due_on`).
- `member_loan_stats`: `member_id` (from `member.member_id`), `full_name` (from `member.full_name`), `loans_total` (`COUNT(loan.loan_id)`), `loans_open` (filtered count of loans where `returned_on IS NULL`), `last_loan_on` (`MAX(loan.loaned_on)`).
- `popular_books`: `book_id` (from `book.book_id`), `title` (from `book.title`), `author_name` (from `author.full_name`), `times_loaned` (`COUNT(*)`).
- Index `idx_popular_books_id`: unique B-tree on `popular_books (book_id)`.

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`.
- `member`: `member_id`, `email`, `full_name`.
- `book`: `book_id`, `title`, `author_id`.
- `author`: `author_id`, `full_name`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Source tables: `loan`, `member`, `book`, `author`.
