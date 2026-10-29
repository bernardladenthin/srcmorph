### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:51:27Z
- G: 
- A: 0.0.0
- X: file
---
> This migration updates the lending library schema by introducing branches, relocating existing book copies to a branch structure, and modifying member and book tracking fields.

#### Purpose
* Implement `branch` entity to categorize book copies geographically.
* Migrate existing `book` copies into the new `book_copy` structure, assigning them to the 'Main' branch.
* Update `member` records to associate members with a `home_branch_id` and rename the `joined_on` column.
* Remove deprecated copy counter columns (`copies_free`, `copies_total`) from the `book` table.

#### Type
table / migration script (PostgreSQL)

#### Schema
Table: branch
branch_id SMALLSERIAL PRIMARY KEY
name VARCHAR(100) NOT NULL UNIQUE
city VARCHAR(100) NOT NULL

Table: book_copy
copy_id BIGSERIAL PRIMARY KEY
book_id BIGINT NOT NULL
branch_id SMALLINT NOT NULL
barcode VARCHAR(32) NOT NULL UNIQUE
status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))

Table: book (modified)
(Columns `copies_free` and `copies_total` dropped)

Table: member (modified)
member_since (renamed from `joined_on`)
home_branch_id SMALLINT

#### Reads
book.book_id, book.isbn, book.copies_total, book.copies_free
book_copy.book_id
member.home_branch_id (for update)

#### Writes
branch (INSERT)
book_copy (INSERT)
loan (ALTER: adds `copy_id`)
member (ALTER: renames `joined_on` to `member_since`, adds `home_branch_id`)
book (ALTER: drops `copies_free`, `copies_total`)

#### Relationships
book.book_id -> book_copy.book_id
branch.branch_id -> book_copy.branch_id
branch.branch_id -> member.home_branch_id
loan.copy_id -> book_copy.copy_id

#### Routine logic
N/A

#### Dependencies
book
loan
member
generate_series
