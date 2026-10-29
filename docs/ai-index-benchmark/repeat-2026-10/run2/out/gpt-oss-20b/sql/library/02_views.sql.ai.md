### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:14:14Z
- G: 
- A: 0.0.0
- X: file
---
> Provides reporting views for a lending library, including overdue loans, member loan statistics, and popular books.

#### Purpose
- Exposes aggregated loan data for reporting.
- Highlights overdue loans and popular books.
- Summarizes member loan activity.

#### Type
- PostgreSQL views and materialized view.

#### Schema
**overdue_loans**  
- `loan_id` integer  
- `member_id` integer  
- `email` text  
- `title` text  
- `due_on` date  
- `days_overdue` integer  

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

#### Reads
- `loan` (all columns used: loan_id, member_id, book_id, due_on, returned_on, loaned_on)
- `member` (member_id, email, full_name)
- `book` (book_id, title, author_id)
- `author` (author_id, full_name)

#### Writes
- None (views are read‑only).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`
- Index: `idx_popular_books_id` on `popular_books(book_id)`

#### Routine logic
- **overdue_loans**: selects loans not returned and past due, calculates days overdue.
- **member_loan_stats**: aggregates total loans, open loans, and last loan date per member.
- **popular_books**: counts loans per book in the last year, filters for at least 5 loans, includes author name.
