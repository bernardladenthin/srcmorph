### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:44:25Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Defines lending reports for overdue loans, member borrowing activity, and books with high recent loan counts.

#### Purpose
- Exposes current overdue loans and per-member loan statistics.
- Materializes books loaned at least five times in the past 365 days.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`; materialized view: `popular_books`.
- Index: `idx_popular_books_id`.

#### Schema
- `overdue_loans`: `loan_id` (from `loan.loan_id`); `member_id` (from `member.member_id`); `email` (from `member.email`); `title` (from `book.title`); `due_on` (from `loan.due_on`); `days_overdue` (integer, `CURRENT_DATE - loan.due_on`).
- `member_loan_stats`: `member_id` (from `member.member_id`); `full_name` (from `member.full_name`); `loans_total` (bigint, `COUNT`); `loans_open` (bigint, filtered `COUNT`); `last_loan_on` (type of `loan.loaned_on`, `MAX`).
- `popular_books`: `book_id` (from `book.book_id`); `title` (from `book.title`); `author_name` (from `author.full_name`); `times_loaned` (bigint, `COUNT`).
- Index: `idx_popular_books_id` on `popular_books (book_id)`, unique, default PostgreSQL method (B-tree).

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
- Tables: `loan`, `member`, `book`, `author`.
