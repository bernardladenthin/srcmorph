### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:08:10Z
- G: 
- A: 0.0.0
- X: file
---
> Provides reporting views for a lending library, summarizing overdue loans, member loan statistics, and popular books.

#### Purpose
- Generate overdue loan details for overdue members.
- Summarize loan activity per member.
- Identify books frequently loaned in the past year.

#### Type
- View: `overdue_loans`
- View: `member_loan_stats`
- Materialized view: `popular_books`
- Index: `idx_popular_books_id`

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

**idx_popular_books_id**
- Unique index on `popular_books(book_id)`

#### Reads
- `loan` (`loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`)
- `member` (`member_id`, `email`, `full_name`)
- `book` (`book_id`, `title`, `author_id`)
- `author` (`author_id`, `full_name`)

#### Writes
- None (views and materialized view are read-only; index creation writes metadata).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
- Functions: `CURRENT_DATE`, `CURRENT_DATE - INTERVAL '365 days'`
- Index: `idx_popular_books_id` on `popular_books`

---
