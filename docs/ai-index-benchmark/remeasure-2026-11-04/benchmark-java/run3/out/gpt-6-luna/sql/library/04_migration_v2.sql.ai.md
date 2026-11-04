### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:45:10Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Adds branch-based lending locations, assigns existing book copies to the main branch, and updates member and loan records for branch and copy tracking.

#### Purpose
- Introduces library branches and per-copy inventory with barcode and availability status.
- Migrates existing copy counts into `book_copy`, associates members with a home branch, and retires book-level copy counters.

#### Type
- PostgreSQL migration; creates tables and an index.

#### Schema
- `branch`:
  - `branch_id SMALLSERIAL` — primary key (constraint name not specified).
  - `name VARCHAR(100) NOT NULL UNIQUE` — unique constraint name not specified.
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`:
  - `copy_id BIGSERIAL` — primary key (constraint name not specified).
  - `book_id BIGINT NOT NULL` — FK to `book.book_id` (constraint name not specified).
  - `branch_id SMALLINT NOT NULL` — FK to `branch.branch_id` (constraint name not specified).
  - `barcode VARCHAR(32) NOT NULL UNIQUE` — unique constraint name not specified.
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))` — check constraint name not specified.
- `loan`: adds `copy_id BIGINT` with FK to `book_copy.copy_id` (constraint name not specified).
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT` with FK to `branch.branch_id` (constraint name not specified).
- `book`: drops constraint `book_free_le_total`; drops columns `copies_free`, `copies_total`.
- Index `idx_book_copy_book` on `book_copy (book_id)`; method not specified.

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` — source for copy generation and status assignment.
- `member.home_branch_id` — tested for NULL by the update.

#### Writes
- `branch`: 1 row (`Main`, `Springfield`).
- `book_copy`: `INSERT ... SELECT` generates one row per book and `generate_series(1, book.copies_total)` value; total depends on existing book data.
- `loan`: adds `copy_id`.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id`; sets NULL `home_branch_id` values to `1`.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.
- Creates index `idx_book_copy_book`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book`
- `loan`
- `member`
- `generate_series`
