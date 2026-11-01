### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:21:42Z
- G: 
- A: 0.0.0
- X: file
---
> Provides lending-library reports for overdue loans, member borrowing activity, and books loaned at least five times in the past year.

#### Purpose
- Reports unreturned loans past their due date, aggregate loan activity per member, and identify recently popular books.

#### Type
- PostgreSQL: views `overdue_loans`, `member_loan_stats`; materialized view `popular_books`; unique index `idx_popular_books_id`.

#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue`.
- `member_loan_stats`: `member_id`, `full_name`, `loans_total`, `loans_open`, `last_loan_on`.
- `popular_books`: `book_id`, `title`, `author_name`, `times_loaned`.
- Index `idx_popular_books_id` on `popular_books (book_id)`, unique; default index method.

#### Reads
- `overdue_loans` reads `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`; `member.member_id`, `member.email`; `book.book_id`, `book.title`.
- `member_loan_stats` reads `member.member_id`, `member.full_name`; `loan.member_id`, `loan.loan_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books` reads `loan.book_id`, `loan.loaned_on`; `book.book_id`, `book.title`, `book.author_id`; `author.author_id`, `author.full_name`.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
