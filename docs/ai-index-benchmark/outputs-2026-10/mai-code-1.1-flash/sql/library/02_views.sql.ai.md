### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:45:58Z
- G: 
- A: 0.0.0
- X: file
---
> This file defines PostgreSQL reporting views for lending-library operations, including overdue loans, member loan summaries, and popular-book analysis.

#### Purpose
- Expose operational reporting for loan delinquency and member borrowing patterns.
- Publish materialized popularity data for books borrowed in the last 365 days.

#### Type
- PostgreSQL: `overdue_loans` view.
- PostgreSQL: `member_loan_stats` view.
- PostgreSQL: `popular_books` materialized view.
- PostgreSQL: `idx_popular_books_id` unique index on `popular_books(book_id)`.

#### Schema
- `overdue_loans`: `loan_id`, `member_id`, `email`, `title`, `due_on`, `days_overdue` (derived: `CURRENT_DATE - loan.due_on`).
- `member_loan_stats`: `member_id`, `full_name`, `loans_total` (`COUNT(l.loan_id)`), `loans_open` (`COUNT(l.loan_id) FILTER (WHERE l.returned_on IS NULL)`), `last_loan_on` (`MAX(l.loaned_on)`).
- `popular_books`: `book_id`, `title`, `author_name`, `times_loaned` (`COUNT(*)`).
- `idx_popular_books_id`: unique index on `popular_books.book_id`.

#### Reads
- `overdue_loans` reads from `loan`: `loan.loan_id`, `loan.member_id`, `loan.book_id`, `loan.due_on`, `loan.returned_on`.
- `overdue_loans` reads from `member`: `member.member_id`, `member.email`.
- `overdue_loans` reads from `book`: `book.book_id`, `book.title`.
- `member_loan_stats` reads from `member`: `member.member_id`, `member.full_name`.
- `member_loan_stats` reads from `loan`: `loan.member_id`, `loan.loan_id`, `loan.returned_on`, `loan.loaned_on`.
- `popular_books` reads from `loan`: `loan.book_id`, `loan.loaned_on`.
- `popular_books` reads from `book`: `book.book_id`, `book.title`, `book.author_id`.
- `popular_books` reads from `author`: `author.author_id`, `author.full_name`.
- `idx_popular_books_id` reads the `popular_books` materialized view, indexed on `popular_books.book_id`.

#### Writes
- No `INSERT`, `UPDATE`, `DELETE`, or `MERGE` statements targeting base tables are present in this file.
- No trigger or routine writes are present.
- `idx_popular_books_id` creates a database index; no direct table row writes are defined in the file itself.

#### Relationships
- `loan.member_id -> member.member_id`
- `loan.book_id -> book.book_id`
- `book.author_id -> author.author_id`
- `member_loan_stats.member_id` derives from `member.member_id` and grouped `loan.member_id`
- `overdue_loans.member_id` derives from `loan.member_id -> member.member_id`
- `overdue_loans.book_id` is not projected, but `loan.book_id -> book.book_id` is used for `book.title`
- `popular_books.book_id` derives from `loan.book_id -> book.book_id`
- `popular_books.author_name` derives from `book.author_id -> author.author_id`

#### Routine logic
- `overdue_loans`:
  - Joins `loan` to `member` on `member.member_id = loan.member_id`.
  - Joins `loan` to `book` on `book.book_id = loan.book_id`.
  - Filters `loan.returned_on IS NULL` and `loan.due_on < CURRENT_DATE`.
  - Produces overdue loan rows with derived `days_overdue`.
- `member_loan_stats`:
  - Left joins `member` to `loan` on `loan.member_id = member.member_id`.
  - Aggregates by `member.member_id`, `member.full_name`.
  - Counts all `loan.loan_id` values and open loans where `loan.returned_on IS NULL`.
  - Reports `MAX(l.loaned_on)` as `last_loan_on`.
- `popular_books`:
  - Joins `loan` to `book` on `book.book_id = loan.book_id`.
  - Joins `book` to `author` on `author.author_id = book.author_id`.
  - Filters `loan.loaned_on >= CURRENT_DATE - INTERVAL '365 days'`.
  - Groups by `book.book_id`, `book.title`, `author.full_name`.
  - Keeps only groups with `COUNT(*) >= 5`.
- `idx_popular_books_id`:
  - Creates a unique index on `popular_books.book_id` to enforce uniqueness of the materialized view key.

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`, `popular_books`.
- Views: `overdue_loans`, `member_loan_stats`, `popular_books`.
- Functions / expressions: `CURRENT_DATE`, `COUNT`, `MAX`, `FILTER`, `INTERVAL`.
