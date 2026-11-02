### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:59:03Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and books with high loan counts over the past year.

#### Purpose
- `overdue_loans` reports unreturned loans past their due date, including days overdue.
- `member_loan_stats` summarizes each member’s borrowing; `popular_books` ranks books loaned at least five times in the past year.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`.
- PostgreSQL materialized view: `popular_books`.
- PostgreSQL index: `idx_popular_books_id`.

#### Schema
- `overdue_loans`: `loan_id` (source type unspecified), `member_id` (source type unspecified), `email` (source type unspecified), `title` (source type unspecified), `due_on` (source type unspecified), `days_overdue` (date subtraction; integer).
- `member_loan_stats`: `member_id` (source type unspecified), `full_name` (source type unspecified), `loans_total` (count; bigint), `loans_open` (filtered count; bigint), `last_loan_on` (maximum `loaned_on`; source type unspecified).
- `popular_books`: `book_id` (source type unspecified), `title` (source type unspecified), `author_name` (source type unspecified), `times_loaned` (count; bigint).
- Index `idx_popular_books_id` on `popular_books (book_id)`; unique, default method (B-tree).

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`.
- `member`: `member_id`, `email`, `full_name`.
- `book`: `book_id`, `title`, `author_id`.
- `author`: `author_id`, `full_name`.
- `CURRENT_DATE`; `INTERVAL '365 days'`.

#### Writes
- Creates `overdue_loans` and `member_loan_stats` views.
- Creates and populates materialized view `popular_books` from its query.
- Creates unique index `idx_popular_books_id` on `popular_books`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- Materialized view: `popular_books` (indexed by `idx_popular_books_id`).
