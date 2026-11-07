### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:06:39Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Adds branch-based inventory to the lending library, assigns existing book copies to the main branch, and replaces book-level copy counters with copy records.

#### Purpose
- Introduces library branches and per-copy barcode, branch, and lending status.
- Migrates existing inventory and member branch data; retires `book.copies_free` and `book.copies_total`.

#### Type
- PostgreSQL schema migration; creates tables `branch`, `book_copy` and index `idx_book_copy_book`.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key.
  - `name VARCHAR(100) NOT NULL UNIQUE`.
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key.
  - `book_id BIGINT NOT NULL` — FK to `book.book_id`.
  - `branch_id SMALLINT NOT NULL` — FK to `branch.branch_id`.
  - `barcode VARCHAR(32) NOT NULL UNIQUE`.
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`.
- Alters `loan`: adds `copy_id BIGINT` referencing `book_copy.copy_id`.
- Alters `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT` referencing `branch.branch_id`.
- Alters `book`: drops constraint `book_free_le_total` and columns `copies_free`, `copies_total`.
- Index `idx_book_copy_book`: on `book_copy (book_id)`.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` (copy migration query).
- `member`: `home_branch_id` (null check in update).

#### Writes
- `branch`: 1 row (`Main`, `Springfield`).
- `book_copy`: one `INSERT ... SELECT`; data-dependent row count, generated from each book’s `copies_total`; assigns `AVAILABLE` when `g.n <= copies_free`, otherwise `ON_LOAN`.
- `member`: updates `home_branch_id` to `1` where null.
- `loan`: adds nullable `copy_id` column.
- `member`: renames `joined_on` to `member_since` and adds `home_branch_id`.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Existing tables: `book`, `loan`, `member`.
- PostgreSQL function: `generate_series`.
