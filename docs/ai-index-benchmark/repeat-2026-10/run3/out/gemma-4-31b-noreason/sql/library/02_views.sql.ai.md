### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:29:13Z
- G: 
- A: 0.0.0
- X: file
---
> Reporting views for tracking overdue loans, member borrowing statistics, and popular book trends in a lending library.

#### Purpose
* Identify books not returned by their due date for notification.
* Aggregate loan counts and activity dates per library member.
* Track high-demand books based on loan frequency over the last year.

#### Type
View, Materialized View, Index (PostgreSQL).

#### Schema
**overdue_loans** (View)
* `loan_id` (from loan)
* `member_id` (from member)
* `email` (from member)
* `title` (from book)
* `due_on` (from loan)
* `days_overdue` (calculated: CURRENT_DATE - due_on)

**member_loan_stats** (View)
* `member_id` (from member)
* `full_name` (from member)
* `loans_total` (count of loan_id)
* `loans_open` (count of loan_id where returned_on IS NULL)
* `last_loan_on` (max of loaned_on)

**popular_books** (Materialized View)
* `book_id` (from book) - PK/Unique Index `idx_popular_books_id`
* `title` (from book)
* `author_name` (from author)
* `times_loaned` (count)

#### Reads
* `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`
* `member`: `member_id`, `email`, `full_name`
* `book`: `book_id`, `title`, `author_id`
* `author`: `author_id`, `full_name`

#### Relationships
* `loan.member_id` -> `member.member_id`
* `loan.book_id` -> `book.book_id`
* `book.author_id` -> `author.author_id`

#### Dependencies
* `loan` (table)
* `member` (table)
* `book` (table)
* `author` (table)
