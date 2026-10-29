### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:08:10Z
- G: 
- A: 0.0.0
- X: file
---
> Defines reporting views and a materialized view for a lending library to surface overdue loans, per-member loan statistics, and top-borrowed books.

#### Purpose
- Provide read-only reporting layers for loan operations: overdue detection, member activity summaries, and popularity analytics.
- Materialize the popular books ranking for faster repeated access.

#### Type
- `overdue_loans`: view (PostgreSQL)
- `member_loan_stats`: view (PostgreSQL)
- `popular_books`: materialized view (PostgreSQL)
- `idx_popular_books_id`: unique index

#### Schema
**overdue_loans**
- `loan_id` (from `loan`)
- `member_id` (from `member`)
- `email` (from `member`)
- `title` (from `book`)
- `due_on` (from `loan`)
- `days_overdue` (computed: `CURRENT_DATE - l.due_on`)

**member_loan_stats**
- `member_id` (from `member`)
- `full_name` (from `member`)
- `loans_total` (aggregate: `COUNT(l.loan_id)`)
- `loans_open` (aggregate: `COUNT(l.loan_id) FILTER (WHERE l.returned_on IS NULL)`)
- `last_loan_on` (aggregate: `MAX(l.loaned_on)`)

**popular_books**
- `book_id` (from `book`)
- `title` (from `book`)
- `author_name` (from `author.full_name`)
- `times_loaned` (aggregate: `COUNT(*)`)
- Unique index `idx_popular_books_id` on `book_id`

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
