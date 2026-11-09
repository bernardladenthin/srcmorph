### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:55:51Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Seed data for a lending library: populates authors, books, members, and loan records.

#### Purpose
- Populate the library database with initial test data.
- Provide a realistic dataset for benchmarking and development.

#### Type
- Data insertion script (PostgreSQL `INSERT` statements).

#### Schema
**author**  
`author_id integer`  
`full_name text`  
`birth_year integer`  
`country_code char(2)`

**book**  
`book_id integer`  
`isbn text`  
`title text`  
`author_id integer`  
`published_year integer`  
`copies_total integer`  
`copies_free integer`

**member**  
`member_id integer`  
`email text`  
`full_name text`  
`joined_on date`  
`is_blocked boolean`

**loan**  
`loan_id integer`  
`book_id integer`  
`member_id integer`  
`loaned_on date`  
`due_on date`  
`returned_on date`

#### Reads
None – the script only performs `INSERT` operations.

#### Writes
- `author`: 15 rows  
- `book`: 150 rows  
- `member`: 50 rows  
- `loan`: 150 rows  

#### Relationships
- `book.author_id -> author.author_id`  
- `loan.book_id -> book.book_id`  
- `loan.member_id -> member.member_id`

#### Dependencies
- Tables: `author`, `book`, `member`, `loan`  
- No functions, triggers, or extensions referenced.
