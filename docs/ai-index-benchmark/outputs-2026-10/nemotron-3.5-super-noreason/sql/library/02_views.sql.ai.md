### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:02:35Z
- G: 
- A: 0.0.0
- X: file
---
> Models lending library reporting data: overdue loans, per-member loan statistics, and frequently loaned books over the trailing year.

#### Purpose
- Provides read-only reporting views for overdue loans, member loan aggregates, and popular books.
- Materializes popular books with a unique index on book_id for fast lookup.

#### Type
view, materialized view, index (PostgreSQL).

#### Schema
overdue_loans: loan_id, member_id, email, title, due_on, days_overdue (computed as CURRENT_DATE - l.due_on).
member_loan_stats: member_id, full_name, loans_total, loans_open, last_loan_on.
popular_books: book_id, title, author_name, times_loaned.
Index: idx_popular_books_id on popular_books (book_id), unique.

#### Reads
loan.loan_id, loan.member_id, loan.book_id, loan.returned_on, loan.due_on, loan.loaned_on.
member.member_id, member.email, member.full_name.
book.book_id, book.title, book.author_id.
author.author_id, author.full_name.

#### Writes
None (views and materialized view are read-only definitions; no INSERT/UPDATE/DELETE targets).

#### Relationships
loan.member_id -> member.member_id.
loan.book_id -> book.book_id.
book.author_id -> author.author_id.

#### Routine logic
None.

#### Dependencies
loan, member, book, author tables.
