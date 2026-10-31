### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:44:55Z
- G: 
- A: 0.0.0
- X: file
---
> A set of reporting views and a materialized view for a lending library that surface overdue loans, member loan statistics, and popular books.

#### Purpose
- Show loans that are past due without being returned.
- Provide per‑member loan totals and currently open loans.
- List books loaned frequently in the last year meeting a minimum count.
- Enforce uniqueness on the `popular_books` identifier via an index.

#### Type
view / materialized view / index (PostgreSQL)

#### Schema
*overdue_loans* (view) – no stored schema; selects from loan, member, book.  
*member_loan_stats* (view) – no stored schema; selects from member, loan.  
*popular_books* (materialized view) – columns: `book_id` (PK implied), `title`, `author_name`, `times_loaned`.  
*idx_popular_books_id* (unique index) – on `popular_books(book_id)`.

#### Reads
- `overdue_loans`: loan, member, book
- `member_loan_stats`: member, loan
- `popular_books`: loan, book, author
- `idx_popular_books_id`: reads `popular_books`

#### Writes
- No INSERT/UPDATE/DELETE statements present; views and materialized view are read‑only. The unique index is a write‑time constraint but does not modify data.

#### Relationships
- `loan.member_id` → `member.member_id`
- `loan.book_id` → `book.book_id`
- `book.author_id` → `author.author_id`
- `member_loan_stats` uses `member.member_id` → `loan.member_id`
- `popular_books` uses `loan.book_id` → `book.book_id` and `book.author_id` → `author.author_id`

#### Routine logic
- `overdue_loans`: filters loans where `returned_on IS NULL` and `due_on` precedes `CURRENT_DATE`; computes `days_overdue` as `CURRENT_DATE - due_on`.
- `member_loan_stats`: left joins loan to member, aggregates loan counts (`loans_total`) and counts only open loans (`loans_open` using `FILTER`), captures latest loan date (`last_loan_on`).
- `popular_books`: filters loans from the past 365 days, groups by book and author, counts loans (`times_loaned`), keeps groups with `times_loaned >= 5`.
- `idx_popular_books_id`: unique index on `book_id` ensures single row per book in the materialized view.

#### Dependencies
- `loan`, `member`, `book`, `author` tables (referenced but not defined in this file).
- PostgreSQL interval syntax (`INTERVAL '365 days'`).
