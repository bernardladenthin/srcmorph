### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:54:19Z
- G: 
- A: 0.0.0
- X: file
---
> Provides reporting views for a library system: overdue loans, member loan statistics, and popular books.

#### Purpose
- Generate overdue loan reports.
- Summarize member loan activity.
- Identify popular books in the last year.

#### Type
- PostgreSQL view / materialized view / index.

#### Schema
**overdue_loans**  
- `loan_id` integer  
- `member_id` integer  
- `email` text  
- `title` text  
- `due_on` date  
- `days_overdue` integer  

**member_loan_stats**  
- `member_id` integer  
- `full_name` text  
- `loans_total` bigint  
- `loans_open` bigint  
- `last_loan_on` timestamp  

**popular_books**  
- `book_id` integer  
- `title` text  
- `author_name` text  
- `times_loaned` bigint  

**idx_popular_books_id**  
- unique index on `popular_books(book_id)`  

#### Reads
- `loan` (`loan_id`, `member_id`, `book_id`, `due_on`, `returned_on`, `loaned_on`)  
- `member` (`member_id`, `email`, `full_name`)  
- `book` (`book_id`, `title`, `author_id`)  
- `author` (`author_id`, `full_name`)  

#### Writes
- `popular_books` (materialized view refresh)  
- `idx_popular_books_id` (index creation)

#### Relationships
- `loan.member_id -> member.member_id`  
- `loan.book_id -> book.book_id`  
- `book.author_id -> author.author_id`  

#### Dependencies
- Tables: `loan`, `member`, `book`, `author`  
- Views: none  
- Functions: none  
- Sequences: none  

#### Routine logic
- **overdue_loans**: selects active loans past due date, calculates days overdue.  
- **member_loan_stats**: aggregates total, open, and last loan per member.  
- **popular_books**: counts loans in past year per book/author, filters for ≥5 loans.  
- **idx_popular_books_id**: enforces uniqueness on `book_id` in `popular_books`.
