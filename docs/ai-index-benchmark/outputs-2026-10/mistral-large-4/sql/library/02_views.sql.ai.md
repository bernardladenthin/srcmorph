### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:42:34Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library reporting views for tracking overdue unreturned loans, aggregating per-member loan statistics, and identifying popular books loaned at least five times in the last 365 days.

#### Purpose
- `overdue_loans`: expose unreturned loans (`returned_on IS NULL`) past due (`due_on < CURRENT_DATE`) with computed `days_overdue`.
- `member_loan_stats`: per-member totals (`loans_total`), open loans (`loans_open` where `returned_on IS NULL`), and `last_loan_on` via LEFT JOIN aggregation.
- `popular_books`: books with `COUNT(*) >= 5` loans in the last 365 days (`loaned_on >= CURRENT_DATE - INTERVAL '365 days'`), joined to author name.
- Unique index `idx_popular_books_id` on `popular_books(book_id)`.

#### Type
- view `overdue_loans` (PostgreSQL)
- view `member_loan_stats` (PostgreSQL)
- materialized view `popular_books` (PostgreSQL)
- unique index `idx_popular_books_id` (PostgreSQL)

#### Schema
- View `overdue_loans`:
  - `loan_id` (from `loan.loan_id`)
  - `member_id` (from `member.member_id`)
  - `email` (from `member.email`)
  - `title` (from `book.title`)
  - `due_on` (from `loan.due_on`)
  - `days_overdue` (computed `CURRENT_DATE - loan.due_on`)
- View `member_loan_stats`:
  - `member_id` (from `member.member_id`)
  - `full_name` (from `member.full_name`)
  - `loans_total` (computed `COUNT(loan.loan_id)`)
  - `loans_open` (computed `COUNT(loan.loan_id) FILTER (WHERE loan.returned_on IS NULL)`)
  - `last_loan_on` (computed `MAX(loan.loaned_on)`)
- Materialized view `popular_books`:
  - `book_id` (from `book.book_id`)
  - `title` (from `book.title`)
  - `author_name` (from `author.full_name`)
  - `times_loaned` (computed `COUNT(*)`)
- Index `idx_popular_books_id`: UNIQUE on `popular_books(book_id)`

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`
- `member`: `member_id`, `email`, `full_name`
- `book`: `book_id`, `title`, `author_id`
- `author`: `author_id`, `full_name`

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
- Materialized view: `popular_books` (indexed by `idx_popular_books_id`)
