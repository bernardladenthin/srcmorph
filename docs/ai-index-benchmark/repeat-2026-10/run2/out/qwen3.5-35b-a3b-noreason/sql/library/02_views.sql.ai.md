### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:04:42Z
- G: 
- A: 0.0.0
- X: file
---
> Defines reporting views and materialized views for a lending library to track overdue loans, member statistics, and popular books.

#### Purpose
- Provides read-only reporting views for overdue loans, member loan statistics, and popular book trends.
- Materializes popular book data with a unique index for efficient querying.

#### Type
- View: `overdue_loans`, `member_loan_stats`
- Materialized View: `popular_books`
- Index: `idx_popular_books_id`
- Dialect: PostgreSQL

#### Schema
**View: overdue_loans**
- `loan_id` (from loan)
- `member_id` (from member)
- `email` (from member)
- `title` (from book)
- `due_on` (from loan)
- `days_overdue` (calculated: `CURRENT_DATE - due_on`)
- Filter: `returned_on IS NULL` AND `due_on < CURRENT_DATE`

**View: member_loan_stats**
- `member_id` (PK candidate)
- `full_name` (from member)
- `loans_total` (COUNT of `loan_id`)
- `loans_open` (COUNT of `loan_id` FILTER WHERE `returned_on IS NULL`)
- `last_loan_on` (MAX of `loaned_on`)
- Join: LEFT JOIN on `member` and `loan`

**Materialized View: popular_books**
- `book_id` (Indexed)
- `title` (from book)
- `author_name` (from author)
- `times_loaned` (COUNT of rows)
- Filter: `loaned_on >= CURRENT_DATE - INTERVAL '365 days'`
- Filter: `COUNT(*) >= 5`

**Index: idx_popular_books_id**
- Type: UNIQUE
- Target: `popular_books`
- Column: `book_id`

#### Reads
- `loan` (columns: `loan_id`, `member_id`, `book_id`, `returned_on`, `due_on`, `loaned_on`)
- `member` (columns: `member_id`, `email`, `full_name`)
- `book` (columns: `book_id`, `title`, `author_id`)
- `author` (columns: `author_id`, `full_name`)

#### Writes
- `popular_books` (Materialized View data population)

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`

#### Routine logic
- **overdue_loans**: Joins `loan`, `member`, `book`; filters for unreturned items past due date; calculates days overdue.
- **member_loan_stats**: Groups `member` by ID; counts total loans and open loans (where `returned_on` is null); finds most recent loan date.
- **popular_books**: Joins `loan`, `book`, `author`; filters loans from last 365 days; groups by book/author; filters for books loaned 5+ times; materializes result.
- **idx_popular_books_id**: Enforces uniqueness on `book_id` within the `popular_books` materialized view.

#### Dependencies
- `loan`
- `member`
- `book`
- `author`
- `popular_books` (referenced by `idx_popular_books_id`)
