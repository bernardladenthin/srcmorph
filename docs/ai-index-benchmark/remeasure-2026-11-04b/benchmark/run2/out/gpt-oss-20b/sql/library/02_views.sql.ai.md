### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:02:22Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Provides reporting views for a library system: overdue loans, member loan statistics, and popular books.

#### Purpose
- Exposes aggregated loan data for reporting.
- Highlights overdue loans, member activity, and recent popular books.

#### Type
- PostgreSQL views and materialized view.
- Index: `idx_popular_books_id` on `popular_books`.

#### Schema
**View `overdue_loans`**
- `loan_id` integer
- `member_id` integer
- `email` text
- `title` text
- `due_on` date
- `days_overdue` integer
- *No primary key; read‑only.*

**View `member_loan_stats`**
- `member_id` integer
- `full_name` text
- `loans_total` bigint
- `loans_open` bigint
- `last_loan_on` timestamp
- *No primary key; read‑only.*

**Materialized View `popular_books`**
- `book_id` integer
- `title` text
- `author_name` text
- `times_loaned` bigint
- *No primary key; read‑only.*

**Index `idx_popular_books_id`**
- Table: `popular_books`
- Columns: `book_id`
- Method: default (B‑tree)

#### Reads
- `loan` (`loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`)
- `member` (`member_id`, `email`, `full_name`)
- `book` (`book_id`, `title`, `author_id`)
- `author` (`author_id`, `full_name`)

#### Writes
- None (views and materialized view are read‑only).

#### Relationships
- `overdue_loans`: `loan.member_id -> member.member_id`, `loan.book_id -> book.book_id`
- `member_loan_stats`: `loan.member_id -> member.member_id`
- `popular_books`: `loan.book_id -> book.book_id`, `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
- Views: none
- Functions: none
- Sequences: none

---
