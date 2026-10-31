### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:32:50Z
- G: 
- A: 0.0.0
- X: file
---
> This file defines PostgreSQL reporting views for the lending library, surfacing overdue loans, member loan statistics, and popular books over the last year.

#### Type
- View: `overdue_loans`, `member_loan_stats`
- Materialized view: `popular_books`
- Index: `idx_popular_books_id` on `popular_books`
- Dialect: PostgreSQL (CURRENT_DATE, INTERVAL, FILTER, COUNTARI)

#### Schema
- `overdue_loans`
  - `loan_id` — from `loan.loan_id`
  - `member_id` — from `member.member_id`
  - `email` — from `member.email`
  - `title` — from `book.title`
  - `due_on` — from `loan.due_on`
  - `days_overdue` — computed as `CURRENT_DATE - l.due_on`
- `member_loan_stats`
  - `member_id` — from `member.member_id`
  - `full_name` — from `member.full_name`
  - `loans_total` — `COUNT(l.loan_id)`
  - `loans_open` — `COUNT(l.loan_id) FILTER (WHERE l.returned_on IS NULL)`
  - `last_loan_on` — `MAX(l.loaned_on)`
- `popular_books` (materialized)
  - `book_id` — from `book.book_id`, unique index `idx_popular_books_id`
  - `title` — from `book.title`
  - `author_name` — from `author.full_name`
  - `times_loaned` — `COUNT(*)`

#### Reads
- `overdue_loans` reads `loan` (`loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`), `member` (`member_id`, `email`), `book` (`book_id`, `title`)
- `member_loan_stats` reads `member` (`member_id`, `full_name`), `loan` (`loan_id`, `member_id`, `returned_on`, `loaned_on`)
- `popular_books` reads `loan` (`loan_id`, `book_id`, `loaned_on`, `returned_on`), `book` (`book_id`, `title`, `author_id`), `author` (`author_id`, `full_name`)

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Routine logic
- `overdue_loans`: joins `loan` to `member` and `book`; filters to unreturned loans (`returned_on IS NULL`) with `due_on < CURRENT_DATE`; computes `days_overdue` as difference between current date and `due_on`
- `member_loan_stats`: left joins `member` to `loan`; aggregates counts per member (`loans_total`, `loans_open`, `last_loan_on`); groups by `member_id`, `full_name`
- `popular_books`: joins `loan` to `book` and `author`; filters loans in last 365 days (`loaned_on >= CURRENT_DATE - INTERVAL '365 days'`); groups by `book_id`, `title`, `full_name`; having `COUNT(*) >= 5`; unique index on `book_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
