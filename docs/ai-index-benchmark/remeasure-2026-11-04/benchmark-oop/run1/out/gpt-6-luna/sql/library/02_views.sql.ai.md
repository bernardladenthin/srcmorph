### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:21:29Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Provides lending-library reports for overdue loans, member borrowing activity, and frequently loaned books.

#### Purpose
- Exposes overdue-loan and member-loan statistics for reporting.
- Materializes recently popular books based on loan counts.

#### Type
- Views and materialized view; PostgreSQL.

#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (`CURRENT_DATE - l.due_on`).
- `member_loan_stats`: `member_id`, `full_name`, `loans_total` (`COUNT(l.loan_id)`), `loans_open` (count filtered to unreturned loans), `last_loan_on` (`MAX(l.loaned_on)`).
- `popular_books` (materialized): `book_id`, `title`, `author_name` (`a.full_name`), `times_loaned` (`COUNT(*)`); includes books loaned at least 5 times in the last 365 days.
- Index `idx_popular_books_id` on `popular_books (book_id)`, UNIQUE.

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.loan_id`, `loan.member_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books`: `loan.loaned_on`, `loan.book_id`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Writes
- Creates `overdue_loans` and `member_loan_stats` views.
- Creates and populates materialized view `popular_books`.
- Creates unique index `idx_popular_books_id` on `popular_books`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
