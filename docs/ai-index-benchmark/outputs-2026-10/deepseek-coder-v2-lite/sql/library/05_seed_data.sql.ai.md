### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T02:03:18Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds initial data into the database for a lending library system.

#### Purpose
- **Insert data into the `author`, `book`, and `member` tables**: Populates the database with initial data for authors, books, and members.
- **Insert data into the `loan` table**: Seeds the loan history for books borrowed by members.

#### Type
SQL script for PostgreSQL.

#### Schema
**Author Table**
- `author_id` (PK, INT): Unique identifier for each author.
- `full_name` (VARCHAR): Full name of the author.
- `birth_year` (INT): Year of birth of the author.
- `country_code` (CHAR(2)): Country code of the author.

**Book Table**
- `book_id` (PK, INT): Unique identifier for each book.
- `isbn` (VARCHAR): International Standard Book Number of the book.
- `title` (VARCHAR): Title of the book.
- `author_id` (INT, FK): Reference to the author of the book.
- `published_year` (INT): Year the book was published.
- `copies_total` (INT): Total number of copies available.
- `copies_free` (INT): Number of copies currently free (not borrowed).

**Member Table**
- `member_id` (PK, INT): Unique identifier for each member.
- `email` (VARCHAR): Email address of the member.
- `full_name` (VARCHAR): Full name of the member.
- `joined_on` (DATE): Date the member joined the library.
- `is_blocked` (BOOLEAN): Indicates if the member is blocked from borrowing books.

**Loan Table**
- `loan_id` (PK, INT): Unique identifier for each loan.
- `book_id` (INT, FK): Reference to the book being borrowed.
- `member_id` (INT, FK): Reference to the member borrowing the book.
- `loaned_on` (DATE): Date the book was borrowed.
- `due_on` (DATE): Date the book is due to be returned.
- `returned_on` (DATE): Date the book was actually returned (NULL if not returned).

#### Reads
- **Author Table**: Used in `book` table for foreign key reference.
- **Book Table**: Reads from `author` table for author information.
- **Member Table**: Reads from `loan` table for member information related to loans.
- **Loan Table**: Reads from `book` and `member` tables for book and member details.

#### Writes
- **Author Table**: Writes new authors.
- **Book Table**: Writes new books.
- **Member Table**: Writes new members.
- **Loan Table**: Writes new loan records.

#### Relationships
- **Foreign Keys**:
  - `author_id` in `book` references `author_id` in `author`.
  - `member_id` in `loan` references `member_id` in `member`.
- **Join Relationships**:
  - `book` and `member` are joined in the `loan` table to track borrowing history.

#### Routine logic
- **Data Insertion**:
  - **Author Table**: Inserts data for each author with their full name, birth year, and country code.
  - **Book Table**: Inserts data for each book with its ISBN, title, author ID, published year, total copies, and free copies.
  - **Member Table**: Inserts data for each member with their email, full name, joining date, and block status.
  - **Loan Table**: Inserts data for each loan with the book ID, member ID, loan date, due date, and return date.

#### Dependencies
- **Other Tables**:
  - `author` is referenced by `book`.
  - `book` and `member` are referenced by `loan`.

This script is designed to initialize the database with a set of seed data that represents a lending library system, including authors, books, members, and their borrowing history.
