### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:43:10Z
- G: 
- A: 0.0.0
- X: file
---
I need to index the provided SQL source file according to the specific format.> Lending-library reporting views that surface overdue loans, per-member loan counts, and books borrowed often in the last year.

#### Purpose
- Report open loans past `due_on` with member contact and book title.
- Aggregate each member's total, open, and most recent loans.
- Materialize books loaned at least five times in the last 365 days, with author name.

#### Type
- View: `overdue_loans`, `member_loan_stats` (PostgreSQL).
- Materialized view: `popular_books` (PostgreSQL).
- Unique index: `idx_popular_books_id` on `popular_books (book_id)`.

#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (`CURRENT_DATE - l.due_on`).
- `member_loan_stats`: `member_id`, `full_name`, `loans_total` (`COUNT(l.loan_id)`), `loans_open` (`COUNT` FILTER `returned_on IS NULL`), `last_loan_on` (`MAX(l.loaned_on)`). Grouped by `member_id`, `full_name`.
- `popular_books`: `book_id`, `title`, `author_name` (`a.full_name`), `times_loaned` (`COUNT(*)`). Unique index `idx_popular_books_id` on `book_id`.

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`.
- `member`: `member_id`, `email`, `full_name`.
- `book`: `book_id`, `title`, `author_id`.
- `author`: `author_id`, `full_name`.

#### Writes
- None. Views and a unique index only; no INSERT, UPDATE, DELETE, or MERGE.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- `loan`, `member`, `book`, `author`
- `popular_books` (index target)
- PostgreSQL `CURRENT_DATE`, `INTERVAL '365 days'`, `FILTER`, materialized view
