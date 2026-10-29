### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T05:06:33Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library schema migration v2: adds branches, moves book copies per branch, renames a column, and removes obsolete copy counters.

#### Purpose
- Introduce a **branch** dimension to the library model.
- Migrate existing book copies to the new `book_copy` table, assigning each copy to a branch.
- Add a `copy_id` foreign key to `loan` to track which copy was loaned.
- Rename `member.joined_on` to `member_since`.
- Add a `home_branch_id` to `member` to indicate the member's preferred branch.
- Remove the legacy `copies_free` and `copies_total` columns from `book`.

#### Type
- PostgreSQL DDL (migration script)

#### Schema
**branch**
- `branch_id` SMALLSERIAL PRIMARY KEY
- `name` VARCHAR(100) NOT NULL UNIQUE
- `city` VARCHAR(100) NOT NULL

**book_copy**
- `copy_id` BIGSERIAL PRIMARY KEY
- `book_id` BIGINT NOT NULL REFERENCES book(book_id)
- `branch_id` SMALLINT NOT NULL REFERENCES branch(branch_id)
- `barcode` VARCHAR(32) NOT NULL UNIQUE
- `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','ON_LOAN','LOST'))

#### Reads
- `book` (columns: `book_id`, `copies_total`, `copies_free`, `isbn`)
- `member` (column: `joined_on`)

#### Writes
- `branch` (INSERT)
- `book_copy` (INSERT, ALTER)
- `loan` (ALTER)
- `member` (ALTER, UPDATE)

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Routine logic
- Inserts one row per existing copy into `book_copy`, using `generate_series` to create unique barcodes (`isbn-n`).
  - `status` set to **AVAILABLE** if `n <= copies_free`, otherwise **ON_LOAN**.
- Adds `copy_id` column to `loan` referencing `book_copy`.
- Renames `member.joined_on` to `member_since`.
- Adds `home_branch_id` column to `member` referencing `branch`.
- Updates existing members to set `home_branch_id = 1` (Main branch) where it was NULL.
- Drops the `copies_free` and `copies_total` columns from `book`.
- Creates an index `idx_book_copy_book` on `book_copy.book_id`.

#### Dependencies
- `book` table
- `member` table
- `loan` table
- `branch` table (created in this script)
