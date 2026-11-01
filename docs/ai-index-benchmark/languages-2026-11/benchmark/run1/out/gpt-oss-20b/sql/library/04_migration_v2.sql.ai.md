### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:19:55Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, moves book copies to branch-specific rows, and updates member and loan tables accordingly.

#### Purpose
- Introduce branch hierarchy for a lending library.
- Relocate book copy tracking from book-level counters to per-copy rows.
- Update member and loan schemas to reference branches and specific copies.

#### Type
- PostgreSQL DDL: tables, indexes, column alterations, data migration.

#### Schema
- **branch**  
  - `branch_id SMALLSERIAL PRIMARY KEY`  
  - `name VARCHAR(100) NOT NULL UNIQUE`  
  - `city VARCHAR(100) NOT NULL`  
  - Index: `idx_branch_name` (implicit from UNIQUE on `name`)

- **book_copy**  
  - `copy_id BIGSERIAL PRIMARY KEY`  
  - `book_id BIGINT NOT NULL REFERENCES book(book_id)`  
  - `branch_id SMALLINT NOT NULL REFERENCES branch(branch_id)`  
  - `barcode VARCHAR(32) NOT NULL UNIQUE`  
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','ON_LOAN','LOST'))`  
  - Index: `idx_book_copy_book` on `(book_id)`

- **member** (altered)  
  - `member_since TIMESTAMP` (renamed from `joined_on`)  
  - `home_branch_id SMALLINT REFERENCES branch(branch_id)` (new column, default set to 1)

- **loan** (altered)  
  - `copy_id BIGINT REFERENCES book_copy(copy_id)` (new column)

#### Reads
- `book` table: `book_id`, `isbn`, `copies_free`, `copies_total` (for data migration).
- `branch` table: `branch_id` (used in INSERT default).
- `member` table: `home_branch_id` (for UPDATE).

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: `b.copies_total` rows inserted per book (exact count depends on existing data).
- `member`: 1 column added, 1 column renamed, 1 column added with default values, 1 UPDATE sets `home_branch_id` to 1 where NULL.
- `loan`: 1 column added.
- `book`: constraints and columns dropped.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Existing `book` table (must have `book_id`, `isbn`, `copies_free`, `copies_total`).
- Existing `loan` and `member` tables (altered).
- `generate_series` function used in data migration.

#### Routine logic
- Create `branch` table and insert a default branch.
- Create `book_copy` table with foreign keys to `book` and `branch`.
- Populate `book_copy` by generating one row per existing copy, assigning barcodes as `isbn-n` and status based on `copies_free`.
- Add `copy_id` column to `loan` referencing `book_copy`.
- Rename `joined_on` to `member_since` in `member`.
- Add `home_branch_id` to `member`, set default to main branch.
- Remove old copy counters from `book` and drop related constraint.
- Create index on `book_copy.book_id`.
