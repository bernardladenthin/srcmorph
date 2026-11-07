### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:12:07Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Adds branch-based inventory to a lending library, assigns existing book copies to the main branch, and updates loan and member records for branch-aware circulation.

#### Purpose
- Introduces branches and individual `book_copy` inventory records.
- Migrates existing copy counts into copy rows, then removes the per-book copy counters.

#### Type
- PostgreSQL migration script; creates tables and an index, alters existing tables, and performs DML.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key.
  - `name VARCHAR(100) NOT NULL UNIQUE` — unique constraint unnamed.
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key.
  - `book_id BIGINT NOT NULL` — FK to `book(book_id)`.
  - `branch_id SMALLINT NOT NULL` — FK to `branch(branch_id)`.
  - `barcode VARCHAR(32) NOT NULL UNIQUE` — unique constraint unnamed.
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'` — CHECK allows `AVAILABLE`, `ON_LOAN`, `LOST` (constraint unnamed).
- `loan`: adds `copy_id BIGINT` — FK to `book_copy(copy_id)`.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT` — FK to `branch(branch_id)`.
- `book`: drops constraint `book_free_le_total`; drops columns `copies_free` and `copies_total`.
- Index: `idx_book_copy_book` on `book_copy(book_id)`; default index method.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` (used to generate and classify copy rows).

#### Writes
- `branch`: 1 row inserted — `Main`, `Springfield`.
- `book_copy`: one `INSERT ... SELECT`; inserts one row per generated copy for each `book` row, based on `copies_total` (exact total depends on source data).
- `loan`: adds `copy_id`.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id`; updates `home_branch_id = 1` where it is `NULL`.
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
