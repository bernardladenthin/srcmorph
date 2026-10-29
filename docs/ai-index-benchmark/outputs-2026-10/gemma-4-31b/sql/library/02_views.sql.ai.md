### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:33:49Z
- G: 
- A: 0.0.0
- X: file
---
> Reporting views for tracking overdue loans, member borrowing statistics, and high-demand books in a lending library.

#### Purpose
* Identify overdue books and associated member contact details.
* Aggregate borrowing activity and status per member.
* Track books loaned at least 5 times within the last 365 days.

#### Type
View, Materialized View, Index (PostgreSQL).

#### Schema
`overdue_loans` (View)
* `loan_id`
* `member_id`
* `email`
* `title`
* `due_on`
* `days_overdue`

`member_loan_stats` (View)
* `member_id`
* `full_name`
* `loans_total`
* `loans_open`
* `last_loan_on`

`popular_books` (Materialized View)
* `book_id` (Unique Index: `idx_popular_books_id`)
* `title`
* `author_name`
* `times_loaned`

#### Reads
* `loan`: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`.
* `member`: `member_id`, `email`, `full_name`.
* `book`: `book_id`, `title`, `author_id`.
* `author`: `author_id`, `full_name`.

#### Relationships
* `loan.member_id -> member.member_id`
* `loan.book_id -> book.book_id`
* `book.author_id -> author.author_id`

#### Dependencies
* `loan`
* `member`
* `book`
* `author`
