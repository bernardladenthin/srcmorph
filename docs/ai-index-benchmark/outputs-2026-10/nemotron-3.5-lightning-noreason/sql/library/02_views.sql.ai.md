### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:53:14Z
- G: 
- A: 0.0.0
- X: file
---
> Reporting views for a lending library, exposing loan status, member statistics, and book popularity metrics.

#### Purpose
- Provide read-only views for loan overdue status, member loan statistics, and popular books tracking.

#### Type
View / Materialized view (PostgreSQL)

#### Schema
**overdue_loans**
- loan_id (integer)
- member_id (integer)
- email (text)
- title (text)
- due_on (date)
- days_overdue (integer, generated: CURRENT_DATE - l.due_on)
PK: none defined
Indexes: none defined

**member_loan_stats**
- member_id (integer)
- full_name (text)
- loans_total (bigint, count)
- loans_open (bigint, count, filtered: returned_on IS NULL)
- last_loan_on (date, max)
PK: none defined
Indexes: none defined

**popular_books**
- book_id (integer)
- title (text)
- author_name (text)
- times_loaned (bigint, count)
PK: none defined
Indexes: idx_popular_books_id on book_id (unique)

#### Reads
- overdue_loans: loan (l), member (m), book (b) — columns: loan_id, member_id, email, title, due_on, returned_on
- member_loan_stats: member (m), loan (l) — columns: member_id, full_name, loan_id, returned_on, loaned_on
- popular_books: loan (l), book (b), author (a) — columns: book_id, title, author_id, loaned_on

#### Writes
- No write operations; views are read-only.

#### Relationships
- overdue_loans: l.member_id → m.member_id, l.book_id → b.book_id
- member_loan_stats: l.member_id → m.member_id
- popular_books: l.book_id → b.book_id, l.author_id → a.author_id (via book join)

#### Routine logic
- overdue_loans: Selects active loans where returned_on is null and due_on is before today, computing days_overdue.
- member_loan_stats: Aggregates loan counts per member, distinguishing open vs total, and finds the most recent loan date.
- popular_books: Counts loans in the last 365 days, groups by book and author, filters to books with at least 5 loans.
- idx_popular_books_id: Enforces uniqueness on book_id within the materialized view.

#### Dependencies
- loan, member, book, author tables
- materialized view popular_books references loan, book, author
