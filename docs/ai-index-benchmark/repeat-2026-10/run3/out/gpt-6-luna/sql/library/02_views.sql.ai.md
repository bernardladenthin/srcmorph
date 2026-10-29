### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:21:34Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and books with high loan counts over the past year.

#### Purpose
- Report unreturned loans past their due date, with days overdue.
- Aggregate member loan history and identify books loaned at least five times in the last 365 days.

#### Type
- Views and materialized view; PostgreSQL.
- Unique index on `popular_books`.

#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (`CURRENT_DATE - due_on`).
- `member_loan_stats`: `member_id`, `full_name`, `loans_total` (`COUNT`), `loans_open` (`COUNT` filtered to unreturned loans), `last_loan_on` (`MAX(loaned_on)`).
- `popular_books` (materialized): `book_id`, `title`, `author_name` (`author.full_name`), `times_loaned` (`COUNT`).
- `idx_popular_books_id`: unique index on `popular_books(book_id)`.

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`.
- `member`: `member_id`, `email`, `full_name`.
- `book`: `book_id`, `title`, `author_id`.
- `author`: `author_id`, `full_name`.

#### Writes
- Creates views `overdue_loans`, `member_loan_stats`, and materialized view `popular_books`.
- Creates unique index `idx_popular_books_id` on `popular_books(book_id)`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- Index: `idx_popular_books_id` on `popular_books`.
