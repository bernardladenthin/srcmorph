### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:24Z
- G: 
- A: 0.0.0
- X: file
---
> This migration updates the lending library schema by introducing branches, relocating existing book copies to a branch structure, and retiring old book copy counters.

#### Purpose
*   Establish the `branch` entity to categorize physical book locations.
*   Migrate existing `book` records into the new `book_copy` structure, assigning initial copies to the 'Main' branch.

#### Type
table / migration script (PostgreSQL)

#### Schema
branch:
branch_id SMALLSERIAL PRIMARY KEY
name VARCHAR(100) NOT NULL UNIQUE
city VARCHAR(100) NOT NULL

book_copy:
copy_id BIGSERIAL PRIMARY KEY
book_id BIGINT NOT NULL
branch_id SMALLINT NOT NULL
barcode VARCHAR(32) NOT NULL UNIQUE
status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))

#### Reads
book: book_id, copies_total, copies_free, isbn
member: joined_on (renamed)

#### Writes
branch: INSERT (name, city)
book_copy: INSERT (book_id, branch_id, barcode, status)
loan: ALTER (ADD copy_id)
member: ALTER (RENAME COLUMN joined_on TO member_since), UPDATE (home_branch_id)
book: ALTER (DROP CONSTRAINT book_free_le_total), ALTER (DROP COLUMN copies_free), ALTER (DROP COLUMN copies_total)

#### Relationships
book_copy.book_id -> book.book_id
book_copy.branch_id -> branch.branch_id
loan.copy_id -> book_copy.copy_id
member.home_branch_id -> branch.branch_id

#### Routine logic
N/A
