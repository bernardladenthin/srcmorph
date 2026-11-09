### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:29:44Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Provides lending-library reports for overdue loans, member borrowing activity, and books with at least five loans in the past year.

#### Purpose
- `overdue_loans` identifies unreturned loans past their due date and calculates days overdue.
- `member_loan_stats` summarizes borrowing per member; `popular_books` reports frequently loaned books from the past 365 days.

#### Type
- PostgreSQL: views `overdue_loans`, `member_loan_stats`; materialized view `popular_books`; index `idx_popular_books_id`.

#### Schema
- `overdue_loans` (view): `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (`CURRENT_DATE - due_on`).
- `member_loan_stats` (view): `member_id`, `full_name`, `loans_total`, `loans_open`, `last_loan_on`.
- `popular_books` (materialized view): `book_id`, `title`, `author_name`, `times_loaned`.
- Index `idx_popular_books_id`: unique, on `popular_books (book_id)`.

#### Reads
- `overdue_loans`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats`: `member.member_id`, `member.full_name`; `loan.loan_id`, `loan.member_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books`: `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Writes
- Creates materialized view `popular_books` from its query; creates unique index `idx_popular_books_id` on `popular_books`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
