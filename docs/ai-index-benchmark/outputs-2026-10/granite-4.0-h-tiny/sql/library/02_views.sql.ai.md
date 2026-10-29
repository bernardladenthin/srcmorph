### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:08:49Z
- G: 
- A: 0.0.0
- X: file
---
```sql
> A PostgreSQL library schema providing reporting views for overdue loans, member loan statistics, and popular books.

#### Purpose
- Generate reporting views for overdue loans, member loan statistics, and popular books.

#### Type
- VIEW (3 views) / MATERIALIZED VIEW (1 MVIEW) / INDEX (1)

#### Schema
- overdue_loans: loan_id INT, member_id INT, email VARCHAR, title TEXT, due_on DATE, days_overdue INT
- member_loan_stats: member_id INT, full_name VARCHAR, loans_total INT, loans_open INT, last_loan_on DATE
- popular_books: book_id INT, title TEXT, author_name VARCHAR, times_loaned INT

#### Reads
- overdue_loans: loan (loan_id, member_id, book_id), member (member_id), book (book_id)
- member_loan_stats: member (member_id), loan (member_id, loan_id)
- popular_books: loan (loan_id, loaned_on), book (book_id), author (author_id)

#### Writes
- No explicit writes (CREATE VIEW/MATERIALIZED VIEW statements).

#### Relationships
- overdue_loans: loan -> member (member_id), loan -> book (book_id)
- member_loan_stats: member (member_id), loan (member_id, loan_id)
- popular_books: loan (loan_id, loaned_on), book (book_id), author (author_id)

#### Routine logic
- overdue_loans: Lists overdue loans with overdue days.
- member_loan_stats: Aggregates loan counts per member, including open loans.
- popular_books: Identifies books loaned ≥5 times in the last year.

#### Dependencies
- loan, member, book, author tables and author.full_name column.
``````
