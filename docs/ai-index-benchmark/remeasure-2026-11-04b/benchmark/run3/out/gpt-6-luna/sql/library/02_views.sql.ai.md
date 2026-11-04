### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:06:27Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Provides lending reports for overdue loans, member borrowing activity, and frequently loaned books.

#### Purpose
- Reports open loans past their due dates with member contact and book title.
- Aggregates member loan activity and identifies books loaned at least five times in the past year.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`.
- PostgreSQL materialized view: `popular_books`.
- Index: `idx_popular_books_id`.

#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (`CURRENT_DATE - due_on`).
- `member_loan_stats`: `member_id`, `full_name`, `loans_total`, `loans_open`, `last_loan_on`.
- `popular_books`: `book_id`, `title`, `author_name`, `times_loaned`.
- Unique index `idx_popular_books_id` on `popular_books (book_id)`.

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`.
- `member`: `member_id`, `email`, `full_name`.
- `book`: `book_id`, `title`, `author_id`.
- `author`: `author_id`, `full_name`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
