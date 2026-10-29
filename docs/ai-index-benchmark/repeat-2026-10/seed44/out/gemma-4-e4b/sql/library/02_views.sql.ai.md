### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:06Z
- G: 
- A: 0.0.0
- X: file
---
> This file defines reporting views and a materialized view to track overdue loans, member borrowing statistics, and frequently loaned books.

#### Purpose
* Defines `overdue_loans` view to list loans past their due date.
* Defines `member_loan_stats` view to aggregate loan counts per member.
* Defines `popular_books` materialized view to track books loaned frequently in the last year.

#### Type
view / materialized view / index (PostgreSQL)

#### Schema
overdue_loans (view):
loan_id integer
member_id integer
email text
title text
due_on date
days_overdue integer

member_loan_stats (view):
member_id integer
full_name text
loans_total bigint
loans_open bigint
last_loan_on date

popular_books (materialized view):
book_id integer
title text
author_name text
times_loaned bigint
Index: idx_popular_books_id (book_id) UNIQUE

#### Reads
loan (loan_id, member_id, book_id, due_on, returned_on, loaned_on)
member (member_id, email)
book (book_id, title)
author (author_id, full_name)

#### Writes
None

#### Relationships
loan.member_id -> member.member_id
loan.book_id -> book.book_id
book.author_id -> author.author_id

#### Dependencies
loan
member
book
author
