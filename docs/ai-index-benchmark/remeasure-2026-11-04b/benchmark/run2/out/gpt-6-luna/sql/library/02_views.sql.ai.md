### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:58:09Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Provides lending-library reports for overdue loans, member borrowing activity, and recently popular books.

#### Purpose
- `overdue_loans` identifies unreturned loans past their due dates.
- `member_loan_stats` summarizes member borrowing; `popular_books` ranks books loaned in the past year.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`
- PostgreSQL materialized view: `popular_books`
- PostgreSQL index: `idx_popular_books_id`

#### Schema
- `overdue_loans`: `loan_id` (from `loan.loan_id`); `member_id` (from `member.member_id`); `email` (from `member.email`); `title` (from `book.title`); `due_on` (from `loan.due_on`); `days_overdue` (`CURRENT_DATE - loan.due_on`).
- `member_loan_stats`: `member_id` (from `member.member_id`); `full_name` (from `member.full_name`); `loans_total` (`COUNT(l.loan_id)`); `loans_open` (filtered count where `returned_on IS NULL`); `last_loan_on` (`MAX(l.loaned_on)`).
- `popular_books`: `book_id` (from `book.book_id`); `title` (from `book.title`); `author_name` (from `author.full_name`); `times_loaned` (`COUNT(*)`).
- Index `idx_popular_books_id` on `popular_books (book_id)`; unique, default method.

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.member_id`, `loan.loan_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books`: `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
