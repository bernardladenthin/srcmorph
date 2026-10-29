### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:40:59Z
- G: 
- A: 0.0.0
- X: file
---
> This file models a library schema migration that adds branch management, redistributes copies across branches, renames a member column, and retires per-book copy counters.

#### Purpose
- Migrate the library schema to support multiple branches and retire legacy copy-count columns.
- Add a `branch` table, redistribute existing `book_copy` rows to the main branch, and add `copy_id` to `loan` for branch-level tracking.

#### Type
PostgreSQL migration script (DDL + DML).

#### Schema
- **branch**: `branch_id` SMALLSERIAL PK, `name` VARCHAR(100) NOT NULL UNIQUE, `city` VARCHAR(100) NOT NULL
- **book_copy**: `copy_id` BIGSERIAL PK, `book_id` BIGINT NOT NULL FK→`book.book_id`, `branch_id` SMALLINT NOT NULL FK→`branch.branch_id`, `barcode` VARCHAR(32) NOT NULL UNIQUE, `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK(status IN ('AVAILABLE','ON_LOAN','LOST'))
- **book**: columns `book_id`, `isbn`, `copies_total` dropped; `copies_free` dropped
- **loan**: added column `copy_id` BIGINT FK→`book_copy.copy_id`
- **member**: renamed `joined_on`→`member_since`, added `home_branch_id` SMALLINT FK→`branch.branch_id`

#### Reads
- `book` (via `SELECT b.book_id`, `b.isbn`, `b.copies_total` in INSERT ... SELECT)
- `book_copy` (via FK references `book_copy.copy_id`, `book_copy.book_id`)
- `branch` (via FK references `branch.branch_id`)

#### Writes
- **branch**: INSERT (name, city) → row 'Main','Springfield'
- **book_copy**: INSERT (book_id, branch_id, barcode, status) → rows from `book` CROSS JOIN generate_series
- **loan**: ALTER ADD COLUMN `copy_id` BIGINT FK→`book_copy.copy_id`
- **member**: RENAME COLUMN `joined_on`→`member_since`; ALTER ADD COLUMN `home_branch_id` SMALLINT FK→`branch.branch_id`; UPDATE set `home_branch_id`=1
- **book**: DROP CONSTRAINT `book_free_le_total`; DROP COLUMN `copies_free`; DROP COLUMN `copies_total`
- **index**: CREATE INDEX `idx_book_copy_book` ON `book_copy`(`book_id`)

#### Relationships
- `book_copy.book_id` → `book.book_id`
- `book_copy.branch_id` → `branch.branch_id`
- `loan.copy_id` → `book_copy.copy_id` (added)
- `member.home_branch_id` → `branch.branch_id`
- `book.book_id` referenced by `book_copy.book_id` (FK)

#### Routine logic
- Insert branch 'Main' in Springfield.
- Populate `book_copy` rows using `book` data: barcode = `isbn`-`n`, status = AVAILABLE if `n <= copies_free` else ON_LOAN.
- Add `copy_id` to `loan`, referencing `book_copy.copy_id`.
- Rename `member.joined_on` to `member_since`.
- Add `member.home_branch_id`, default to 1.
- Drop `book` columns `copies_free`, `copies_total` and their constraint.

#### Dependencies
- `book` (table, columns: `book_id`, `isbn`, `copies_total`, `copies_free`)
- `book_copy` (table)
- `branch` (table, newly created)
- `loan` (table)
- `member` (table)
- `generate_series` (LATERAL function)
