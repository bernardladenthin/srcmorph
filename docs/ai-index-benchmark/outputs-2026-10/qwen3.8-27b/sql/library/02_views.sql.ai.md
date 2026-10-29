### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:43:31Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library reporting views: overdue loans, per-member loan statistics, and popular books (last 365 days).

#### Purpose
- `overdue_loans` — surfaces all unreturned loans past their due date with days-overdue count.
- `member_loan_stats` — per-member aggregate: total loans, open loans, last loan date.
- `popular_books` — materialized view of books loaned ≥ 5 times in the trailing 365 days.

#### Type
- `overdue_loans` — view (PostgreSQL)
- `member_loan_stats` — view (PostgreSQL)
- `popular_books` — materialized view (PostgreSQL)
- `idx_popular_books_id` — unique index on materialized view

#### Schema

**overdue_loans**
- `loan_id` — from `loan`
- `member_id` — from `member`
- `email` — from `member`
- `title` — from `book`
- `due_on` — from `loan`
- `days_overdue` — computed: `CURRENT_DATE - l.due_on`

**member_loan_stats**
- `member_id` — from `member`
- `full_name` — from `member`
- `loans_total` — `COUNT(l.loan_id)`
- `loans_open` — `COUNT(l.loan_id) FILTER (WHERE l.returned_on IS NULL)`
- `last_loan_on` — `MAX(l.loaned_on)`

**popular_books**
- `book_id` — from `book`
- `title` — from `book`
- `author_name` — from `author.full_name`
- `times_loaned` — `COUNT(*)`
- Unique index `idx_popular_books_id` on `book_id`

#### Reads

- `overdue_loans` reads: `loan` (loan_id, member_id, book_id, due_on, returned_on), `member` (member_id, email), `book` (book_id, title)
- `member_loan_stats` reads: `member` (member_id, full_name), `loan` (loan_id, member_id, returned_on, loaned_on)
- `popular_books` reads: `loan` (loan_id, book_id, loaned_on), `book` (book_id, title, author_id), `author` (author_id, full_name)

#### Writes
- None (all are read-only views / materialized view)

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- `loan`, `member`, `book`, `author` (base tables)
- `idx_popular_books_id` depends on `popular_books`
