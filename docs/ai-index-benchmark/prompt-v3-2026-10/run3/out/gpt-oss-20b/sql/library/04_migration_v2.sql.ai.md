### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:43:34Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, moves book copies to branch-specific rows, and updates member and loan tables to reference branches and copies.

#### Purpose
- Introduce branch hierarchy for a lending library.
- Relocate book copy data to a dedicated table with branch association.
- Update member and loan tables to reference branches and copies.

#### Type
- Table: `branch`, `book_copy`
- Alterations: `loan`, `member`, `book`
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
  - `member_since TIMESTAMP` (renamed from `joined_on`)
  - `home_branch_id SMALLINT REFERENCES branch (branch_id)`
- **book** (altered)
  - `copies_free` and `copies_total` columns dropped; constraint `book_free_le_total` removed.
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, copies_total, copies_free, isbn)
- `branch` (INSERT default branch)
- `member` (UPDATE home_branch_id where NULL)

#### Writes
- `branch`: 1 row (`Main`, `Springfield`)
- `book_copy`: 1 + 1 = 2 rows per existing book copy (one per copy, status set accordingly)
- `loan`: adds `copy_id` column (no data changes)
- `member`: adds `home_branch_id` column, updates existing rows to 1
- `book`: drops columns and constraint

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book` table (existing)
- `loan` table (existing)
- `member` table (existing)
- `book_copy` table (new)
- `branch` table (new)
