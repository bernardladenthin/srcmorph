### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:47:16Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, tracks each book copy per branch, and updates member and loan tables accordingly.

#### Purpose
- Introduce branch dimension for library locations.
- Replace per-book copy counters with explicit `book_copy` rows.
- Update loan and member tables to reference copies and branches.

#### Type
- Table: `branch`
- Table: `book_copy`
- Table alterations: `loan`, `member`, `book`
- Index: `idx_book_copy_book`

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
- **loan** (altered)
  - `copy_id BIGINT REFERENCES book_copy (copy_id)`
- **member** (altered)
  - `joined_on` renamed to `member_since`
  - `home_branch_id SMALLINT REFERENCES branch (branch_id)`
- **book** (altered)
  - `copies_free` and `copies_total` columns removed
  - Constraint `book_free_le_total` dropped
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_free, copies_total)
- `branch` (INSERT default row)
- `loan` (none)
- `member` (none)

#### Writes
- `branch`: 1 row (`Main`, `Springfield`)
- `book_copy`: 1 + 1 = 2 rows per existing book copy (one per copy, status based on `copies_free`)
- `loan`: adds column `copy_id` (no data written)
- `member`: adds column `home_branch_id`, updates existing rows to `1` where null
- `book`: drops columns and constraint

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Table `book` (existing)
- Table `loan` (existing)
- Table `member` (existing)
- Sequence `branch.branch_id` (implicit)
- Sequence `book_copy.copy_id` (implicit)
