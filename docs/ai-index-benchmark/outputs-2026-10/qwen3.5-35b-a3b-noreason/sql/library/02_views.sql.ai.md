### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:56:57Z
- G: 
- A: 0.0.0
- X: file
---
> Defines reporting views and a materialized view for a library lending system to track overdue loans, member statistics, and popular books.

#### Purpose
- Provides read-only reporting views for overdue loans, member loan statistics, and popular book trends.
- Materializes popular book data with a unique index for efficient querying.

#### Type
- View: `overdue_loans` (PostgreSQL)
- View: `member_loan_stats` (PostgreSQL)
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
- `loans_open` (count of `loan_id` where `returned_on` IS NULL)
- `last_loan_on` (MAX of `loaned_on`)

**Materialized View: popular_books**
- `book_id` (from `book`)
- `title` (from `book`)
- `author_name` (from `author`)
- `times_loaned` (count of rows)
- **Index**: `idx_popular_books_id` UNIQUE on `book_id`

#### Reads
- `loan` (columns: `loan_id`, `member_id`, `book_id`, `returned_on`, `due_on`, `loaned_on`)
- `member` (columns: `member_id`, `email`, `full_name`)
- `book` (columns: `book_id`, `title`, `author_id`)
- `author` (columns: `author_id`, `full_name`)

#### Writes
- `popular_books` (Materialized View)

#### Relationships
- `loan.member_id` -> `member.member_id`
- `loan.book_id` -> `book.book_id`
- `book.author_id` -> `author.author_id`

#### Routine logic
- **overdue_loans**: Joins `loan`, `member`, and `book`; filters for unreturned items (`returned_on IS NULL`) where `due_on` is in the past.
- **member_loan_stats**: Left joins `member` to `loan`; groups by member to count total loans, open loans, and find the most recent loan date.
- **popular_books**: Joins `loan`, `book`, and `author`; filters loans from the last 365 days; groups by book/author; filters groups with 5 or more loans.

#### Dependencies
- `loan`
- `member`
- `book`
- `author`
- `popular_books` (referenced by `idx_popular_books_id`)
