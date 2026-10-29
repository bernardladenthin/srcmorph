### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:17:18Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates a lending library schema to support multiple branches, per-branch book copies, and updated member and loan data structures.

#### Purpose
- Adds branch management for a lending library
- Introduces per-branch book copy tracking
- Updates member and loan tables with new schema elements

#### Type
table

#### Schema
branch.branch_id SMALLSERIAL PRIMARY KEY
branch.name VARCHAR(100) NOT NULL UNIQUE
branch.city VARCHAR(100) NOT NULL

book_copy.copy_id BIGSERIAL PRIMARY KEY
book_copy.book_id BIGINT NOT NULL REFERENCES book (book_id)
book_copy.branch_id SMALLINT NOT NULL REFERENCES branch (branch_id)
book_copy.barcode VARCHAR(32) NOT NULL UNIQUE
book_copy.status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))

#### Reads
book.book_id
book.copies_free
book.copies_total
book.isbn

#### Writes
branch
book_copy
loan.copy_id
member.home_branch_id

#### Relationships
book_copy.book_id -> book.book_id
book_copy.branch_id -> branch.branch_id
loan.copy_id -> book_copy.copy_id
member.home_branch_id -> branch.branch_id

#### Dependencies
book
branch
loan
member
generate_series

#### Routine logic
- Creates branch table with unique name and city
- Inserts default 'Main' branch in Springfield
- Creates book_copy table with foreign keys to book and branch
- Migrates existing book copies to new branch structure
- Adds copy_id column to loan table
- Renames member.joined_on to member_since
- Adds home_branch_id to member table
- Updates member home_branch_id to default value
- Removes old copy count constraints and columns from book
- Creates index on book_copy.book_id

#### Schema
branch.branch_id SMALLSERIAL PRIMARY KEY
branch.name VARCHAR(100) NOT NULL UNIQUE
branch.city VARCHAR(100) NOT NULL

book_copy.copy_id BIGSERIAL PRIMARY KEY
book_copy.book_id BIGINT NOT NULL REFERENCES book (book_id)
book_copy.branch_id SMALLINT NOT NULL REFERENCES branch (branch_id)
book_copy.barcode VARCHAR(32) NOT NULL UNIQUE
book_copy.status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))

#### Reads
book.book_id
book.copies_free
book.copies_total
book.isbn

#### Writes
branch
book_copy
loan.copy_id
member.home_branch_id

#### Relationships
book_copy.book_id -> book.book_id
book_copy.branch_id -> branch.branch_id
loan.copy_id -> book_copy.copy_id
member.home_branch_id -> branch.branch_id

#### Dependencies
book
branch
loan
member
generate_series

#### Routine logic
- Creates branch table with unique name and city
- Inserts default 'Main' branch in Springfield
- Creates book_copy table with foreign keys to book and branch
- Migrates existing book copies to new branch structure
- Adds copy_id column to loan table
- Renames member.joined_on to member_since
- Adds home_branch_id to member table
- Updates member home_branch_id to default value
- Removes old copy count constraints and columns from book
- Creates index on book_copy.book_id
