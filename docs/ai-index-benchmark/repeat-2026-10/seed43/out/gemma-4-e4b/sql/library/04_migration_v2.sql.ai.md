### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:58:17Z
- G: 
- A: 0.0.0
- X: file
---
> This migration updates the lending library schema by introducing branches, relocating book copies to specific branches, and restructuring book inventory tracking.

#### Purpose
*   Introduce `branch` entity to define library locations.
*   Migrate existing `book` copies into the new `book_copy` structure, assigning them to the 'Main' branch.
*   Update `member` table to track a `home_branch_id` and rename `joined_on` to `member_since`.
*   Remove deprecated copy counters (`copies_free`, `copies_total`) from the `book` table.

#### Type
Table / Migration Script (PostgreSQL)

#### Schema
Table: branch
branch_id SMALLSERIAL PRIMARY KEY
name VARCHAR(100) NOT NULL UNIQUE
city VARCHAR(100) NOT NULL

Table: book_copy
copy_id BIGSERIAL PRIMARY KEY
book_id BIGINT NOT NULL REFERENCES book (book_id)
branch_id SMALLINT NOT NULL REFERENCES branch (branch_id)
barcode VARCHAR(32) NOT NULL UNIQUE
status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))

#### Reads
book.book_id, book.isbn, book.copies_total, book.copies_free
book_copy.book_id (during index creation)
member.joined_on (during rename)

#### Writes
branch (INSERT)
book_copy (INSERT)
loan (ALTER: ADD copy_id)
member (ALTER: RENAME COLUMN, ADD home_branch_id, UPDATE)
book (ALTER: DROP CONSTRAINT, DROP COLUMN)

#### Relationships
book.book_id -> book_copy.book_id
branch.branch_id -> book_copy.branch_id
branch.branch_id -> member.home_branch_id
loan.copy_id -> book_copy.copy_id

#### Dependencies
book
loan
member
