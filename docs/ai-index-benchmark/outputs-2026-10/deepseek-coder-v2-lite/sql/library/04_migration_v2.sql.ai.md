### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T02:02:03Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library: schema migration v2 (PostgreSQL). Adds branches, moves copies per branch, renames a column and retires the per-book copy counters.

#### Purpose
- Adds a new table `branch` to manage branches.
- Creates a new table `book_copy` to track book copies per branch.
- Updates the `loan` table to include `copy_id`.
- Renames `joined_on` to `member_since` in `member`.
- Adds `home_branch_id` to `member`.
- Drops unused columns `copies_free` and `copies_total` from `book`.

#### Type
PostgreSQL DDL.

#### Schema
- **branch**
  - `branch_id SMALLSERIAL PRIMARY KEY`
  - `name VARCHAR(100) NOT NULL UNIQUE`
  - `city VARCHAR(100) NOT NULL`
- **book_copy**
  - `copy_id BIGSERIAL PRIMARY KEY`
  - `book_id BIGINT NOT NULL REFERENCES book (book_id)`
  - `branch_id SMALLINT NOT NULL REFERENCES branch (branch_id)`
  - `barcode VARCHAR(32) NOT NULL UNIQUE`
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'`
    - CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))
- **book**
  - Removed: `copies_free`, `copies_total`

#### Reads
- Reads from `book` for `book_id` in `book_copy`.
- Reads from `generate_series` for `copy_id` and `status` in `book_copy`.
- Reads from `member` for `home_branch_id`.

#### Writes
- Writes to `branch` during insertion.
- Writes to `book_copy` during insertion.
- Writes to `loan` for `copy_id`.
- Writes to `member` for `home_branch_id`.

#### Relationships
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Routine logic
- Inserts initial data into `branch` and `book_copy`.
- Updates `loan` to include `copy_id`.
- Renames `joined_on` to `member_since` in `member`.
- Sets `home_branch_id` to 1 for all members where not specified.
- Drops unused columns from `book`.

#### Dependencies
- References `book` for `book_id`.
- References `branch` for `branch_id`.
- Uses `generate_series` for series generation.
