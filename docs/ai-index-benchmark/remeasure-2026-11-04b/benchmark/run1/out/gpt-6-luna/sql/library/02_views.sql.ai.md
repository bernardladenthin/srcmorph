### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:48:38Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Provides lending-library reports for overdue loans, member borrowing activity, and frequently borrowed books.

#### Purpose
- Exposes overdue-loan details and per-member loan statistics.
- Materializes books loaned at least five times in the past 365 days.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`; materialized view: `popular_books`.
- Index: `idx_popular_books_id`.

#### Schema
- `overdue_loans`:
  - `loan_id` — inherited from `loan.loan_id`
  - `member_id` — inherited from `member.member_id`
  - `email` — inherited from `member.email`
  - `title` — inherited from `book.title`
  - `due_on` — inherited from `loan.due_on`
  - `days_overdue` — integer expression, `CURRENT_DATE - due_on`
- `member_loan_stats`:
  - `member_id` — inherited from `member.member_id`
  - `full_name` — inherited from `member.full_name`
  - `loans_total` — bigint, `COUNT(loan_id)`
  - `loans_open` — bigint, filtered count where `returned_on IS NULL`
  - `last_loan_on` — type inherited from `loan.loaned_on`, `MAX(loaned_on)`
- `popular_books`:
  - `book_id` — inherited from `book.book_id`
  - `title` — inherited from `book.title`
  - `author_name` — inherited from `author.full_name`
  - `times_loaned` — bigint, `COUNT(*)`
- Index: `idx_popular_books_id` — unique B-tree on `popular_books (book_id)`.

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.loan_id`, `loan.member_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books`: `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Writes
- `popular_books`: materialized result rows from its defining query; rows meet the 365-day loan window and `COUNT(*) >= 5` condition.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`
- `popular_books.book_id` has a unique index.

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
