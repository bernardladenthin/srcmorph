### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:36:41Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and books with at least five loans in the past year.

#### Purpose
- Exposes current overdue loans and per-member loan statistics.
- Materializes recent popular-book counts for reporting.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`.
- PostgreSQL materialized view: `popular_books`.
- Index: `idx_popular_books_id`.

#### Schema
- `overdue_loans`:
  - `loan_id` type inherited from `loan.loan_id`
  - `member_id` type inherited from `member.member_id`
  - `email` type inherited from `member.email`
  - `title` type inherited from `book.title`
  - `due_on` type inherited from `loan.due_on`
  - `days_overdue` integer
- `member_loan_stats`:
  - `member_id` type inherited from `member.member_id`
  - `full_name` type inherited from `member.full_name`
  - `loans_total` bigint
  - `loans_open` bigint
  - `last_loan_on` type inherited from `loan.loaned_on`
- `popular_books`:
  - `book_id` type inherited from `book.book_id`
  - `title` type inherited from `book.title`
  - `author_name` type inherited from `author.full_name`
  - `times_loaned` bigint
- Index: `idx_popular_books_id` on `popular_books (book_id)`, unique; method defaults to PostgreSQL B-tree.

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.returned_on`, `loan.due_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.loan_id`, `loan.member_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books`: `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
