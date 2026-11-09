### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:11:52Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Provides lending-library reports for overdue loans, member borrowing activity, and recently popular books.

#### Purpose
- Exposes loan and member reporting through two views and one materialized view.
- Identifies books loaned at least five times in the past 365 days.

#### Type
- Views: `overdue_loans`, `member_loan_stats`; materialized view: `popular_books`.
- Dialect: PostgreSQL.

#### Schema
- `overdue_loans`: `loan_id` (from `loan`), `member_id` (from `member`), `email` (from `member`), `title` (from `book`), `due_on` (from `loan`), `days_overdue` (date subtraction; integer).
- `member_loan_stats`: `member_id` (from `member`), `full_name` (from `member`), `loans_total` (count; bigint), `loans_open` (filtered count; bigint), `last_loan_on` (maximum `loaned_on`; source type).
- `popular_books`: `book_id` (from `book`), `title` (from `book`), `author_name` (from `author.full_name`), `times_loaned` (count; bigint).
- Index `idx_popular_books_id`: unique B-tree on `popular_books (book_id)`.

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.loan_id`, `loan.member_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books`: `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
