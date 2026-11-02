### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:59:24Z
- G: 
- A: 0.0.0
- X: file
---
> Adds branch-based inventory to a lending library, assigns existing book copies to the main branch, and replaces per-book copy counters with copy-level records.

#### Purpose
- Creates branch and physical-copy records, migrates existing inventory, and adds branch/copy references to members and loans.
- Renames the member join-date column and removes book-level copy counters.

#### Type
- PostgreSQL migration script; creates tables and an index.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key (constraint name not declared).
  - `name VARCHAR(100) NOT NULL UNIQUE` — unique constraint name not declared.
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key (constraint name not declared).
  - `book_id BIGINT NOT NULL` — FK to `book.book_id` (constraint name not declared).
  - `branch_id SMALLINT NOT NULL` — FK to `branch.branch_id` (constraint name not declared).
  - `barcode VARCHAR(32) NOT NULL UNIQUE` — unique constraint name not declared.
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))` — check constraint name not declared.
- Existing-table changes:
  - `loan`: adds `copy_id BIGINT` with FK to `book_copy.copy_id` (constraint name not declared).
  - `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT` with FK to `branch.branch_id` (constraint name not declared).
  - `book`: drops constraint `book_free_le_total`; drops columns `copies_free` and `copies_total`.
- Index: `idx_book_copy_book` on `book_copy (book_id)`; method not specified.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` — copy-generation query.
- `member.home_branch_id` — `UPDATE` predicate checking for `NULL`.

#### Writes
- `branch`: one row inserted (`Main`, `Springfield`).
- `book_copy`: one `INSERT ... SELECT`; inserts one row per generated series value from `1` through each book’s `copies_total` (total depends on source data). Sets branch to `1`, barcode to `isbn || '-' || series value`, and status based on `copies_free`.
- `member.home_branch_id`: sets `NULL` values to `1`.
- Schema writes: alters `loan`, `member`, and `book`; creates `branch`, `book_copy`, and `idx_book_copy_book`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book`
- `loan`
- `member`
- PostgreSQL `generate_series()`
