### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:47:33Z
- G: 
- A: 0.0.0
- X: file
---
> Defines PostgreSQL reporting views for overdue loans, per-member loan statistics, and popular books, plus a unique index on the popular books materialized view.

#### Purpose
- `overdue_loans` lists active loans past their due date with computed days overdue.
- `member_loan_stats` aggregates total, open, and most recent loan dates per member.
- `popular_books` materializes books loaned at least five times within the trailing 365 days.
- `idx_popular_books_id` enforces uniqueness on `popular_books.book_id`.

#### Type
PostgreSQL view, materialized view, and unique index.

#### Schema
`overdue_loans`
- `loan_id` — projected from `loan.loan_id`
- `member_id` — projected from `member.member_id`
- `email` — projected from `member.email`
- `title` — projected from `book.title`
- `due_on` — projected from `loan.due_on`
- `days_overdue` — computed `CURRENT_DATE - loan.due_on`
No explicit constraints on the view.

`member_loan_stats`
- `member_id` — grouped from `member.member_id`
- `full_name` — grouped from `member.full_name`
- `loans_total` — aggregate `COUNT(loan.loan_id)`
- `loans_open` — aggregate `COUNT(loan.loan_id) FILTER (WHERE loan.returned_on IS NULL)`
- `last_loan_on` — aggregate `MAX(loan.loaned_on)`
No explicit constraints on the view.

`popular_books`
- `book_id` — grouped from `book.book_id`
- `title` — grouped from `book.title`
- `author_name` — projected from `author.full_name`
- `times_loaned` — aggregate `COUNT(*)`
Index: `idx_popular_books_id` UNIQUE on `popular_books (book_id)`.

#### Reads
- `overdue_loans` reads `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.returned_on`, `loan.due_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats` reads `member.member_id`, `member.full_name`; `loan.loan_id`, `loan.member_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books` reads `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- `loan`
- `member`
- `book`
- `author`
- `popular_books`
