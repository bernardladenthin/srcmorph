### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:04:49Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates the lending library schema to support multi-branch inventory by introducing branch and book_copy tables, retiring per-book copy counters, and linking loans and members to specific branches.

#### Purpose
*   Implements schema version 2 for a PostgreSQL lending library to enable multi-branch operations and track individual physical copies.
*   Transitions from global book copy counts to granular `book_copy` records assigned to specific branches.

#### Type
PostgreSQL DDL migration script (multiple objects: tables, triggers via constraints, indexes).

#### Schema
**Table: branch**
*   `branch_id SMALLSERIAL PRIMARY KEY`
*   `name VARCHAR(100) NOT NULL UNIQUE`
*   `city VARCHAR(100) NOT NULL`

**Table: book_copy**
*   `copy_id BIGSERIAL PRIMARY KEY`
*   `book_id BIGINT NOT NULL` (FK to `book.book_id`)
*   `branch_id SMALLINT NOT NULL` (FK to `branch.branch_id`)
*   `barcode VARCHAR(32) NOT NULL UNIQUE`
*   `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`

**Index:**
*   `idx_book_copy_book` on `book_copy(book_id)`

#### Reads
*   `book` table (columns: `book_id`, `copies_free`, `copies_total`)
*   `loan` table (structure read for column addition context)
*   `member` table (structure read for column rename/addition context)

#### Writes
*   `branch` table (INSERT row for 'Main' branch)
*   `book_copy` table (INSERT rows generated from `book` data)
*   `loan` table (ALTER TABLE ADD COLUMN `copy_id`)
*   `member` table (ALTER TABLE RENAME COLUMN `joined_on` to `member_since`; ALTER TABLE ADD COLUMN `home_branch_id`; UPDATE `home_branch_id`)
*   `book` table (DROP CONSTRAINT `book_free_le_total`; DROP COLUMNS `copies_free`, `copies_total`)

#### Relationships
*   `book_copy.book_id -> book.book_id`
*   `book_copy.branch_id -> branch.branch_id`
*   `loan.copy_id -> book_copy.copy_id`
*   `member.home_branch_id -> branch.branch_id`

#### Routine logic
*   Creates `branch` table with auto-incrementing ID and unique name constraint.
*   Seeds initial 'Main' branch in Springfield.
*   Generates `book_copy` rows for every existing book copy using `generate_series`, assigning them to branch_id 1 and setting status based on previous `copies_free` vs `copies_total` logic.
*   Adds foreign key reference `copy_id` to the `loan` table.
*   Renames `joined_on` to `member_since` in `member` table.
*   Adds `home_branch_id` to `member` table and defaults existing nulls to branch 1.
*   Removes legacy `copies_free` and `copies_total` columns and associated check constraints from `book`.
*   Creates index on `book_copy.book_id` for performance.

#### Dependencies
*   `book`
*   `loan`
*   `member`
*   `branch` (created within script)
*   `book_copy` (created within script)
