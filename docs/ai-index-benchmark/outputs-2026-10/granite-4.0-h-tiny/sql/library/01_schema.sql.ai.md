### 01_schema.sql
- H: 1.0
- C: 0604AED6
- D: 2026-10-28T21:08:38Z
- T: 2026-10-29T00:08:43Z
- G: 
- A: 0.0.0
- X: file
---
**Purpose**  
Provides a relational model for a lending library, enabling discovery of authors, books, members, and loan transactions while enforcing business rules such as free‑copy limits and loan validity.

**Type**  
`TABLE` (PostgreSQL)

**Schema**  

| Column | Type | Constraints |
|--------|------|-------------|
| `author_id` | `BIGSERIAL` | `PRIMARY KEY` |
| `full_name` | `VARCHAR(200)` | `NOT NULL` |
| `birth_year` | `SMALLINT` | `CHECK (birth_year BETWEEN 1000 AND 2100)` |
| `country_code` | `CHAR(2)` | — |
| `book_id` | `BIGSERIAL` | `PRIMARY KEY` |
| `isbn` | `VARCHAR(17)` | `NOT NULL`, `UNIQUE` |
| `title` | `VARCHAR(300)` | `NOT NULL` |
| `author_id` | `BIGINT` | `NOT NULL`, `REFERENCES author(author_id)` |
| `published_year` | `SMALLINT` | — |
| `copies_total` | `INTEGER` | `NOT NULL`, `DEFAULT 1`, `CHECK (copies_total >= 0)` |
| `copies_free` | `INTEGER` | `NOT NULL`, `DEFAULT 1`, `CHECK (copies_free >= 0)` |
| `book_free_le_total` | — | `CHECK (copies_free <= copies_total)` |
| `member_id` | `BIGSERIAL` | `PRIMARY KEY` |
| `email` | `VARCHAR(254)` | `NOT NULL`, `UNIQUE` |
| `full_name` | `VARCHAR(200)` | `NOT NULL` |
| `joined_on` | `DATE` | `NOT NULL`, `DEFAULT CURRENT_DATE` |
| `is_blocked` | `BOOLEAN` | `NOT NULL`, `DEFAULT FALSE` |
| `loan_id` | `BIGSERIAL` | `PRIMARY KEY` |
| `book_id` | `BIGINT` | `NOT NULL`, `REFERENCES book(book_id)` |
| `member_id` | `BIGINT` | `NOT NULL`, `REFERENCES member(member_id)`, `ON DELETE CASCADE` |
| `loaned_on` | `DATE` | `NOT NULL`, `DEFAULT CURRENT_DATE` |
| `due_on` | `DATE` | `NOT NULL` |
| `returned_on` | `DATE` | — |
| `loan_due_after_loan` | — | `CHECK (due_on > loaned_on)` |

**Reads**  
- `author` (for author look‑ups)  
- `book` (for book details, author joins)  
- `member` (for member look‑ups)  
- `loan` (for loan details, author joins, member joins)

**Writes**  
- `author` (INSERT)  
- `book` (INSERT)  
- `member` (INSERT)  
- `loan` (INSERT)  

**Relationships**  
- `book.author_id -> author.author_id`  
- `loan.book_id -> book.book_id`  
- `loan.member_id -> member.member_id` (cascade delete)  

**Routine logic**  
- **author**: `author_id` auto‑generated, `full_name` required, optional birth year and country.  
- **book**: `isbn` unique, `copies_total` default 1, `copies_free` default 1, enforced `copies_free <= copies_total`.  
- **member**: `email` unique, `joined_on` defaults to today, `is_blocked` default FALSE.  
- **loan**: `loaned_on` defaults to today, `due_on` > `loaned_on`, `returned_on` nullable.  

**Dependencies**  
- `author`, `book`, `member`, `loan` tables (all referenced).  
- Indexes: `idx_book_author`, `idx_loan_member`, `idx_loan_open`.
