### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-28T22:17:54Z
- G: 
- A: 0.0.0
- X: file
---
> Seeds initial data for authors, books, members, and loans in a lending library system.

#### Purpose
Populates database with sample data for testing and demonstration.

#### Type
DML script (INSERT statements)

#### Schema
##### author
`author_id integer` PK  
`full_name text` NOT NULL  
`birth_year integer`  
`country_code char(2)`  

##### book
`book_id integer` PK  
`isbn text` NOT NULL  
`title text` NOT NULL  
`author_id integer` FK `author.author_id`  
`published_year integer`  
`copies_total integer` NOT NULL  
`copies_free integer` NOT NULL  

##### member
`member_id integer` PK  
`email text` NOT NULL  
`full_name text` NOT NULL  
`joined_on date` NOT NULL  
`is_blocked boolean` NOT NULL  

##### loan
`loan_id integer` PK  
`book_id integer` FK `book.book_id`  
`member_id integer` FK `member.member_id`  
`loaned_on date` NOT NULL  
`due_on date` NOT NULL  
`returned_on date`  

#### Reads
None

#### Writes
- `author`
- `book`
- `member`
- `loan`

#### Relationships
- `book.author_id` -> `author.author_id`
- `loan.book_id` -> `book.book_id`
- `loan.member_id` -> `member.member_id`

#### Dependencies
- `author`
- `book`
- `member`
- `loan`
