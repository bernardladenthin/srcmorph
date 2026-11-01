### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:43:33Z
- G: 
- A: 0.0.0
- X: file
---
> Adds branch-based inventory to a lending library, assigns existing book copies to the main branch, and replaces book-level copy counters with copy-level records.

#### Purpose
- Creates branch and book-copy records; migrates existing copies to the main branch.
- Adds copy and home-branch references, renames the member join date, and removes book copy counters.

#### Type
- PostgreSQL migration; creates tables and an index.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key.
  - `name VARCHAR(100) NOT NULL UNIQUE`.
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key.
  - `book_id BIGINT NOT NULL` — FK to `book(book_id)`.
  - `branch_id SMALLINT NOT NULL` — FK to `branch(branch_id)`.
  - `barcode VARCHAR(32) NOT NULL UNIQUE`.
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`.
- `loan.copy_id BIGINT` — FK to `book_copy(copy_id)`, nullable.
- `member.joined_on` renamed to `member.member_since`.
- `member.home_branch_id SMALLINT` — FK to `branch(branch_id)`, nullable; existing NULL values set to `1`.
- `book` — drops constraint `book_free_le_total` and columns `copies_free`, `copies_total`.
- Index `idx_book_copy_book` on `book_copy(book_id)`.

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` — used to generate copy rows.
- `generate_series(1, b.copies_total)` — generates per-book copy numbers.

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: one row per generated series value for each `book` row; total is the sum of `copies_total` across books at migration time.
- `loan`: adds `copy_id` column.
- `member`: renames `joined_on`; adds `home_branch_id`; updates NULL `home_branch_id` values to `1`.
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
