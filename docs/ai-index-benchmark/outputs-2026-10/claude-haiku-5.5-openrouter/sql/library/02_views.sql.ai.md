### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:35:37Z
- G: 
- A: 0.0.0
- X: file
---
> Reporting views over a lending library's loan, member, book and author data: overdue loans, per-member loan statistics, and popular books over the last year.

#### Purpose
- Provides three read-only reporting objects for a lending library (PostgreSQL).
- `overdue_loans` lists unreturned loans past due; `member_loan_stats` summarizes loans per member; `popular_books` ranks frequently loaned titles.

#### Type
- Three views and one unique index, PostgreSQL dialect.
- `overdue_loans`: view.
- `member_loan_stats`: view.
- `popular_books`: materialized view.
- `idx_popular_books_id`: unique index on `popular_books`.

#### Schema
- `overdue_loans` columns (derived, no physical schema):
  - `loan_id`, `member_id`, `email`, `title`, `due_on`
  - `days_overdue` = `CURRENT_DATE - due_on`
- `member_loan_stats` columns:
  - `member_id`, `full_name`
  - `loans_total` = `COUNT(loan_id)`
  - `loans_open` = `COUNT(loan_id) FILTER (WHERE returned_on IS NULL)`
  - `last_loan_on` = `MAX(loaned_on)`
- `popular_books` columns:
  - `book_id`, `title`, `author_name` (from `author.full_name`), `times_loaned` = `COUNT(*)`
  - Unique index `idx_popular_books_id` on (`book_id`)
- Filters and grouping:
  - `overdue_loans`: `returned_on IS NULL` and `due_on < CURRENT_DATE`
  - `member_loan_stats`: `GROUP BY member_id, full_name`
  - `popular_books`: `loaned_on >= CURRENT_DATE - 365 days`, `GROUP BY book_id, title, full_name`, `HAVING COUNT(*) >= 5`

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`
- `member`: `member_id`, `email`, `full_name`
- `book`: `book_id`, `title`, `author_id`
- `author`: `author_id`, `full_name`

#### Writes
- None. Views are read-only. `popular_books` stores materialized data and is refreshed externally.

#### Relationships
- `loan.member_id -> member.member_id` (inner join in `overdue_loans`, left join in `member_loan_stats`)
- `loan.book_id -> book.book_id` (inner join in `overdue_loans`, `popular_books`)
- `book.author_id -> author.author_id` (inner join in `popular_books`)

#### Routine logic
- Not applicable. No procedures, functions, or triggers in this file.

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
- Views: `overdue_loans`, `member_loan_stats`
- Materialized view: `popular_books`
- Index: `idx_popular_books_id`
- Functions and keywords: `CURRENT_DATE`, `COUNT`, `MAX`, `INTERVAL`
