### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:21:43Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Adds branch-based inventory to a lending library, assigns existing book copies to the main branch, and associates members and loans with branch-specific copies.

#### Purpose
- Introduces branches and per-copy inventory, migrating existing book copy counts into individual copy records.
- Renames the member join-date column and removes the retired book-level copy counters.

#### Type
- PostgreSQL migration script; creates tables and an index, alters tables, and migrates data.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key (unnamed constraint).
  - `name VARCHAR(100)` — NOT NULL, UNIQUE (unnamed constraint).
  - `city VARCHAR(100)` — NOT NULL.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key (unnamed constraint).
  - `book_id BIGINT` — NOT NULL; FK to `book.book_id` (unnamed constraint).
  - `branch_id SMALLINT` — NOT NULL; FK to `branch.branch_id` (unnamed constraint).
  - `barcode VARCHAR(32)` — NOT NULL, UNIQUE (unnamed constraint).
  - `status VARCHAR(12)` — NOT NULL, DEFAULT `'AVAILABLE'`; CHECK `status IN ('AVAILABLE', 'ON_LOAN', 'LOST')` (unnamed constraint).
- Alters `loan`: adds `copy_id BIGINT`, FK to `book_copy.copy_id` (unnamed constraint).
- Alters `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT`, FK to `branch.branch_id` (unnamed constraint).
- Alters `book`: drops constraint `book_free_le_total`; drops columns `copies_free` and `copies_total`.
- Index: `idx_book_copy_book` on `book_copy (book_id)`; default method and operator class.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` (copy-migration SELECT).
- `member.home_branch_id` (migration UPDATE condition).

#### Writes
- `branch`: 1 seed row, `('Main', 'Springfield')`.
- `book_copy`: one `INSERT ... SELECT`; one row per generated series value from 1 through each book’s `copies_total`; barcode uses `isbn` and series number; status is `AVAILABLE` through `copies_free`, otherwise `ON_LOAN`. Exact total depends on source `book` data.
- `loan`: adds `copy_id` column.
- `member`: renames `joined_on`; adds `home_branch_id`; updates rows where it is NULL to `1`.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.
- Creates index `idx_book_copy_book`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`.
- Function: `generate_series`.
