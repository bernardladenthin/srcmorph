### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:46:11Z
- G: 
- A: 0.0.0
- X: file
---
> Adds branch-based lending inventory by assigning existing book copies to a branch, linking loans and members to branches, and removing book-level copy counters.

#### Purpose
- Introduces `branch` and per-copy `book_copy` inventory for a lending library.
- Migrates existing copy counts into individual copy records and updates loan/member/book schemas.

#### Type
- PostgreSQL schema migration script; transactional (`BEGIN` / `COMMIT`).

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key (unnamed in source)
  - `name VARCHAR(100) NOT NULL UNIQUE` — unique constraint unnamed in source
  - `city VARCHAR(100) NOT NULL`
- `book_copy`
  - `copy_id BIGSERIAL` — primary key (unnamed in source)
  - `book_id BIGINT NOT NULL` — FK to `book(book_id)` (unnamed in source)
  - `branch_id SMALLINT NOT NULL` — FK to `branch(branch_id)` (unnamed in source)
  - `barcode VARCHAR(32) NOT NULL UNIQUE` — unique constraint unnamed in source
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))` — check constraint unnamed in source
  - Index: `idx_book_copy_book (book_id)`
- `loan`
  - Adds `copy_id BIGINT` — FK to `book_copy(copy_id)` (unnamed in source)
- `member`
  - Renames `joined_on` to `member_since`
  - Adds `home_branch_id SMALLINT` — FK to `branch(branch_id)` (unnamed in source)
- `book`
  - Drops constraint `book_free_le_total`
  - Drops columns `copies_free`, `copies_total`

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` — used to generate copy rows and determine initial copy status.
- `member.home_branch_id` — checked for NULL during backfill.

#### Writes
- `branch`: inserts exactly 1 row, `('Main', 'Springfield')`.
- `book_copy`: inserts one row per generated existing copy; row count depends on `SUM(book.copies_total)`. Sets `branch_id = 1`, barcode to `book.isbn || '-' || copy_number`, and status from `copies_free`.
- `loan`: adds `copy_id` column.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id`; sets NULL `home_branch_id` values to `1`.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`
- Function: `generate_series` (used to generate one row per copy)
