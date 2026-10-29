### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T04:07:10Z
- G: 
- A: 0.0.0
- X: file
---
> This file populates the lending library database with initial records for authors, books, members, and loan transactions.

#### Purpose
* Provides initial seed data for testing and development of a library management system.
* Populates core entities: authors, books, library members, and historical/active loan records.

#### Type
DML (Data Manipulation Language) / Seed Script (PostgreSQL)

#### Schema
* **author**
    * author_id INT (PK)
    * full_name VARCHAR
    * birth_year INT
    * country_code CHAR(2)
* **book**
    * book_id INT (PK)
    * isbn VARCHAR
    * title VARCHAR
    * author_id INT (FK)
    * published_year INT
    * copies_total INT
    * copies_free INT
* **member**
    * member_id INT (PK)
    * email VARCHAR
    * full_name VARCHAR
    * joined_on DATE
    * is_blocked BOOLEAN
* **loan**
    * loan_id INT (PK)
    * book_id INT (FK)
    * member_id INT (FK)
    * loaned_on DATE
    * due_on DATE
    * returned_on DATE

#### Reads
None (This is a seed file performing only writes).

#### Writes
* **author**: inserts 15 records.
* **book**: inserts 110 records.
* **member**: inserts 50 records.
* **loan**: inserts 150 records.

#### Relationships
* book.author_id -> author.author_id
* loan.book_id -> book.book_id
* loan.member_id -> member.member_id
