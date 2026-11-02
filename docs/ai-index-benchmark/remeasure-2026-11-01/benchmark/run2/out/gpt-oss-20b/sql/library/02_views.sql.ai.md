### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:47:11Z
- G: 
- A: 0.0.0
- X: file
---
> Provides reporting views for a lending library, summarizing overdue loans, member loan statistics, and popular books.

#### Purpose
- Exposes aggregated loan data for reporting.
- Highlights overdue loans, member activity, and recent popular books.

#### Type
- PostgreSQL views and materialized view.
- `CREATE VIEW overdue_loans`, `CREATE VIEW member_loan_stats`, `CREATE MATERIALIZED VIEW popular_books`.
- `CREATE UNIQUE INDEX idx_popular_books_id`.

#### Schema
**overdue_loans**
- `loan_id` integer
- `member_id` integer
- `email` text
- `title` text
- `due_on` date
- `days_overdue` integer

**member_loan_stats**
- `member_id` integer
- `full_name` text
- `loans_total` bigint
- `loans_open` bigint
- `last_loan_on` timestamp

**popular_books**
- `book_id` integer
- `title` text
- `author_name` text
- `times_loaned` bigint

**Indexes**
- `idx_popular_books_id` on `popular_books(book_id)` (unique).

#### Reads
- `loan` (all columns used: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`).
- `member` (`member_id`, `email`, `full_name`).
- `book` (`book_id`, `title`, `author_id`).
- `author` (`author_id`, `full_name`).

#### Writes
- None (views and materialized view are read-only).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- Functions: `CURRENT_DATE`, `INTERVAL`.
- Index: `idx_popular_books_id` (created on materialized view).
