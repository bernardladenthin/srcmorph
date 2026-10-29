### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:21:09Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and frequently loaned books.

#### Purpose
- Reports unreturned loans past their due date, including days overdue.
- Summarizes member loan activity and identifies books loaned at least five times in the past 365 days.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`
- PostgreSQL materialized view: `popular_books`
- Index: `idx_popular_books_id`

#### Schema
- `overdue_loans`:
  - `loan_id` type inherited from `loan.loan_id`
  - `member_id` type inherited from `member.member_id`
  - `email` type inherited from `member.email`
  - `title` type inherited from `book.title`
  - `due_on` type inherited from `loan.due_on`
  - `days_overdue` integer; `CURRENT_DATE - due_on`
- `member_loan_stats`:
  - `member_id` type inherited from `member.member_id`
  - `full_name` type inherited from `member.full_name`
  - `loans_total` bigint; count of matching loans
  - `loans_open` bigint; count of loans where `returned_on IS NULL`
  - `last_loan_on` type inherited from `loan.loaned_on`; maximum loan date
- `popular_books`:
  - `book_id` type inherited from `book.book_id`
  - `title` type inherited from `book.title`
  - `author_name` type inherited from `author.full_name`
  - `times_loaned` bigint; loan count
- `popular_books.book_id` has unique index `idx_popular_books_id`.

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.loan_id`, `loan.member_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books`: `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
- Index: `idx_popular_books_id` on `popular_books(book_id)`
