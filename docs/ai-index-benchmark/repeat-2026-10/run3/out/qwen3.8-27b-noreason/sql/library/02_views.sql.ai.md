### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:48:10Z
- G: 
- A: 0.0.0
- X: file
---
> Defines reporting views for a lending library to surface overdue loans, per-member loan statistics, and top-loaned books over the trailing year.

#### Purpose
- `overdue_loans`: lists active loans past their `due_on` date with `days_overdue` calculated.
- `member_loan_stats`: aggregates total and open loan counts plus `last_loan_on` per `member`.
- `popular_books`: materialized view ranking books by `times_loaned` in the last 365 days, filtered to those loaned at least 5 times.

#### Type
- View: `overdue_loans`, `member_loan_stats`
- Materialized view: `popular_books`
- Index: `idx_popular_books_id`
- Dialect: PostgreSQL

#### Schema
- `overdue_loans`
  - `loan_id`
  - `member_id`
  - `email`
  - `title`
  - `due_on`
  - `days_overdue`
- `member_loan_stats`
  - `member_id`
  - `full_name`
  - `loans_total`
  - `loans_open`
  - `last_loan_on`
- `popular_books`
  - `book_id` (UNIQUE via `idx_popular_books_id`)
  - `title`
  - `author_name`
  - `times_loaned`

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`
- `member`: `member_id`, `email`, `full_name`
- `book`: `book_id`, `title`, `author_id`
- `author`: `author_id`, `full_name`

#### Writes
- None (read-only views and materialized view)

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- `loan`
- `member`
- `book`
- `author`
