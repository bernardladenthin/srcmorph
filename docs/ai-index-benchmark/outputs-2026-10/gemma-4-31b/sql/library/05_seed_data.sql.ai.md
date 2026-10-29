### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T21:35:07Z
- G: 
- A: 0.0.0
- X: file
---
> Initial seed data for a lending library system, populating authors, books, members, and loan records.

#### Purpose
* Populate the database with a baseline set of test data for development and benchmarking.

#### Type
DML (INSERT statements), PostgreSQL.

#### Schema
* **author**: `author_id` (PK), `full_name`, `birth_year`, `country_code`.
* **book**: `book_id` (PK), `isbn`, `title`, `author_id` (FK), `published_year`, `copies_total`, `copies_free`.
* **member**: `member_id` (PK), `email`, `full_name`, `joined_on`, `is_blocked`.
* **loan**: `loan_id` (PK), `book_id` (FK), `member_id` (FK), `loaned_on`, `due_on`, `returned_on`.

#### Writes
* **author**: `author_id`, `full_name`, `birth_year`, `country_code`.
* **book**: `book_id`, `isbn`, `title`, `author_id`, `published_year`, `copies_total`, `copies_free`.
* **member**: `member_id`, `email`, `full_name`, `joined_on`, `is_blocked`.
* **loan**: `loan_id`, `book_id`, `member_id`, `loaned_on`, `due_on`, `returned_on`.

#### Relationships
* `book.author_id` -> `author.author_id`
* `loan.book_id` -> `book.book_id`
* `loan.member_id` -> `member.member_id`

#### Dependencies
* table `author`
* table `book`
* table `member`
* table `loan`
