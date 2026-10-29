### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:09Z
- G: 
- A: 0.0.0
- X: file
---
> This migration adds branch management, redistributes book copies per branch, and retires legacy per-book counters in the lending library.

#### Purpose
- Introduces a `branch` table and redistributes existing `book_copy` rows from the main branch to new branches.
- Adds a `copy_id` foreign key to `loan`, renames `member.joined_on` to `member_since`, and assigns each member a home branch.
- Removes `copies_free` and `copies_total` from `book` and replaces them with a single index on `book_copy.book_id`.

#### Type
PostgreSQL migration script (DDL + data manipulation)

#### Schema
**branch**
- `branch_id` SMALLSERIAL PK
- `name` VARCHAR(100) NOT NULL UNIQUE
- `city` VARCHAR(100) NOT NULL

**book_copy**
- `copy_id` BIGSERIAL PK
- `book_id` BIGINT NOT NULL FK → `book.book_id`
- `branch_id` SMALLINT NOT NULL FK → `branch.branch_id`
- `barcode` VARCHAR(32) NOT NULL UNIQUE
- `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))

**book** (post-migration columns removed)
- Removed columns: `copies_free`, `copies_total`
- Removed constraint: `book_free_le_total`

**member**
- Renamed column: `joined_on` → `member_since`
- Added column: `home_branch_id` SMALLINT FK → `branch.branch_id`

**loan**
- Added column: `copy_id` BIGINT FK → `book_copy.copy_id`

Indexes: `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (via `book_id`, `isbn`, `copies_total` in INSERT ... SELECT)
- `book_copy` (via `book_id`, `branch_id`, `barcode`, `status` in INSERT ... SELECT)
- `branch` (referenced by FK, seed row inserted)
- `generate_series` (lateral join source)

#### Writes
- `branch`: INSERT seed row ('Main', 'Springfield')
- `book_copy`: INSERT per-copy rows from `book` × `generate_series`; ALTER `loan` ADD `copy_id` FK; UPDATE `member` set `home_branch_id`
- `member`: RENAME `joined_on` → `member_since`; ADD `home_branch_id`; UPDATE default value
- `book`: DROP constraint `book_free_le_total`; DROP columns `copies_free`, `copies_total`
- `book_copy`: CREATE INDEX `idx_book_copy_book` on `book_id`

#### Relationships
- `book_copy.book_id` → `book.book_id`
- `book_copy.branch_id` → `branch.branch_id`
- `loan.copy_id` → `book_copy.copy_id` (added)
- `member.home_branch_id` → `branch.branch_id` (added)
- `branch` is referenced by `book_copy` and `member`

#### Routine logic
- Seed `branch` table with one row.
- Populate `book_copy` by crossing each `book` with a `generate_series` up to its `copies_total`, assigning status based on `copies_free` (pre-migration data).
- Add `copy_id` to `loan` and link each row to a `book_copy`.
- Rename `member.joined_on` to `member_since` and add `home_branch_id`, defaulting to `1`.
- Drop `book` columns `copies_free`, `copies_total` and their constraint.
- Create index on `book_copy.book_id`.

#### Dependencies
- `book` (table, columns: `book_id`, `isbn`, `copies_free`, `copies_total`)
- `book_copy` (table, referenced by FK and index)
- `branch` (table, new, referenced by FK)
- `loan` (table, altered to add `copy_id`)
- `member` (table, altered to rename column and add FK)
- `generate_series` (function used in INSERT ... SELECT)
