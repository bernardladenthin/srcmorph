### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:43:25Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library migration v2: introduces branch locations and per-branch physical book copies, links loans to specific copies, assigns members a home branch, and retires aggregate copy counters from the book catalog.

#### Purpose
- Schema migration v2 for a lending library: moves physical copy tracking from book-level counters (`copies_total`, `copies_free`) to per-branch `book_copy` rows.
- Associates each loan with a specific physical copy and each member with a home branch.

#### Type
- Migration script (DDL + DML), PostgreSQL dialect.
- Creates tables `branch` and `book_copy`, index `idx_book_copy_book`, alters `loan`, `member`, and `book`.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` PK
  - `name VARCHAR(100)` NOT NULL UNIQUE
  - `city VARCHAR(100)` NOT NULL
- `book_copy`
  - `copy_id BIGSERIAL` PK
  - `book_id BIGINT` NOT NULL FK → `book(book_id)`
  - `branch_id SMALLINT` NOT NULL FK → `branch(branch_id)`
  - `barcode VARCHAR(32)` NOT NULL UNIQUE
  - `status VARCHAR(12)` NOT NULL DEFAULT `'AVAILABLE'` CHECK (`status IN ('AVAILABLE','ON_LOAN','LOST')`)
- Index: `idx_book_copy_book` ON `book_copy(book_id)`
- Alterations:
  - `loan` ADD `copy_id BIGINT` REFERENCES `book_copy(copy_id)`
  - `member` RENAME `joined_on` → `member_since`; ADD `home_branch_id SMALLINT` REFERENCES `branch(branch_id)`
  - `book` DROP CONSTRAINT `book_free_le_total`; DROP COLUMN `copies_free`; DROP COLUMN `copies_total`

#### Reads
- `book`: `book_id`, `isbn`, `copies_free`, `copies_total` (in `INSERT INTO book_copy ... SELECT` via `CROSS JOIN LATERAL generate_series(1, b.copies_total)`)
- `member`: `home_branch_id` (in `UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL`)

#### Writes
- `branch`: INSERT `(name, city)` VALUES `('Main', 'Springfield')`
- `book_copy`: INSERT `(book_id, branch_id, barcode, status)` SELECT from `book` (barcode = `isbn || '-' || g.n`, status `AVAILABLE` if `g.n <= copies_free` else `ON_LOAN`, `branch_id = 1`)
- `loan`: schema write ADD COLUMN `copy_id`
- `member`: schema write RENAME/ADD columns; UPDATE `home_branch_id` to `1` where NULL
- `book`: schema write DROP CONSTRAINT `book_free_le_total`, DROP COLUMNS `copies_free`, `copies_total`

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Routine logic
- Wrapped in `BEGIN` / `COMMIT` transaction.
- Seeds `branch` with `Main` (assumed `branch_id = 1` via `SMALLSERIAL`).
- Backfills `book_copy`: one row per existing copy using `generate_series(1, copies_total)`, all assigned to branch `1`, barcode derived from `isbn` and sequence number, status split between `AVAILABLE` and `ON_LOAN` based on `copies_free`.
- Adds `loan.copy_id` FK to link loans to physical copies (no backfill shown).
- Renames `member.joined_on` to `member_since`.
- Adds `member.home_branch_id` FK and backfills existing members to branch `1`.
- Drops `book` constraint `book_free_le_total` and columns `copies_free`, `copies_total`.
- Creates index `idx_book_copy_book` on `book_copy(book_id)`.

#### Dependencies
- Pre-existing tables: `book`, `loan`, `member`
- PostgreSQL built-in: `generate_series`
- Dropped constraint: `book_free_le_total` on `book`
