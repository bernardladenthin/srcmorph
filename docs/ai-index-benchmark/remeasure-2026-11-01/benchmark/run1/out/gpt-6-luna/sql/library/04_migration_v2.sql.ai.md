### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:21:58Z
- G: 
- A: 0.0.0
- X: file
---
> Adds branch-based copy inventory to a lending library, assigns existing copies and members to the main branch, and retires book-level copy counters.

#### Purpose
- Introduces library branches and per-copy barcode and loan-status tracking.
- Migrates existing copies and member branch assignments; removes obsolete copy counters from `book`.

#### Type
- PostgreSQL schema migration; creates tables `branch`, `book_copy` and index `idx_book_copy_book`.

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
- `loan`: adds `copy_id BIGINT` referencing `book_copy.copy_id` (FK constraint name not declared).
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT` referencing `branch.branch_id` (FK constraint name not declared).
- `book`: drops constraint `book_free_le_total`; drops columns `copies_free`, `copies_total`.
- Index `idx_book_copy_book`: on `book_copy (book_id)`; default method.

#### Reads
- `book`: reads `book_id`, `isbn`, `copies_total`, `copies_free` to generate copy rows.
- `member`: reads `home_branch_id` to select rows requiring the default branch assignment.

#### Writes
- `branch`: one row inserted (`name = 'Main'`, `city = 'Springfield'`).
- `book_copy`: one `INSERT ... SELECT` generates one row per `book` copy, with barcode from `isbn` and generated sequence number, branch ID `1`, and status based on `copies_free`; exact row count depends on source data.
- `loan`: adds `copy_id`.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id`; updates null `home_branch_id` values to `1`.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.
- Creates index `idx_book_copy_book`.

#### Relationships
- `book_copy.book_id -> book.book_id` (foreign key).
- `book_copy.branch_id -> branch.branch_id` (foreign key).
- `loan.copy_id -> book_copy.copy_id` (foreign key).
- `member.home_branch_id -> branch.branch_id` (foreign key).
- Migration query: `book.book_id` supplies `book_copy.book_id`.

#### Dependencies
- Tables: `book`, `loan`, `member`.
- Built-in PostgreSQL function: `generate_series`.
