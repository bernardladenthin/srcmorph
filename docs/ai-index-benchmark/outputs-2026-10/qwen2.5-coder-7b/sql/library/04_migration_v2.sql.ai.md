### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:17:43Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates the lending library schema to version 2, adding branches, managing book copies per branch, renaming a column, and retiring per-book copy counters.

#### Purpose
- Adds branches to the lending library schema.
- Manages book copies per branch.
- Renames a column in the member table.
- Retires per-book copy counters.

#### Type
PostgreSQL DDL

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
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`
- **loan**
  - Adds `copy_id BIGINT REFERENCES book_copy (copy_id)`
- **member**
  - Renames `joined_on` to `member_since`
  - Adds `home_branch_id SMALLINT REFERENCES branch (branch_id)`

#### Reads
- `book`
  - `book_id`
  - `copies_total`
  - `isbn`
- `generate_series`
  - `n`
- `branch`
  - `branch_id`
- `loan`
  - `copy_id`

#### Writes
- `branch`
  - `branch_id`
  - `name`
  - `city`
- `book_copy`
  - `copy_id`
  - `book_id`
  - `branch_id`
  - `barcode`
  - `status`
- `loan`
  - `copy_id`
- `member`
  - `member_since`
  - `home_branch_id`

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book`
- `generate_series`
- `branch`
- `loan`
