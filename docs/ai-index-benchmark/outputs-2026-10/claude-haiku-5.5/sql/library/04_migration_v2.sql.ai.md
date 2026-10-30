### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:31:52Z
- G: 
- A: 0.0.0
- X: file
---
> Migration v2 for a lending library that adds branches, moves physical copies into a per-branch table, and retires per-book copy counters.

#### Purpose
- Introduces `branch` and `book_copy` tables to track individual physical copies per branch.
- Migrates existing `book` copy counters into `book_copy` rows and drops those counters from `book`.

#### Type
- Migration script (PostgreSQL), wrapped in `BEGIN` / `COMMIT`.
- Contains tables, inserts, updates, alters, and an index.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` PK
  - `name VARCHAR(100)` NOT NULL, UNIQUE
  - `city VARCHAR(100)` NOT NULL
- `book_copy` (created here)
  - `copy_id BIGSERIAL` PK
  - `book_id BIGINT` NOT NULL, FK -> `book.book_id`
  - `branch_id SMALLINT` NOT NULL, FK -> `branch.branch_id`
  - `barcode VARCHAR(32)` NOT NULL, UNIQUE
  - `status VARCHAR(12)` NOT NULL, DEFAULT `'AVAILABLE'`, CHECK in (`AVAILABLE`, `ON_LOAN`, `LOST`)
- Added columns
  - `loan.copy_id BIGINT` FK -> `book_copy.copy_id`
  - `member.home_branch_id SMALLINT` FK -> `branch.branch_id`
- Renamed column
  - `member.joined_on` -> `member.member_since`
- Dropped from `book`
  - constraint `book_free_le_total`
  - column `copies_free`
  - column `copies_total`
- Indexes
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book.isbn`, `book.book_id`, `book.copies_free`, `book.copies_total`
- `generate_series(1, book.copies_total)` via `CROSS JOIN LATERAL`
- `member.home_branch_id` (filter `IS NULL`)

#### Writes
- `branch`: insert row `('Main', 'Springfield')`
- `book_copy`: insert one row per copy
  - `book_id` from `book.book_id`
  - `branch_id` = 1
  - `barcode` = `book.isbn || '-' || n`
  - `status` = `AVAILABLE` if `n <= copies_free`, else `ON_LOAN`
- `loan`: add column `copy_id`
- `member`: rename `joined_on` to `member_since`; add `home_branch_id`; update `home_branch_id` = 1 where NULL
- `book`: drop constraint `book_free_le_total`; drop `copies_free`, `copies_total`

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Routine logic
- Migration steps run in order inside one transaction:
  - Create `branch`, insert `Main` branch.
  - Create `book_copy`, backfill one row per existing copy from `book` counters.
  - Add `loan.copy_id`.
  - Rename `member.joined_on`, add and backfill `member.home_branch_id` with 1.
  - Drop `book_free_le_total` and the `copies_free` / `copies_total` columns.
  - Create `idx_book_copy_book`.
- Effect: copy availability moves from counters on `book` to rows in `book_copy`.

#### Dependencies
- Tables: `book`, `branch`, `book_copy`, `loan`, `member`
- Sequences: implicit from `SMALLSERIAL` / `BIGSERIAL` (`branch_id`, `copy_id`)
- Functions: `generate_series`
- Constraint: `book_free_le_total`
- Index: `idx_book_copy_book`
