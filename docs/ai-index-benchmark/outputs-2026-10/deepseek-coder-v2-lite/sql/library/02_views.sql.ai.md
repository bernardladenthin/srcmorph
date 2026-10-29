### 02_views.sql
- H: 1.0
- C: C0605265
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T01:59:15Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library reporting views for tracking overdue loans and member loan statistics, including a materialized view for popular books.

#### Purpose
- **overdue_loans**: Tracks loans that are overdue, showing details including loan ID, member ID, email, book title, due date, and days overdue.
- **member_loan_stats**: Provides statistics on member loans, including total and open loans, and the last loan date.
- **popular_books**: A materialized view that lists books that have been loaned multiple times within the last year.

#### Type
PostgreSQL.

#### Schema
- **overdue_loans**:
  - `loan_id` INT,
  - `member_id` INT,
  - `email` VARCHAR(255),
  - `title` VARCHAR(255),
  - `due_on` DATE,
  - `days_overdue` INTERVAL DAY TO DAY
  - PRIMARY KEY (`loan_id`)
  - Indexes: None

- **member_loan_stats**:
  - `member_id` INT,
  - `full_name` VARCHAR(255),
  - `loans_total` INT,
  - `loans_open` INT,
  - `last_loan_on` DATE
  - PRIMARY KEY (`member_id`)
  - Indexes: None

- **popular_books**:
  - `book_id` INT,
  - `title` VARCHAR(255),
  - `author_name` VARCHAR(255),
  - `times_loaned` INT
  - PRIMARY KEY (`book_id`)
  - INDEX `idx_popular_books_id` (`book_id`)

#### Reads
- **overdue_loans**: Reads from `loan`, `member`, `book` tables.
- **member_loan_stats**: Reads from `member`, `loan` tables.
- **popular_books**: Reads from `loan`, `book`, `author` tables.

#### Writes
- **overdue_loans**: None.
- **member_loan_stats**: None.
- **popular_books**: None.

#### Relationships
- **loan.book_id -> book.book_id**
- **loan.member_id -> member.member_id**
- **book.author_id -> author.author_id**

#### Routine logic
- **overdue_loans**: Selects loans that are not returned and are overdue.
- **member_loan_stats**: Aggregates loan counts and tracks the last loan date for each member.
- **popular_books**: Identifies books loaned multiple times within the last year.

#### Dependencies
- **loan**, **member**, **book**, **author** tables.
- **CURRENT_DATE**, **INTERVAL '365 days'** for date calculations.
