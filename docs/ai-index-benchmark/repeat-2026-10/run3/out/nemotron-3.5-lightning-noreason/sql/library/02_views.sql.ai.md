### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:40:10Z
- G: 
- A: 0.0.0
- X: file
---
> Reporting views for a lending library, modeling loan status, member statistics, and book popularity.

#### Purpose
- overdue_loans: Returns currently active loans that are past their due date, showing member contact and book details.
- member_loan_stats: Aggregates per-member loan counts, distinguishing open vs total loans and the date of the most recent loan.
- popular_books: Materialized view listing books loaned at least five times in the last year, with author names and loan counts.

#### Type
View / Materialized View (PostgreSQL)

#### Schema
- overdue_loans: loan_id, member_id, email, title, due_on, days_overdue
- member_loan_stats: member_id, full_name, loans_total, loans_open, last_loan_on
- popular_books: book_id, title, author_name, times_loaned
- Index: idx_popular_books_id on popular_books (book_id)

#### Reads
- overdue_loans: loan (loan_id, member_id, due_on, returned_on), member (member_id, email), book (book_id, title)
- member_loan_stats: member (member_id, full_name), loan (loan_id, member_id, returned_on, loaned_on)
- popular_books: loan (loan_id, book_id, loaned_on), book (book_id, title, author_id), author (author_id, full_name)

#### Writes
- popular_books: materialized view (populated on refresh; not directly written by DML)

#### Relationships
- overdue_loans: l.member_id -> m.member_id, l.book_id -> b.book_id
- member_loan_stats: l.member_id -> m.member_id
- popular_books: l.book_id -> b.book_id, a.author_id -> b.author_id

#### Routine logic
- overdue_loans: SELECT with JOINs and WHERE filter for unreturned loans past due date.
- member_loan_stats: LEFT JOIN loan, GROUP BY member, COUNT and MAX aggregates, FILTER for open loans.
- popular_books: JOIN loan, book, author; WHERE loaned_on within last 365 days; GROUP BY book and author; HAVING count >= 5.

#### Dependencies
- overdue_loans: loan, member, book
- member_loan_stats: member, loan
- popular_books: loan, book, author
- idx_popular_books_id references popular_books (book_id)
