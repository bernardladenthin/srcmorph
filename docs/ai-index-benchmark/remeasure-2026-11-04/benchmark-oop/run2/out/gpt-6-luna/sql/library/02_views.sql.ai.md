### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:36:59Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Provides lending-library reports for overdue loans, member borrowing activity, and books frequently loaned during the past year.

#### Purpose
- `overdue_loans`: reports unreturned loans past their due date.
- `member_loan_stats` and `popular_books`: summarize member activity and recent book popularity.

#### Type
- PostgreSQL views: `overdue_loans`, `member_loan_stats`.
- PostgreSQL materialized view: `popular_books`.
- Index: `idx_popular_books_id`.

#### Schema
- `overdue_loans`:
  - `loan_id` — source type
  - `member_id` — source type
  - `email` — source type
  - `title` — source type
  - `due_on` — source type
  - `days_overdue` — integer (`CURRENT_DATE - due_on`)
- `member_loan_stats`:
  - `member_id` — source type
  - `full_name` — source type
  - `loans_total` — bigint (`COUNT`)
  - `loans_open` — bigint (`COUNT ... FILTER`)
  - `last_loan_on` — same type as `loan.loaned_on` (`MAX`)
- `popular_books`:
  - `book_id` — source type
  - `title` — source type
  - `author_name` — same type as `author.full_name`
  - `times_loaned` — bigint (`COUNT`)
- Index `idx_popular_books_id`: unique B-tree on `popular_books (book_id)`.

#### Reads
- `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`.
- `member`: `member_id`, `email`, `full_name`.
- `book`: `book_id`, `title`, `author_id`.
- `author`: `author_id`, `full_name`.

#### Writes
- Defines materialized-view storage for `popular_books`, populated from its query.

#### Relationships
- `loan.member_id -> member.member_id` (`overdue_loans`, `member_loan_stats`).
- `loan.book_id -> book.book_id` (`overdue_loans`, `popular_books`).
- `book.author_id -> author.author_id` (`popular_books`).
