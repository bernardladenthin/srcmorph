### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:02:19Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> A set of reporting views and a materialized view for a library system, summarizing overdue loans, member loan statistics, and popular books.

#### Purpose
- Provide read‑only reports on loan status and book popularity.
- Enable quick access to overdue loan details, member activity, and recent high‑loan books.

#### Type
- PostgreSQL
- Views: `overdue_loans`, `member_loan_stats`
- Materialized view: `popular_books`
- Index: `idx_popular_books_id`

#### Schema
**overdue_loans**
- `loan_id` integer
- `member_id` integer
- `email` text
- `title` text
- `due_on` date
- `days_overdue` integer
- Index: `idx_popular_books_id` (not applicable)

**member_loan_stats**
- `member_id` integer
- `full_name` text
- `loans_total` bigint
- `loans_open` bigint
- `last_loan_on` timestamp

**popular_books**
- `book_id` integer
- `title` text
- `author_name` text
- `times_loaned` bigint
- Index: `idx_popular_books_id` on `book_id`

#### Reads
- `loan` (`l.loan_id`, `l.member_id`, `l.book_id`, `l.due_on`, `l.returned_on`, `l.loaned_on`)
- `member` (`m.member_id`, `m.email`, `m.full_name`)
- `book` (`b.book_id`, `b.title`, `b.author_id`)
- `author` (`a.author_id`, `a.full_name`)

#### Writes
- None (all objects are read‑only).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
- Functions: `CURRENT_DATE`, `INTERVAL`
- Operators: `-` (date subtraction), `FILTER`, `HAVING`

---
