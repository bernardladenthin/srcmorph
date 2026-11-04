### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:58:27Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Adds branch-based copy tracking to a lending library, assigns existing copies to the main branch, and replaces book-level copy counters with copy-level inventory.

#### Purpose
- Introduces library branches and individual book copies with barcode and availability status.
- Links loans and members to copies and branches; removes per-book copy counters.

#### Type
- PostgreSQL migration; creates tables `branch`, `book_copy` and index `idx_book_copy_book`.

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
- `loan`: adds `copy_id BIGINT` — FK to `book_copy.copy_id`.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT` — FK to `branch.branch_id`.
- `book`: drops constraint `book_free_le_total` and columns `copies_free`, `copies_total`.
- Index `idx_book_copy_book` on `book_copy (book_id)`; method not specified.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` — used to generate copy rows and determine initial status.
- `member`: `home_branch_id` — read by the `IS NULL` filter during update.

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: 1 `INSERT ... SELECT` populates one row per generated copy; exact row count depends on `book.copies_total`.
- `loan`: adds `copy_id`.
- `member`: renames `joined_on`; adds `home_branch_id`; updates `home_branch_id` to `1` where null.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.
- Creates index `idx_book_copy_book`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`
- Copy-generation query: `book.book_id`; branch assignment uses `branch.branch_id = 1`.

#### Dependencies
- Tables: `book`, `loan`, `member`.
