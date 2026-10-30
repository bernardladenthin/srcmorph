### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:31:38Z
- G: 
- A: 0.0.0
- X: file
---
> Reporting views for a lending library: overdue loans, per-member loan statistics, and books loaned most in the past 365 days.

#### Purpose
- Lists open loans past due date with member contact and book title.
- Summarizes loan counts and last loan date per member.
- Ranks books by loan count over the last 365 days, for at least 5 loans.

#### Type
- `overdue_loans`: view
- `member_loan_stats`: view
- `popular_books`: materialized view
- `idx_popular_books_id`: unique index on `popular_books (book_id)`
- Dialect: PostgreSQL (`FILTER`, `INTERVAL '365 days'`, `CREATE MATERIALIZED VIEW`)

#### Schema
- `overdue_loans` columns: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (`CURRENT_DATE - due_on`)
- `member_loan_stats` columns: `member_id`, `full_name`, `loans_total` (`COUNT(loan_id)`), `loans_open` (`COUNT(loan_id) FILTER (returned_on IS NULL)`), `last_loan_on` (`MAX(loaned_on)`)
- `popular_books` columns: `book_id`, `title`, `author_name` (`author.full_name`), `times_loaned` (`COUNT(*)`)
- Unique index: `idx_popular_books_id` on `popular_books.book_id`
- Column types not in source.

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`
- `member`: `member_id`, `email`, `full_name`
- `book`: `book_id`, `title`, `author_id`
- `author`: `author_id`, `full_name`

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`
- `member_loan_stats`: `member.member_id -> loan.member_id` (LEFT JOIN)

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
- Views: `overdue_loans`, `member_loan_stats`, `popular_books`
- Index: `idx_popular_books_id`
- Functions: `CURRENT_DATE`, `COUNT`, `MAX`
