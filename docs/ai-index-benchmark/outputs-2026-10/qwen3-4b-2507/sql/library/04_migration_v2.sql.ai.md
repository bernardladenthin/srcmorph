### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:13:27Z
- G: 
- A: 0.0.0
- X: file
---
> This SQL script migrates a lending library schema to support branch-based book copies, removes per-book copy counters, and adds branch-specific copy tracking with status management.

#### Purpose
- Introduces a branch table to model physical locations of the library.
- Moves book copies to be branch-specific, enabling per-branch availability and loan tracking.
- Retires per-book copy counters (`copies_free`, `copies_total`) in favor of centralized copy tracking.

#### Type
Table, view, materialized view, procedure, function, trigger, index, sequence.  
Dialect: PostgreSQL

#### Schema
- `branch(branch_id, name, city)`  
  - `branch_id` SMALLSERIAL PRIMARY KEY  
  - `name` VARCHAR(100) NOT NULL UNIQUE  
  - `city` VARCHAR(100) NOT NULL  

- `book_copy(copy_id, book_id, branch_id, barcode, status)`  
  - `copy_id` BIGSERIAL PRIMARY KEY  
  - `book_id` BIGINT NOT NULL REFERENCES book(book_id)  
  - `branch_id` SMALLINT NOT NULL REFERENCES branch(branch_id)  
  - `barcode` VARCHAR(32) NOT NULL UNIQUE  
  - `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))  

- `idx_book_copy_book` INDEX on `book_copy(book_id)`

#### Reads
- `book(book_id, copies_total, copies_free)` via `SELECT b.book_id, b.copies_total`  
- `book_copy` via `INSERT INTO book_copy ... SELECT`  
- `branch` via `INSERT INTO branch`  

#### Writes
- `branch` via `INSERT INTO branch`  
- `book_copy` via `INSERT INTO book_copy`  
- `loan` via `ALTER TABLE loan ADD COLUMN copy_id`  
- `member` via `RENAME COLUMN joined_on TO member_since` and `ADD COLUMN home_branch_id`  
- `member` via `UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL`

#### Relationships
- `book_copy.book_id` → `book.book_id`  
- `book_copy.branch_id` → `branch.branch_id`  
- `loan.copy_id` → `book_copy.copy_id`  
- `member.home_branch_id` → `branch.branch_id`

#### Routine logic
- Creates `branch` table and inserts a default "Main" branch in Springfield.  
- Creates `book_copy` table with branch-specific copies, using `generate_series` to generate barcodes and statuses based on book copy counts.  
- Adds `copy_id` to `loan` table to link loans to specific book copies.  
- Renames `member.joined_on` to `member_since`.  
- Adds `home_branch_id` to `member` with reference to `branch`.  
- Updates `member.home_branch_id` to 1 for missing values.  
- Removes `copies_free` and `copies_total` from `book` table and drops their constraint.

#### Dependencies
- `book` (via references and SELECT)  
- `branch` (via references and INSERT)  
- `generate_series` (used in LATERAL join)  
- `loan` (via column addition)  
- `member` (via column rename, add, and update)
