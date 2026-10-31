### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:39:31Z
- G: 
- A: 0.0.0
- X: file
---
> Provides reporting views for a lending library, summarizing overdue loans, member loan statistics, and popular books.

#### Purpose
- Generate overdue loan details for overdue members.
- Summarize loan activity per member.
- Identify books frequently loaned in the past year.

#### Type
- PostgreSQL views and materialized view.

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

**Index**
- `idx_popular_books_id` on `popular_books(book_id)` (unique)

#### Reads
- `loan` (all columns used: `loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`)
- `member` (`member_id`, `email`, `full_name`)
- `book` (`book_id`, `title`, `author_id`)
- `author` (`author_id`, `full_name`)

#### Writes
- `popular_books` materialized view refreshed (implicit INSERT/UPDATE on refresh).

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Routine logic
- **`overdue_loans`**: joins `loan`, `member`, `book`; filters for unreturned loans past due date; calculates days overdue.
- **`member_loan_stats`**: left joins `loan` to `member`; aggregates total loans, open loans, and last loan date per member.
- **`popular_books`**: joins `loan`, `book`, `author`; filters loans in last 365 days; groups by book and author; retains books loaned at least 5 times.

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`.
- Index: `idx_popular_books_id`.
