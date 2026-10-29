### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:03Z
- G: 
- A: 0.0.0
- X: file
---
> Reporting views for a lending library, providing derived data on overdue loans, member statistics, and popular books.

#### Purpose
- overdue_loans: returns loans that have not been returned and are past their due date, including days overdue.
- member_loan_stats: aggregates per-member loan counts (total and open) and the date of the most recent loan.
- popular_books: materialized view listing books loaned at least five times in the last year, with author name and loan count.

#### Type
view / materialized view (PostgreSQL)

#### Schema
overdue_loans:
  - loan_id
  - member_id
  - email
  - title
  - due_on
  - days_overdue
  PK: none (view)
  FK: member.member_id -> loan.member_id; book.book_id -> loan.book_id

member_loan_stats:
  - member_id
  - full_name
  - loans_total
  - loans_open
  - last_loan_on
  PK: none (view)
  FK: member.member_id -> loan.member_id

popular_books:
  - book_id
  - title
  - author_name
  - times_loaned
  PK: none (materialized view)
  FK: book.book_id -> loan.book_id; author.author_id -> book.author_id
  Index: idx_popular_books_id on book_id

#### Reads
overdue_loans: loan (l), member (m), book (b)
member_loan_stats: member (m), loan (l)
popular_books: loan (l), book (b), author (a)

#### Writes
overdue_loans: none (read-only view)
member_loan_stats: none (read-only view)
popular_books: none (read-only materialized view)

#### Relationships
- loan.member_id -> member.member_id
- loan.book_id -> book.book_id
- book.author_id -> author.author_id

#### Dependencies
- member
- book
- author
- loan
