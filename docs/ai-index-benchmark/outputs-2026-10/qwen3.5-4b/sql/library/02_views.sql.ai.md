### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:03:55Z
- G: 
- A: 0.0.0
- X: file
---
> This file defines reporting views and a materialized view for tracking overdue loans, member loan statistics, and popular book titles in a lending library system.

#### Purpose
- Defines `overdue_loans` view to list loans not yet returned where the due date has passed.
- Defines `member_loan_stats` view to aggregate total and open loan counts per member.
- Defines `popular_books` materialized view to track books loaned frequently within the last year.
- Creates a unique index on `popular_books` for efficient lookups by book ID.

#### Type
- View (`overdue_loans`, `member_loan_stats`)
- Materialized View (`popular_books`)
- Index (`idx_popular_books_id`)
- Dialect: PostgreSQL

#### Schema
- **overdue_loans**: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (computed)
- **member_loan_stats**: `member_id`, `full_name`, `loans_total`, `loans_open`, `last_loan_on`
- **popular_books**: `book_id`, `title`, `author_name`, `times_loaned`
- **idx_popular_books_id**: Unique index on `popular_books(book_id)`

#### Reads
- `loan` table: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`
- `member` table: `member_id`, `email`, `full_name`
- `book` table: `book_id`, `title`, `author_id`
- `author` table: `author_id`, `full_name`

#### Writes
- None

#### Relationships
- `overdue_loans`: `member.member_id -> loan.member_id`, `loan.book_id -> book.book_id`
- `member_loan_stats`: `member.member_id -> loan.member_id` (LEFT JOIN)
- `popular_books`: `loan.book_id -> book.book_id`, `book.author_id -> author.author_id`

#### Routine logic
- `overdue_loans`: Filters loans where `returned_on` is NULL and `due_on` is before `CURRENT_DATE`, calculating `days_overdue`.
- `member_loan_stats`: Groups members by `member_id` and `full_name`, counting total loans and open loans, tracking the most recent `last_loan_on`.
- `popular_books`: Aggregates loan counts for books within the last 365 days, filtering to books with at least 5 loans, storing results in a materialized view.

#### Dependencies
- `loan`
- `member`
- `book`
- `author`
- `popular_books` (materialized view)
