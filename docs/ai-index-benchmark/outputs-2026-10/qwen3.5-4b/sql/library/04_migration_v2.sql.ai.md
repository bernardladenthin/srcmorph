### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:04:12Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates the lending library schema to v2 by introducing a branch table, linking copies to branches, and retiring per-book copy counters in favor of a single copy tracking model.

#### Purpose
- Creates a `branch` table to model physical library locations.
- Creates a `book_copy` table to track individual physical copies linked to books and branches.
- Migrates existing book copies from the main branch to the new `book_copy` table structure.
- Removes per-book copy counters (`copies_free`, `copies_total`) from the `book` table.
- Adds a `copy_id` foreign key to the `loan` table to link loans to specific physical copies.

#### Type
Table (PostgreSQL)

#### Schema
**branch**
- `branch_id` SMALLSERIAL PRIMARY KEY
- `name` VARCHAR(100) NOT NULL UNIQUE
- `city` VARCHAR(100) NOT NULL

**book_copy**
- `copy_id` BIGSERIAL PRIMARY KEY
- `book_id` BIGINT NOT NULL REFERENCES book (book_id)
- `branch_id` SMALLINT NOT NULL REFERENCES branch (branch_id)
- `barcode` VARCHAR(32) NOT NULL UNIQUE
- `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))

#### Reads
- `book` table (columns: `book_id`, `isbn`, `copies_free`, `copies_total`)
- `generate_series` function (via `LATERAL` join)

#### Writes
- `branch` table (columns: `name`, `city`)
- `book_copy` table (columns: `book_id`, `branch_id`, `barcode`, `status`)
- `loan` table (column: `copy_id`)
- `member` table (column: `home_branch_id`)

#### Relationships
- `book_copy.book_id` -> `book.book_id`
- `book_copy.branch_id` -> `branch.branch_id`
- `loan.copy_id` -> `book_copy.copy_id`
- `member.home_branch_id` -> `branch.branch_id`

#### Routine logic
- Inserts a default `branch` row named 'Main' in 'Springfield'.
- Generates rows in `book_copy` for every existing book copy, assigning them to the main branch and calculating initial status based on `copies_free` vs `copies_total`.
- Adds `copy_id` column to `loan` table referencing `book_copy`.
- Renames `member.joined_on` to `member_since`.
- Adds `home_branch_id` column to `member` table referencing `branch`.
- Drops `book.copies_free` and `book.copies_total` columns and the associated constraint `book_free_le_total`.
- Creates index `idx_book_copy_book` on `book_copy(book_id)`.

#### Dependencies
- `book` table
- `member` table
- `loan` table
- `branch` table
- `book_copy` table
