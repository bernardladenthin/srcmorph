### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:44:39Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Adds library branches and branch-assigned book copies, links loans to individual copies, and replaces member join-date naming while retiring book-level copy counters.

#### Purpose
- Introduces branch and copy-level inventory for a lending library.
- Migrates existing copies to the main branch and assigns members a home branch.

#### Type
- PostgreSQL schema migration; creates tables and an index, alters existing tables.

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
- `loan` — adds `copy_id BIGINT`, FK to `book_copy.copy_id`.
- `member` — renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT`, FK to `branch.branch_id`.
- `book` — drops constraint `book_free_le_total`; drops columns `copies_free` and `copies_total`.
- Index `idx_book_copy_book` on `book_copy (book_id)`.

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` — source rows and values for copy migration.
- `generate_series(1, book.copies_total)` — generates copy numbers.

#### Writes
- `branch`: 1 row — `('Main', 'Springfield')`.
- `book_copy`: inserts one row per generated copy for each `book` row; barcode uses `isbn` and generated number; status derives from `copies_free`.
- `loan`: adds `copy_id`.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id`; updates null `home_branch_id` values to `1`.
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
