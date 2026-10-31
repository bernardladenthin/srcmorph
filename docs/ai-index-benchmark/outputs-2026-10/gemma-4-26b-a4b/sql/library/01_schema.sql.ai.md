### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:48:25Z
- G: 
- A: 0.0.0
- X: file
---
> Defines the core data model for a lending library system, managing authors, books, members, and loan transactions.

#### Purpose
* Establishes the foundational relational structure for tracking library inventory and circulation.
* Manages relationships between authors and books, and members and their borrowed items.

#### Type
SQL schema (PostgreSQL).

#### Schema
**author**
* `author_id` BIGSERIAL (PK)
* `full_name` VARCHAR(200) NOT NULL
* `birth_year` SMALLINT (CHECK 1000-2100)
* `country_code` CHAR(2)

**book**
* `book_id` BIGSERIAL (PK)
* `isbn` VARCHAR(17) NOT NULL UNIQUE
* `title` VARCHAR(300) NOT NULL
* `author_id` BIGINT NOT NULL
* `published_year` SMALLINT
* `copies_total` INTEGER NOT NULL DEFAULT 1 (CHECK >= 0)
* `copies_free` INTEGER NOT NULL DEFAULT 1 (CHECK >= 0)
* *Constraint*: `book_free_le_total` (copies_free <= copies_total)
* *Index*: `idx_book_author` ON `author_id`

**member**
* `member_id` BIGSERIAL (PK)
* `email` VARCHAR(254) NOT NULL UNIQUE
* `full_name` VARCHAR(200) NOT NULL
* `joined_on` DATE NOT NULL DEFAULT CURRENT_DATE
* `is_blocked` BOOLEAN NOT NULL DEFAULT FALSE

**loan**
* `loan_id` BIGSERIAL (PK)
* `book_id` BIGINT NOT NULL
* `member_id` BIGINT NOT NULL
* `loaned_on` DATE NOT NULL DEFAULT CURRENT_DATE
* `due_on` DATE NOT NULL
* `returned_on` DATE
* *Constraint*: `loan_due_after_loan` (due_on > loaned_on)
* *Index*: `idx_loan_member` ON `member_id`
* *Index*: `idx_loan_open` ON `due_on` WHERE `returned_on` IS NULL

#### Relationships
* `book.author_id` -> `author.author_id`
* `loan.book_id` -> `book.book_id`
* `loan.member_id` -> `member.member_id` (ON DELETE CASCADE)

#### Dependencies
* `author`
* `book`
* `member`
* `loan`
