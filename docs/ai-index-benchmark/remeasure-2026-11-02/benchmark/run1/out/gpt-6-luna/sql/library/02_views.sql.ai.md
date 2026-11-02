### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:27:57Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Defines lending-library reports for overdue loans, member borrowing activity, and books with high loan counts.

#### Purpose
- Exposes loan, member, and book reporting views, including a materialized ranking of recently popular books.
- Adds a unique index on each popular-books row’s `book_id`.

#### Type
- Views and materialized view; PostgreSQL.
- Views: `overdue_loans`, `member_loan_stats`.
- Materialized view: `popular_books`.
- Index: `idx_popular_books_id`.

#### Schema
- `overdue_loans`: `loan_id` (source type), `member_id` (source type), `email` (source type), `title` (source type), `due_on` (source type), `days_overdue` (date subtraction; integer).
- `member_loan_stats`: `member_id` (source type), `full_name` (source type), `loans_total` (count; bigint), `loans_open` (filtered count; bigint), `last_loan_on` (source type).
- `popular_books`: `book_id` (source type), `title` (source type), `author_name` (source type), `times_loaned` (count; bigint).
- Index `idx_popular_books_id` on `popular_books (book_id)`, unique, default method.

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
- Built-in values/functions: `CURRENT_DATE`, `COUNT`, `MAX`.
