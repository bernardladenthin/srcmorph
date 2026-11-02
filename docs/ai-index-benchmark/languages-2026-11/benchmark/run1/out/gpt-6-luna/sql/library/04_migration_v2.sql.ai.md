### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:15:34Z
- G: 
- A: 0.0.0
- X: file
---
> Adds branch-based lending inventory by assigning book copies to branches and linking loans and members to copies and home branches.

#### Purpose
- Migrates the lending library to branch-level copy inventory.
- Replaces per-book copy counters with individual `book_copy` records.

#### Type
- PostgreSQL schema migration script; runs within `BEGIN` / `COMMIT`.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key
  - `name VARCHAR(100) NOT NULL UNIQUE`
  - `city VARCHAR(100) NOT NULL`
- `book_copy`
  - `copy_id BIGSERIAL` — primary key
  - `book_id BIGINT NOT NULL` — FK to `book.book_id`
  - `branch_id SMALLINT NOT NULL` — FK to `branch.branch_id`
  - `barcode VARCHAR(32) NOT NULL UNIQUE`
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`
- `loan`
  - Adds `copy_id BIGINT` — FK to `book_copy.copy_id`
- `member`
  - Renames `joined_on` to `member_since`
  - Adds `home_branch_id SMALLINT` — FK to `branch.branch_id`
- `book`
  - Drops constraint `book_free_le_total`
  - Drops columns `copies_free`, `copies_total`
- Index `idx_book_copy_book` on `book_copy (book_id)`; default method and operator class.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` (copy-generation query).

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: `INSERT ... SELECT` creates one row per generated copy for each book; exact total depends on existing `book` data.
- `loan`: adds `copy_id` column.
- `member`: renames `joined_on`; adds `home_branch_id`; updates `home_branch_id = 1` where NULL.
- `book`: drops `copies_free`, `copies_total`, and constraint `book_free_le_total`.

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
