### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:57:25Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Adds library branches, creates a book_copy table to track individual copies per branch, and updates loan and member tables to reference copies and branches.

#### Purpose
- Introduce branch management and per-copy tracking.
- Replace book-level copy counters with a dedicated book_copy table.
- Update member and loan schemas to reference branches and copies.

#### Type
- Table: `branch`
- Table: `book_copy`
- Table: `loan` (ALTER)
- Table: `member` (ALTER)
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
- **loan**
  - `copy_id BIGINT REFERENCES book_copy (copy_id)` (new column)
- **member**
  - `joined_on` renamed to `member_since`
  - `home_branch_id SMALLINT REFERENCES branch (branch_id)` (new column)
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, copies_total, copies_free, isbn)
- `branch` (INSERT default branch)
- `member` (UPDATE home_branch_id where NULL)

#### Writes
- `branch`: 1 row (`Main`, `Springfield`)
- `book_copy`: 1 + 1 + … + 1 rows per existing copy (total = `b.copies_total` per book)
- `loan`: adds `copy_id` column (no data change)
- `member`: adds `home_branch_id` column and updates existing rows to 1 where NULL

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
