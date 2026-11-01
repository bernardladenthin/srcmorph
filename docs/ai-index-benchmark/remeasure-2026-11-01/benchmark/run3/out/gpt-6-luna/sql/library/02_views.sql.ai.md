### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:00:48Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and books loaned at least five times in the past year.

#### Purpose
- Reports unreturned loans past their due date, with borrower, book, and days overdue.
- Aggregates member loan statistics and identifies popular books from the past year.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`; materialized view: `popular_books`; unique index: `idx_popular_books_id`.

#### Schema
- `overdue_loans` (view):
  - `loan_id` — source type from `loan.loan_id`
  - `member_id` — source type from `member.member_id`
  - `email` — source type from `member.email`
  - `title` — source type from `book.title`
  - `due_on` — source type from `loan.due_on`
  - `days_overdue` — integer expression (`CURRENT_DATE - due_on`)
- `member_loan_stats` (view):
  - `member_id` — source type from `member.member_id`
  - `full_name` — source type from `member.full_name`
  - `loans_total` — bigint (`COUNT`)
  - `loans_open` — bigint (`COUNT ... FILTER`)
  - `last_loan_on` — source type from `loan.loaned_on` (`MAX`)
- `popular_books` (materialized view):
  - `book_id` — source type from `book.book_id`
  - `title` — source type from `book.title`
  - `author_name` — source type from `author.full_name`
  - `times_loaned` — bigint (`COUNT`)
- Index: `idx_popular_books_id` — unique B-tree on `popular_books (book_id)`.

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.loan_id`, `loan.member_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books`: `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Writes
- Creates view definitions `overdue_loans` and `member_loan_stats`.
- Creates and populates materialized view `popular_books` from its query.
- Creates unique index `idx_popular_books_id` on `popular_books`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
