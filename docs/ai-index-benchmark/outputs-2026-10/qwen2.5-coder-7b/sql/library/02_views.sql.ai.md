### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:17:38Z
- G: 
- A: 0.0.0
- X: file
---
> Reporting views for a lending library system, providing insights into overdue loans, member loan statistics, and popular books.

#### Purpose
- Provides a view of loans that are overdue.
- Offers statistics on the total and open loans for each member.
- Creates a materialized view of books that have been loaned frequently in the past year.

#### Type
- View
- Materialized View
- Index

#### Schema
- **overdue_loans**
  - loan_id integer
  - member_id integer
  - email text
  - title text
  - due_on date
  - days_overdue integer
- **member_loan_stats**
  - member_id integer
  - full_name text
  - loans_total integer
  - loans_open integer
  - last_loan_on date
- **popular_books**
  - book_id integer
  - title text
  - author_name text
  - times_loaned integer

#### Reads
- **loan**
  - loan_id
  - member_id
  - returned_on
  - due_on
  - loaned_on
- **member**
  - member_id
  - full_name
  - email
- **book**
  - book_id
  - title
  - author_id
- **author**
  - author_id
  - full_name

#### Writes
- **popular_books**
  - book_id
  - title
  - author_name
  - times_loaned

#### Relationships
- **loan.member_id -> member.member_id**
- **loan.book_id -> book.book_id**
- **book.author_id -> author.author_id**

#### Dependencies
- **loan**
- **member**
- **book**
- **author**
