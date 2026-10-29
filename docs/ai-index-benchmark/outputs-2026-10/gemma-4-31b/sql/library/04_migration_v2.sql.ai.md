### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:34:46Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates lending library schema to support multiple branches and individual book copy tracking.

#### Purpose
* Introduce branch-based organization for the library.
* Transition from aggregate book copy counters to individual tracking of each book copy.

#### Type
Migration script (PostgreSQL).

#### Schema
`branch`
* `branch_id` SMALLSERIAL (PK)
* `name` VARCHAR(100) NOT NULL UNIQUE
* `city` VARCHAR(100) NOT NULL

`book_copy`
* `copy_id` BIGSERIAL (PK)
* `book_id` BIGINT NOT NULL (FK)
* `branch_id` SMALLINT NOT NULL (FK)
* `barcode` VARCHAR(32) NOT NULL UNIQUE
* `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' (CHECK: 'AVAILABLE', 'ON_LOAN', 'LOST')
* Index: `idx_book_copy_book` on `book_id`

#### Reads
* `book`: `book_id`, `isbn`, `copies_free`, `copies_total` (used to populate `book_copy`).

#### Writes
* `branch`: Inserts initial seed data.
* `book_copy`: Inserts records based on `book` copy counts.
* `loan`: Adds `copy_id` column.
* `member`: Renames `joined_on` to `member_since`; adds `home_branch_id` column; updates `home_branch_id` to default value.
* `book`: Drops `copies_free` and `copies_total` columns; drops constraint `book_free_le_total`.

#### Relationships
* `book_copy.book_id` -> `book.book_id`
* `book_copy.branch_id` -> `branch.branch_id`
* `loan.copy_id` -> `book_copy.copy_id`
* `member.home_branch_id` -> `branch.branch_id`

#### Dependencies
* `book`
* `loan`
* `member`
