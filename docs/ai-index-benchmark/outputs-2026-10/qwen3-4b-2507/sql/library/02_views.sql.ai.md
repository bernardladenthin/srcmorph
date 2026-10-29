### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:13:19Z
- G: 
- A: 0.0.0
- X: file
---
> Provides reporting on overdue loans, member loan activity, and popular books over the past year

#### Purpose
- Identifies loans that are currently overdue by calculating days past due
- Aggregates loan statistics per member including total loans and open loans
- Tracks books that have been loaned frequently over the last year

#### Type
View (with one materialized view)

#### Schema
- **overdue_loans**:  
  loan_id (integer), member_id (integer), email (text), title (text), due_on (date), days_overdue (interval)

- **member_loan_stats**:  
  member_id (integer), full_name (text), loans_total (integer), loans_open (integer), last_loan_on (date)

- **popular_books**:  
  book_id (integer), title (text), author_name (text), times_loaned (integer)  
  UNIQUE INDEX: idx_popular_books_id (book_id)

#### Reads
- overdue_loans: loan, member, book
- member_loan_stats: member, loan
- popular_books: loan, book, author

#### Writes
- None

#### Relationships
- overdue_loans.member_id → member.member_id  
- overdue_loans.book_id → book.book_id  
- member_loan_stats.member_id → member.member_id  
- member_loan_stats.loan_id → loan.loan_id  
- popular_books.book_id → book.book_id  
- popular_books.author_id → author.author_id  
- popular_books.loan_id → loan.loan_id

#### Routine logic
- overdue_loans: filters active loans with due date before today, computes days overdue  
- member_loan_stats: groups members by ID and name, counts total and open loans, tracks last loan date  
- popular_books: aggregates loans from the past year, groups by book and author, filters to only books loaned 5+ times

#### Dependencies
loan, member, book, author
