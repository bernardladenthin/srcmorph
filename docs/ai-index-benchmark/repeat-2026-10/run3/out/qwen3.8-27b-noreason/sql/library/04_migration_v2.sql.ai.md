### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:48:17Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library schema migration v2: introduces branch entities, decomposes per-book copy counters into individual book_copy rows, links loans to specific copies, and assigns members to home branches.

#### Purpose
- Migrate the lending library schema from a counter-based copy model to a row-per-copy model with branch support.
- Add `branch` table, `book_copy` table, and foreign key links between `loan`, `member`, and `branch`.

#### Type
- PostgreSQL migration script (DDL + DML) wrapped in a transaction (`BEGIN` / `COMMIT`).

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — PK
  - `name VARCHAR(100)` — NOT NULL, UNIQUE
  - `city VARCHAR(100)` — NOT NULL
- `book_copy`
  - `copy_id BIGSERIAL` — PK
  - `book_id BIGINT` — NOT NULL, FK → `book(book_id)`
  - `branch_id SMALLINT` — NOT NULL, FK → `branch(branch_id)`
  - `barcode VARCHAR(32)` — NOT NULL, UNIQUE
  - `status VARCHAR(12)` — NOT NULL, DEFAULT `'AVAILABLE'`, CHECK IN (`'AVAILABLE'`, `'ON_LOAN'`, `'LOST'`)
- `loan` (altered)
  - `copy_id BIGINT` — added, FK → `book_copy(copy_id)`
- `member` (altered)
  - `joined_on` → renamed to `member_since`
  - `home_branch_id SMALLINT` — added, FK → `branch(branch_id)`
- `book` (altered)
  - `copies_free` — dropped
  - `copies_total` — dropped
  - constraint `book_free_le_total` — dropped

#### Reads
- `book` — `book_id`, `isbn`, `copies_free`, `copies_total` (source for populating `book_copy`)
- `generate_series(1, b.copies_total)` — lateral subquery generating row numbers per book

#### Writes
- `branch` — INSERT (`name`, `city`)
- `book_copy` — INSERT (`book_id`, `branch_id`, `barcode`, `status`)
- `loan` — ALTER ADD COLUMN `copy_id`
- `member` — ALTER RENAME COLUMN, ALTER ADD COLUMN `home_branch_id`, UPDATE `home_branch_id`
- `book` — ALTER DROP CONSTRAINT, ALTER DROP COLUMN ×2

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Routine logic
- Seed `branch` with a single row: `('Main', 'Springfield')`.
- Populate `book_copy` by cross-joining each `book` with `generate_series(1, copies_total)`; barcode = `isbn || '-' || n`; status = `'AVAILABLE'` if `n <= copies_free` else `'ON_LOAN'`; all assigned to `branch_id = 1`.
- Add `loan.copy_id` FK to `book_copy`.
- Rename `member.joined_on` → `member_since`; add `member.home_branch_id` FK to `branch`; backfill to `1`.
- Drop `book.copies_free`, `book.copies_total`, and the `book_free_le_total` CHECK constraint.
- Create index `idx_book_copy_book` on `book_copy(book_id)`.

#### Dependencies
- `book` (pre-existing table)
- `loan` (pre-existing table)
- `member` (pre-existing table)
- `generate_series` (PostgreSQL built-in function)
- Index: `idx_book_copy_book`
