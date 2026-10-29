### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:31:27Z
- G: 
- A: 0.0.0
- X: file
---
> This file defines reporting views and indexes for a lending library system to track overdue items, member statistics, and popular books.

#### Purpose
- Define reporting views for library loan analytics
- Create materialized view for popular book statistics with indexing

#### Type
- PostgreSQL views, materialized view, and index

#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (computed)
- `member_loan_stats`: `member_id`, `full_name`, `loans_total`, `loans_open`, `last_loan_on`
- `popular_books`: `book_id`, `title`, `author_name`, `times_loaned`
- `idx_popular_books_id`: `book_id` (unique)

#### Reads
- `overdue_loans`: `loan`, `member`, `book`
- `member_loan_stats`: `member`, `loan`
- `popular_books`: `loan`, `book`, `author`

#### Writes
- `popular_books` (materialized view storage)
- `idx_popular_books_id` (index structure)

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Routine logic
- `overdue_loans`: Filter `loan.returned_on IS NULL` AND `loan.due_on < CURRENT_DATE`
- `member_loan_stats`: Aggregate `COUNT(loan_id)` with `FILTER` for open loans
- `popular_books`: Filter `loan.loaned_on >= CURRENT_DATE - INTERVAL '365 days'`, HAVING `COUNT(*) >= 5`

#### Dependencies
- `loan`, `member`, `book`, `author`
