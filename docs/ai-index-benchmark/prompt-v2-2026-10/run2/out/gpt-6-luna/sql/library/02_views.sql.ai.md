### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:45:54Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and books loaned at least five times in the past year.

#### Purpose
- `overdue_loans` identifies unreturned loans past their due date and calculates days overdue.
- `member_loan_stats` summarizes member borrowing; `popular_books` reports recent high-loan titles and authors.

#### Type
- Views: `overdue_loans`, `member_loan_stats` — PostgreSQL.
- Materialized view: `popular_books` — PostgreSQL.
- Index: `idx_popular_books_id` — PostgreSQL.

#### Schema
- `overdue_loans`:
  - `loan_id` — type not specified; from `loan.loan_id`
  - `member_id` — type not specified; from `member.member_id`
  - `email` — type not specified; from `member.email`
  - `title` — type not specified; from `book.title`
  - `due_on` — type not specified; from `loan.due_on`
  - `days_overdue` — derived from `CURRENT_DATE - loan.due_on`
- `member_loan_stats`:
  - `member_id` — type not specified; from `member.member_id`
  - `full_name` — type not specified; from `member.full_name`
  - `loans_total` — count of `loan.loan_id`
  - `loans_open` — count of `loan.loan_id` where `loan.returned_on IS NULL`
  - `last_loan_on` — maximum `loan.loaned_on`
- `popular_books`:
  - `book_id` — type not specified; from `book.book_id`
  - `title` — type not specified; from `book.title`
  - `author_name` — type not specified; from `author.full_name`
  - `times_loaned` — count of matching loan rows
- Index: `idx_popular_books_id` — UNIQUE on `popular_books (book_id)`.

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.loan_id`, `loan.member_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books`: `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Writes
- Defines views `overdue_loans` and `member_loan_stats`.
- Creates and populates materialized view `popular_books`; no explicit DML row count is specified.
- Creates unique index `idx_popular_books_id` on `popular_books`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- View: `popular_books` is indexed by `idx_popular_books_id`.
