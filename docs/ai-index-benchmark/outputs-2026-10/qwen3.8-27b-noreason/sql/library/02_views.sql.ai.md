### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:58:28Z
- G: 
- A: 0.0.0
- X: file
---
> Defines reporting views and a materialized view for a lending library to surface overdue loans, per-member loan statistics, and top-loaned books.

#### Purpose
- Provide read-only reporting layers for library operations: overdue loan detection, member borrowing history, and popular book analytics.
- Materialize the popular books query for performance with a unique index on `book_id`.

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
- `loans_total` (computed: `COUNT(l.loan_id)`)
- `loans_open` (computed: `COUNT(l.loan_id) FILTER (WHERE l.returned_on IS NULL)`)
- `last_loan_on` (computed: `MAX(l.loaned_on)`)

**popular_books**
- `book_id` (from `book`)
- `title` (from `book`)
- `author_name` (from `author.full_name`)
- `times_loaned` (computed: `COUNT(*)`)
- Index: `idx_popular_books_id` UNIQUE on `book_id`

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`
- `member`: `member_id`, `email`, `full_name`
- `book`: `book_id`, `title`, `author_id`
- `author`: `author_id`, `full_name`

#### Writes
- None (all objects are read-only views or materialized views; no DML targets).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- `loan`
- `member`
- `book`
- `author`
