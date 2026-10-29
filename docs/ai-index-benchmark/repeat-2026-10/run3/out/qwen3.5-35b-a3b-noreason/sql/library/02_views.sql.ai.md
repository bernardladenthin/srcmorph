### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:43:39Z
- G: 
- A: 0.0.0
- X: file
---
> Defines reporting views and materialized views for a lending library to track overdue loans, member statistics, and popular book trends.

#### Purpose
- Provides read-only reporting views for overdue loans, member loan statistics, and popular book analysis.
- Materializes popular book data with a unique index for efficient querying.

#### Type
- View: `overdue_loans`, `member_loan_stats` (PostgreSQL)
- Materialized View: `popular_books` (PostgreSQL)
- Index: `idx_popular_books_id` (PostgreSQL)

#### Schema
**View: overdue_loans**
- `loan_id` (from `loan`)
- `member_id` (from `member`)
- `email` (from `member`)
- `title` (from `book`)
- `due_on` (from `loan`)
- `days_overdue` (calculated: `CURRENT_DATE - due_on`)

**View: member_loan_stats**
- `member_id` (from `member`)
- `full_name` (from `member`)
- `loans_total` (count of `loan_id`)
- `loans_open` (count of `loan_id` where `returned_on` is NULL)
- `last_loan_on` (max of `loaned_on`)

**Materialized View: popular_books**
- `book_id` (from `book`)
- `title` (from `book`)
- `author_name` (from `author`)
- `times_loaned` (count of rows)
- **Index**: `idx_popular_books_id` on `book_id` (UNIQUE)

#### Reads
- `loan` (columns: `loan_id`, `member_id`, `book_id`, `returned_on`, `due_on`, `loaned_on`)
- `member` (columns: `member_id`, `email`, `full_name`)
- `book` (columns: `book_id`, `title`, `author_id`)
- `author` (columns: `author_id`, `full_name`)

#### Writes
- `popular_books` (Materialized View write via `CREATE MATERIALIZED VIEW`)

#### Relationships
- `loan.member_id` -> `member.member_id`
- `loan.book_id` -> `book.book_id`
- `book.author_id` -> `author.author_id`

#### Routine logic
- **overdue_loans**: Joins `loan`, `member`, and `book`; filters for unreturned loans where `due_on` is in the past; calculates days overdue.
- **member_loan_stats**: Left joins `member` and `loan`; groups by member; counts total loans and open loans; finds most recent loan date.
- **popular_books**: Joins `loan`, `book`, and `author`; filters loans from the last 365 days; groups by book/author; filters for books loaned 5+ times.

#### Dependencies
- `loan`
- `member`
- `book`
- `author`
- `popular_books` (referenced by `idx_popular_books_id`)
