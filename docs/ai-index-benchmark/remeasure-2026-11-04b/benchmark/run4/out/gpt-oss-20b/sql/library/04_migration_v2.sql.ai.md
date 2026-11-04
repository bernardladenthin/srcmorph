### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:51:37Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Migrates the library schema to support multiple branches, introduces a book_copy table, and updates member and loan tables accordingly.

#### Purpose
- Add branch support and per‑copy tracking.
- Replace book copy counters with explicit book_copy rows.
- Update member and loan tables for new relationships.

#### Type
- PostgreSQL migration script (DDL).

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
  - Index: `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_free, copies_total)
- `member` (UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL)

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: 1 × `b.copies_total` rows inserted per book (family: total rows = sum of copies_total across all books).
- `loan`: adds column `copy_id` (no data written).
- `member`: renames column, adds `home_branch_id`, updates existing rows to 1.
- `book`: drops columns and constraint (no data written).

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Existing tables: `book`, `loan`, `member`.
- New table: `branch`, `book_copy`.
- Index: `idx_book_copy_book`.
