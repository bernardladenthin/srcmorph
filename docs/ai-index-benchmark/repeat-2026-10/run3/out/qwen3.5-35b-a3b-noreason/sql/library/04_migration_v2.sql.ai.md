### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:43:51Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates the lending library schema to support multi-branch operations by introducing branch and book copy entities while retiring per-book copy counters.

#### Purpose
- Adds branch management and per-copy tracking to support multi-location lending.
- Refactors the schema by removing per-book copy counts and linking loans to specific copies.

#### Type
PostgreSQL DDL migration script (multiple objects: tables, indexes, DML).

#### Schema
**branch**
- `branch_id` SMALLSERIAL PRIMARY KEY
- `name` VARCHAR(100) NOT NULL UNIQUE
- `city` VARCHAR(100) NOT NULL

**book_copy**
- `copy_id` BIGSERIAL PRIMARY KEY
- `book_id` BIGINT NOT NULL
- `branch_id` SMALLINT NOT NULL
- `barcode` VARCHAR(32) NOT NULL UNIQUE
- `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))

**loan** (modified)
- `copy_id` BIGINT (added column, foreign key)

**member** (modified)
- `member_since` (renamed from `joined_on`)
- `home_branch_id` SMALLINT (added column)

**book** (modified)
- `copies_free` (dropped)
- `copies_total` (dropped)
- `book_free_le_total` constraint (dropped)

#### Reads
- `book` (columns: `book_id`, `copies_free`, `copies_total`)
- `loan` (table structure)
- `member` (table structure)

#### Writes
- `branch` (INSERT)
- `book_copy` (INSERT)
- `loan` (ALTER TABLE ADD COLUMN)
- `member` (ALTER TABLE RENAME COLUMN, ALTER TABLE ADD COLUMN, UPDATE)
- `book` (ALTER TABLE DROP CONSTRAINT, ALTER TABLE DROP COLUMN)

#### Relationships
- `book_copy.book_id` -> `book.book_id`
- `book_copy.branch_id` -> `branch.branch_id`
- `loan.copy_id` -> `book_copy.copy_id`
- `member.home_branch_id` -> `branch.branch_id`

#### Routine logic
- Creates `branch` table and inserts a default 'Main' branch.
- Creates `book_copy` table with status constraints.
- Populates `book_copy` by generating rows for each total copy in `book`, assigning them to branch 1 and setting status based on `copies_free`.
- Adds `copy_id` foreign key to `loan` table.
- Renames `joined_on` to `member_since` in `member` table.
- Adds `home_branch_id` to `member` table and defaults existing rows to branch 1.
- Removes `copies_free`, `copies_total`, and associated constraint from `book` table.
- Creates index `idx_book_copy_book` on `book_copy.book_id`.

#### Dependencies
- `book`
- `loan`
- `member`
- `branch` (created within script)
- `book_copy` (created within script)
