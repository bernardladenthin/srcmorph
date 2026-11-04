### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:53:52Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> A set of reporting views for a library system, summarizing overdue loans, member loan statistics, and popular books.

#### Purpose
- Provide read‑only reports on loan status and book popularity.
- Enable quick access to overdue loan details, member loan summaries, and recent popular books.

#### Type
- PostgreSQL views and materialized view.
- `CREATE VIEW overdue_loans`, `CREATE VIEW member_loan_stats`, `CREATE MATERIALIZED VIEW popular_books`.
- `CREATE UNIQUE INDEX idx_popular_books_id` on `popular_books`.

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
  - Unique index `idx_popular_books_id` on `(book_id)`.

#### Reads
- `overdue_loans`: reads from `loan`, `member`, `book`.
- `member_loan_stats`: reads from `member`, `loan`.
- `popular_books`: reads from `loan`, `book`, `author`.

#### Writes
- None (views are read‑only).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- Views: none.
- Index: `idx_popular_books_id` on `popular_books`.
