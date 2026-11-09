### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:54:41Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Seed data for a library system: authors, books, members, and loan records.

#### Purpose
- Populate the `author`, `book`, `member`, and `loan` tables with initial data for testing or demo purposes.

#### Type
- SQL script (PostgreSQL DDL/DML).  
- Creates no new objects; only performs `INSERT` statements.

#### Schema (inferred)
| Table | Columns (type, constraints) |
|-------|-----------------------------|
| **author** | `author_id` integer PK, `full_name` text NOT NULL, `birth_year` integer, `country_code` char(2) |
| **book** | `book_id` integer PK, `isbn` text NOT NULL, `title` text NOT NULL, `author_id` integer FK → `author.author_id`, `published_year` integer, `copies_total` integer, `copies_free` integer |
| **member** | `member_id` integer PK, `email` text NOT NULL, `full_name` text NOT NULL, `joined_on` date, `is_blocked` boolean |
| **loan** | `loan_id` integer PK, `book_id` integer FK → `book.book_id`, `member_id` integer FK → `member.member_id`, `loaned_on` date, `due_on` date, `returned_on` date NULLABLE |

#### Writes
- `author`: 15 rows (1‑15).  
- `book`: 150 rows (1‑150).  
- `member`: 50 rows (1‑50).  
- `loan`: 150 rows (1‑150).

#### Relationships
- `book.author_id -> author.author_id`  
- `loan.book_id -> book.book_id`  
- `loan.member_id -> member.member_id`

#### Dependencies
- None beyond the tables listed above.
