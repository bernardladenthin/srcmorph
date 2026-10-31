### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:54:37Z
- G: 
- A: 0.0.0
- X: file
---
> Provides reporting views and a materialized view for a library system, summarizing overdue loans, member loan statistics, and popular books.

#### Purpose
- Exposes aggregated loan and book data for reporting.
- Enables quick access to overdue loans, member statistics, and popular books.

#### Type
- PostgreSQL
- Views: `overdue_loans`, `member_loan_stats`
- Materialized view: `popular_books`
- Index: `idx_popular_books_id`

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
- `overdue_loans`: `loan`, `member`, `book`
- `member_loan_stats`: `member`, `loan`
- `popular_books`: `loan`, `book`, `author`

#### Writes
- None (views and materialized view are read-only; materialized view is refreshed externally).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
- Functions: `CURRENT_DATE`, `CURRENT_DATE - INTERVAL '365 days'`
- Index: `idx_popular_books_id` on `popular_books` (created in this file)
