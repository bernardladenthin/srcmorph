### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:53:11Z
- G: 
- A: 0.0.0
- X: file
---
> Reporting views for tracking overdue library loans, member borrowing statistics, and identifying popular books.

#### Purpose
* Identify books not returned by the due date for notification.
* Aggregate loan counts and activity per member.
* Cache a list of frequently borrowed books within the last year.

#### Type
* View (PostgreSQL)
* Materialized View (PostgreSQL)
* Index (PostgreSQL)

#### Schema
* **overdue_loans**: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue`
* **member_loan_stats**: `member_id`, `full_name`, `loans_total`, `loans_open`, `last_loan_on`
* **popular_books**: `book_id` (Unique Index), `title`, `author_name`, `times_loaned`

#### Reads
* **loan**: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`
* **member**: `member_id`, `email`, `full_name`
* **book**: `book_id`, `title`, `author_id`
* **author**: `author_id`, `full_name`

#### Relationships
* `loan.member_id` -> `member.member_id`
* `loan.book_id` -> `book.book_id`
* `book.author_id` -> `author.author_id`

#### Dependencies
* table `loan`
* table `member`
* table `book`
* table `author`
