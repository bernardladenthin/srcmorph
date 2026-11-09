### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:02:29Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Migrates the library schema to version 2: adds branches, relocates book copies to a new table, updates member and loan tables, and removes per‑book copy counters.

#### Purpose
- Introduce branch hierarchy and per‑branch book copies.
- Update member and loan records to reference branches and copies.
- Remove legacy copy counters from the book table.

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
- `book` (SELECT book_id, isbn, copies_total, copies_free)
- `member` (UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL)

#### Writes
- `branch`: 1 row (`INSERT INTO branch VALUES ('Main', 'Springfield')`)
- `book_copy`: 1 × `copies_total` rows per book (family: one INSERT per book, total rows = sum of `copies_total`)
- `loan`: adds column `copy_id` (no data written)
- `member`: renames column, adds `home_branch_id`, updates 1 row per member with NULL `home_branch_id`
- `book`: drops constraints and columns (no data written)

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Existing tables: `book`, `loan`, `member`
- New table: `branch`, `book_copy`
- Index: `idx_book_copy_book` on `book_copy`

#### Routine logic
- Create `branch` table and insert the main branch.
- Create `book_copy` table with foreign keys to `book` and `branch`.
- Populate `book_copy` by generating a row for each copy of every book, assigning the main branch (id 1) and barcode derived from ISBN and copy number; status set to AVAILABLE if the copy number is within `copies_free`, otherwise ON_LOAN.
- Alter `loan` to reference `book_copy`.
- Rename `member.joined_on` to `member_since` and add `home_branch_id` referencing `branch`; set default to main branch for existing members.
- Remove legacy copy counters from `book` and drop related constraint.
- Create index on `book_copy.book_id` for efficient lookups.
