### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:52:59Z
- G: 
- A: 0.0.0
- X: file
---
> Provides reporting views and a materialized view for a lending library, summarizing overdue loans, member loan statistics, and popular books.

#### Purpose
- Exposes aggregated loan and member data for reporting.
- Tracks overdue loans, member activity, and recent popular books.

#### Type
- PostgreSQL view, view, materialized view, index.

#### Schema
**View `overdue_loans`**
- `loan_id` integer
- `member_id` integer
- `email` text
- `title` text
- `due_on` date
- `days_overdue` integer

**View `member_loan_stats`**
- `member_id` integer
- `full_name` text
- `loans_total` bigint
- `loans_open` bigint
- `last_loan_on` timestamp

**Materialized View `popular_books`**
- `book_id` integer
- `title` text
- `author_name` text
- `times_loaned` bigint

**Index `idx_popular_books_id`**
- Unique index on `popular_books(book_id)`.

#### Reads
- `loan` (all columns referenced)
- `member` (member_id, email, full_name)
- `book` (book_id, title, author_id)
- `author` (author_id, full_name)

#### Writes
- None (views and materialized view are read-only).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- Functions: `CURRENT_DATE`, `CURRENT_DATE - INTERVAL '365 days'`.

---
