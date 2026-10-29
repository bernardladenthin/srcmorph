### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:05:44Z
- G: 
- A: 0.0.0
- X: file
---
> These PostgreSQL reporting views model library loan tracking and member book statistics for a lending system.

#### Purpose
- Reporting views for a lending library: overdue loan detection, member loan statistics, and popular book analysis.

#### Type
- View and materialized view. Dialect: PostgreSQL.

#### Schema
- overdue_loans: loan_id, member_id, email, title, due_on, days_overdue (computed). No explicit PK; view joins loan l, member m, book b.
- member_loan_stats: member_id, full_name, loans_total, loans_open, last_loan_on. Grouped by member_id, full_name.
- popular_books: book_id, title, author_name, times_loaned. Grouped by book_id, title, author_name. Unique index idx_popular_books_id on book_id.

#### Reads
- overdue_loans reads: loan l (loan_id, member_id, due_on), member m (member_id, email), book b (book_id, title).
- member_loan_stats reads: member m (member_id, full_name), loan l (loan_id, returned_on).
- popular_books reads: loan l (book_id), book b (book_id, title), author a (author_id, full_name).

#### Relationships
- overdue_loans: l.member_id → m.member_id, l.book_id → b.book_id.
- member_loan_stats: l.member_id → m.member_id.
- popular_books: l.book_id → b.book_id, a.author_id → b.author_id.

#### Dependencies
- Tables: loan, member, book, author.
