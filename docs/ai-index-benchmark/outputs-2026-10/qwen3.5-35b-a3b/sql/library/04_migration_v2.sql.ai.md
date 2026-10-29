### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:36:00Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates library schema to support multi-branch inventory tracking by introducing branch and book_copy tables while retiring per-book copy counters.

#### Purpose
*   Schema migration v2 for lending library system.
*   Enables branch-specific inventory tracking and member home branch assignment.

#### Type
*   PostgreSQL migration script (DDL/DML).

#### Schema
*   `branch`: `branch_id` SMALLSERIAL PRIMARY KEY, `name` VARCHAR(100) NOT NULL UNIQUE, `city` VARCHAR(100) NOT NULL.
*   `book_copy`: `copy_id` BIGSERIAL PRIMARY KEY, `book_id` BIGINT NOT NULL, `branch_id` SMALLINT NOT NULL, `barcode` VARCHAR(32) NOT NULL UNIQUE, `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK ('AVAILABLE', 'ON_LOAN', 'LOST').
*   `loan`: Added column `copy_id` BIGINT.
*   `member`: Renamed `joined_on` to `member_since`, Added `home_branch_id` SMALLINT.
*   `book`: Dropped columns `copies_free`, `copies_total`, Dropped constraint `book_free_le_total`.
*   Index: `idx_book_copy_book` on `book_copy(book_id)`.

#### Reads
*   `book`: `book_id`, `copies_free`, `copies_total` (SELECT source for `book_copy` insertion).
*   `member`: All rows (UPDATE source for `home_branch_id` default).

#### Writes
*   `branch`: Inserted seed row ('Main', 'Springfield').
*   `book_copy`: Inserted rows derived from `book` table.
*   `loan`: Schema modified (ADD COLUMN `copy_id`).
*   `member`: Schema modified (RENAME COLUMN, ADD COLUMN, UPDATE `home_branch_id`).
*   `book`: Schema modified (DROP COLUMN, DROP CONSTRAINT).

#### Relationships
*   `book_copy.book_id` -> `book.book_id`.
*   `book_copy.branch_id` -> `branch.branch_id`.
*   `loan.copy_id` -> `book_copy.copy_id`.
*   `member.home_branch_id` -> `branch.branch_id`.

#### Routine logic
*   Create `branch` table and seed 'Main' branch.
*   Create `book_copy` table with status constraints.
*   Migrate existing book copies from `book` table to `book_copy` using `generate_series`.
*   Link `loan` table to `book_copy` via new `copy_id` column.
*   Rename `member.joined_on` to `member_since`.
*   Assign `member.home_branch_id` to default branch (1).
*   Remove legacy copy count columns from `book` table.

#### Dependencies
*   `book` table.
*   `loan` table.
*   `member` table.
