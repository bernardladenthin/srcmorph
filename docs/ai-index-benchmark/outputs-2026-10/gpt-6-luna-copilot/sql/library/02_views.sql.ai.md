### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:35:58Z
- G: 
- A: 0.0.0
- X: file
---
> Defines lending-library reports for overdue loans, member borrowing activity, and frequently loaned books.

#### Purpose
- `overdue_loans` lists unreturned loans past their due date.
- `member_loan_stats` summarizes borrowing per member; `popular_books` stores books loaned at least five times in the past 365 days.

#### Type
- PostgreSQL: views `overdue_loans`, `member_loan_stats`; materialized view `popular_books`; unique index `idx_popular_books_id`.

#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (`CURRENT_DATE - due_on`); derived from source columns and expression.
- `member_loan_stats`: `member_id`, `full_name`, `loans_total`, `loans_open`, `last_loan_on`; aggregates loan records per member.
- `popular_books`: `book_id`, `title`, `author_name`, `times_loaned`; aggregated loan counts, filtered to counts ≥ 5.
- `idx_popular_books_id`: unique index on `popular_books(book_id)`.

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `returned_on`, `due_on`, `loaned_on`.
- `member`: `member_id`, `email`, `full_name`.
- `book`: `book_id`, `title`, `author_id`.
- `author`: `author_id`, `full_name`.

#### Writes
- Creates `popular_books` as a materialized view and creates `idx_popular_books_id`; no base-table DML.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Views depend on `loan`, `member`, `book`; `popular_books` also depends on `author`.
- Index `idx_popular_books_id` depends on `popular_books`.
