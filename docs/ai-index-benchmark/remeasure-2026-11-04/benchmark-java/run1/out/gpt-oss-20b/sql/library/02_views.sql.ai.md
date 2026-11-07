### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:16:49Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> A set of reporting views and a materialized view for a library system, summarizing overdue loans, member loan statistics, and popular books.

#### Purpose
- Provide read‑only reports on loan status and book popularity.
- Enable quick access to overdue loan details, member activity, and recent popular books.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`.
- PostgreSQL materialized view: `popular_books`.
- PostgreSQL index: `idx_popular_books_id`.

#### Schema
- **overdue_loans**
  - `loan_id` integer
  - `member_id` integer
  - `email` text
  - `title` text
  - `due_on` date
  - `days_overdue` integer
- **member_loan_stats**
  - `member_id` integer
  - `full_name` text
  - `loans_total` bigint
  - `loans_open` bigint
  - `last_loan_on` timestamp
- **popular_books**
  - `book_id` integer
  - `title` text
  - `author_name` text
  - `times_loaned` bigint
- **idx_popular_books_id**
  - Unique index on `popular_books(book_id)`.

#### Reads
- `overdue_loans`: reads from `loan`, `member`, `book`.
- `member_loan_stats`: reads from `member`, `loan`.
- `popular_books`: reads from `loan`, `book`, `author`.

#### Writes
- None (views and materialized view are read‑only; index creation does not write data).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- Views: none.
- Functions: none.
