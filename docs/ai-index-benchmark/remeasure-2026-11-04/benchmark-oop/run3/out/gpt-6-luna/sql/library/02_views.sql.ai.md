### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:51:53Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Provides overdue-loan, member borrowing-statistics, and recently popular-book reports for a lending library.

#### Purpose
- `overdue_loans` reports unreturned loans past their due date and days overdue.
- `member_loan_stats` aggregates borrowing activity per member; `popular_books` ranks books loaned at least five times in the past 365 days.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`; materialized view: `popular_books`.
- Index: `idx_popular_books_id`.

#### Schema
- `overdue_loans`: `loan_id` (from `loan.loan_id`), `member_id` (from `member.member_id`), `email` (from `member.email`), `title` (from `book.title`), `due_on` (from `loan.due_on`), `days_overdue` (date subtraction: `CURRENT_DATE - due_on`).
- `member_loan_stats`: `member_id` (from `member.member_id`), `full_name` (from `member.full_name`), `loans_total` (`COUNT`, bigint), `loans_open` (filtered `COUNT`, bigint), `last_loan_on` (`MAX` of `loan.loaned_on`).
- `popular_books`: `book_id` (from `book.book_id`), `title` (from `book.title`), `author_name` (from `author.full_name`), `times_loaned` (`COUNT`, bigint).
- Index `idx_popular_books_id`: unique B-tree on `popular_books (book_id)`.

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.loan_id`, `loan.member_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books`: `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`
- `member_loan_stats`: `member.member_id -> loan.member_id` (left join)
- `popular_books`: `book.book_id -> loan.book_id`; `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- View index: `idx_popular_books_id` on `popular_books`.
