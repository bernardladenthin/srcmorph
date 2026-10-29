### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:57:03Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates the lending library schema to support multi-branch inventory by introducing branch and book_copy tables, retiring per-book copy counters, and linking loans and members to specific branches.

#### Purpose
*   Implements schema version 2 for a PostgreSQL lending library to enable multi-branch operations and track individual physical copies.
*   Transitions from global copy counts to granular `book_copy` records linked to specific `branch` locations.

#### Type
PostgreSQL DDL migration script (multiple objects: tables, indexes, triggers via ALTER).

#### Schema
**Table: branch**
*   `branch_id` SMALLSERIAL PRIMARY KEY
*   `name` VARCHAR(100) NOT NULL UNIQUE
*   `city` VARCHAR(100) NOT NULL

**Table: book_copy**
*   `copy_id` BIGSERIAL PRIMARY KEY
*   `book_id` BIGINT NOT NULL (FK to book)
*   `branch_id` SMALLINT NOT NULL (FK to branch)
*   `barcode` VARCHAR(32) NOT NULL UNIQUE
*   `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK ('AVAILABLE', 'ON_LOAN', 'LOST')

**Index**
*   `idx_book_copy_book` on `book_copy(book_id)`

#### Reads
*   `book` table (columns: `book_id`, `copies_free`, `copies_total`)
*   `generate_series` function (LATERAL join source)

#### Writes
*   `branch` table (INSERT)
*   `book_copy` table (INSERT)
*   `loan` table (ALTER TABLE ADD COLUMN `copy_id`)
*   `member` table (ALTER TABLE RENAME COLUMN `joined_on` to `member_since`, ADD COLUMN `home_branch_id`, UPDATE `home_branch_id`)
*   `book` table (DROP CONSTRAINT `book_free_le_total`, DROP COLUMNS `copies_free`, `copies_total`)

#### Relationships
*   `book_copy.book_id` -> `book.book_id`
*   `book_copy.branch_id` -> `branch.branch_id`
*   `loan.copy_id` -> `book_copy.copy_id`
*   `member.home_branch_id` -> `branch.branch_id`

#### Routine logic
*   Initialize `branch` table with a default 'Main' branch in 'Springfield'.
*   Populate `book_copy` by expanding existing `book` row counts into individual rows using `generate_series`, assigning them to branch ID 1 and setting status based on free vs total count.
*   Add foreign key reference `copy_id` to the `loan` table to link transactions to specific copies.
*   Rename `joined_on` to `member_since` in `member` table for semantic clarity.
*   Assign `home_branch_id` to all existing members (defaulting to 1) and add the column to `member`.
*   Remove legacy copy counting columns (`copies_free`, `copies_total`) and their constraints from `book`.
*   Create index on `book_copy.book_id` to optimize lookups.

#### Dependencies
*   `book`
*   `loan`
*   `member`
*   `branch` (created within script)
*   `book_copy` (created within script)
