### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:11:22Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library reporting views for tracking overdue loans, member statistics, and popular books.

#### Purpose
- Provides reporting views for a PostgreSQL lending library: overdue loans, member loan statistics, and popular books.
- Enables analysis of loan status, member activity, and book circulation without altering base tables.

#### Type
- SQL object kinds: view, materialized view, unique index.

#### Schema
- overdue_loans (view): loan_id, member_id, email, title, due_on, days_overdue (computed as CURRENT_DATE - due_on).
- member_loan_stats (view): member_id, full_name, loans_total, loans_open, last_loan_on.
- popular_books (materialized view): book_id, title, author_name, times_loaned.
- idx_popular_books_id (index): unique index on popular_books.book_id.

#### Reads
- overdue_loans reads loan.loan_id, loan.member_id, loan.due_on, loan.returned_on, member.email, book.title.
- member_loan_stats reads member.member_id, member.full_name, loan.loan_id, loan.returned_on, loan.loaned_on.
- popular_books reads loan.loan_id, loan.loaned_on, book.book_id, book.title, book.author_id, author.full_name.

#### Writes
- popular_books (materialized view) is written to by the materialized view definition.
- idx_popular_books_id (index) is written to during CREATE INDEX.

#### Relationships
- overdue_loans joins loan to member on member_id and loan to book on book_id.
- member_loan_stats joins member to loan on member_id.
- popular_books joins loan to book on book_id and book to author on author_id.
- idx_popular_books_id indexes popular_books.book_id.

#### Dependencies
- Base tables: loan, member, book, author.
- Views depend on loan, member, book, author tables.
- Materialized view popular_books depends on loan, book, author.
- Index depends on popular_books.
