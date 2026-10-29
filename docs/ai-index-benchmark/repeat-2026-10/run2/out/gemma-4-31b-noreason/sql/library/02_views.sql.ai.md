### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:56:35Z
- G: 
- A: 0.0.0
- X: file
---
> Reporting views for tracking overdue library loans, member borrowing statistics, and popular book trends.

#### Purpose
* Identify books not returned by their due date for notification.
* Aggregate loan counts and activity timestamps per library member.
* Track highly-borrowed books over the last 365 days.

#### Type
View / Materialized View / Index (PostgreSQL).

#### Schema
* **overdue_loans** (View): `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue`.
* **member_loan_stats** (View): `member_id`, `full_name`, `loans_total`, `loans_open`, `last_loan_on`.
* **popular_books** (Materialized View): `book_id` (Unique Index), `title`, `author_name`, `times_loaned`.

#### Reads
* **loan**: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`.
* **member**: `member_id`, `email`, `full_name`.
* **book**: `book_id`, `title`, `author_id`.
* **author**: `author_id`, `full_name`.

#### Relationships
* `loan.member_id` -> `member.member_id`
* `loan.book_id` -> `book.book_id`
* `book.author_id` -> `author.author_id`

#### Dependencies
* `loan` (table)
* `member` (table)
* `book` (table)
* `author` (table)
