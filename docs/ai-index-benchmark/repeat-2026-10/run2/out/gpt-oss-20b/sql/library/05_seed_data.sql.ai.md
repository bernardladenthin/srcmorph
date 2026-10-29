### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:14:33Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library system, populating authors, books, members, and loan records.

#### Purpose
- Populate initial data for testing or demo purposes.
- Provide a realistic dataset for queries on authors, books, members, and loans.

#### Type
- SQL script (PostgreSQL) containing INSERT statements.

#### Schema
| Table | Columns (type, constraints) |
|-------|-----------------------------|
| **author** | `author_id integer PK`, `full_name text NOT NULL`, `birth_year integer`, `country_code char(2) NOT NULL` |
| **book** | `book_id integer PK`, `isbn text NOT NULL`, `title text NOT NULL`, `author_id integer NOT NULL FK → author.author_id`, `published_year integer`, `copies_total integer NOT NULL`, `copies_free integer NOT NULL` |
| **member** | `member_id integer PK`, `email text NOT NULL`, `full_name text NOT NULL`, `joined_on date NOT NULL`, `is_blocked boolean NOT NULL` |
| **loan** | `loan_id integer PK`, `book_id integer NOT NULL FK → book.book_id`, `member_id integer NOT NULL FK → member.member_id`, `loaned_on date NOT NULL`, `due_on date NOT NULL`, `returned_on date` |

#### Reads
None – the script only performs INSERT operations.

#### Writes
- `author` – 15 rows
- `book` – 151 rows
- `member` – 50 rows
- `loan` – 150 rows

#### Relationships
- `book.author_id -> author.author_id`
- `loan.book_id -> book.book_id`
- `loan.member_id -> member.member_id`

#### Dependencies
- Tables: `author`, `book`, `member`, `loan` (referenced by foreign keys).
