### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:51:29Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> A set of reporting views and a materialized view for a library system, summarizing overdue loans, member loan statistics, and popular books.

#### Purpose
- Provide aggregated loan and book data for reporting and analytics.
- Enable quick access to overdue loan details, member loan summaries, and recent popular books.

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
  - **Index**: `idx_popular_books_id` on `popular_books(book_id)` (unique)

#### Reads
- `overdue_loans`: `loan`, `member`, `book`.
- `member_loan_stats`: `member`, `loan`.
- `popular_books`: `loan`, `book`, `author`.

#### Writes
- `popular_books`: materialized view refreshed via `REFRESH MATERIALIZED VIEW` (not shown in source).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- Views: none.
- Functions: none.
