### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:19:45Z
- G: 
- A: 0.0.0
- X: file
---
> Provides reporting views for a library system, summarizing overdue loans, member loan statistics, and popular books.

#### Purpose
- Generate analytical views for library reporting.
- Track overdue loans, member activity, and recent popular books.

#### Type
- PostgreSQL views and materialized view.
- Index: `idx_popular_books_id` on `popular_books`.

#### Schema
- **overdue_loans**  
  - `loan_id` integer  
  - `member_id` integer  
  - `email` text  
  - `title` text  
  - `due_on` date  
  - `days_overdue` integer  
  - *No primary key; derived from joins.*

- **member_loan_stats**  
  - `member_id` integer  
  - `full_name` text  
  - `loans_total` bigint  
  - `loans_open` bigint  
  - `last_loan_on` timestamp  
  - *No primary key; derived from aggregation.*

- **popular_books** (materialized view)  
  - `book_id` integer  
  - `title` text  
  - `author_name` text  
  - `times_loaned` bigint  
  - *Unique index `idx_popular_books_id` on `book_id`.*

#### Reads
- `loan` (`loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`)
- `member` (`member_id`, `email`, `full_name`)
- `book` (`book_id`, `title`, `author_id`)
- `author` (`author_id`, `full_name`)

#### Writes
- None (views are read‑only).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- Index: `idx_popular_books_id` on `popular_books`.
