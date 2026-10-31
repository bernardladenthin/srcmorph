### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:59:02Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and frequently borrowed books.

#### Purpose
- `overdue_loans` identifies unreturned loans past their due date and calculates days overdue.
- `member_loan_stats` summarizes member borrowing; `popular_books` lists books loaned at least five times in the past 365 days.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`
- PostgreSQL materialized view: `popular_books`
- Index: `idx_popular_books_id`

#### Schema
- `overdue_loans`:
  - `loan_id` (from `loan.loan_id`)
  - `member_id` (from `member.member_id`)
  - `email` (from `member.email`)
  - `title` (from `book.title`)
  - `due_on` (from `loan.due_on`)
  - `days_overdue` (expression: `CURRENT_DATE - loan.due_on`)
- `member_loan_stats`:
  - `member_id` (from `member.member_id`)
  - `full_name` (from `member.full_name`)
  - `loans_total` (count of `loan.loan_id`)
  - `loans_open` (count of `loan.loan_id` where `returned_on IS NULL`)
  - `last_loan_on` (maximum `loan.loaned_on`)
- `popular_books`:
  - `book_id` (from `book.book_id`)
  - `title` (from `book.title`)
  - `author_name` (from `author.full_name`)
  - `times_loaned` (count of matching loans)
- `idx_popular_books_id`: unique index on `popular_books (book_id)`.
- Column types and constraints are not declared in this source.

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`; `CURRENT_DATE`.
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.member_id`, `loan.loan_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books`: `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`; `CURRENT_DATE`.

#### Relationships
- `loan.member_id -> member.member_id` (`overdue_loans`, `member_loan_stats`)
- `loan.book_id -> book.book_id` (`overdue_loans`, `popular_books`)
- `book.author_id -> author.author_id` (`popular_books`)

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
