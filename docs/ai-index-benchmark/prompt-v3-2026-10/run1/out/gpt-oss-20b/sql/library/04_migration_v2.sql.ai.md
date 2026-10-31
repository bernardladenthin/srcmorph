### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:54:46Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, tracks each book copy per branch, and updates member and loan tables to reference copies and branches.

#### Purpose
- Introduce branch hierarchy for a lending library.
- Replace per-book copy counters with explicit book_copy records.
- Update member and loan tables to reference branches and copies.

#### Type
- Table: `branch`
- Table: `book_copy`
- Table: `loan` (ALTER)
- Table: `member` (ALTER)
- Table: `book` (ALTER)
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
- **book**
  - `copies_free` column dropped
  - `copies_total` column dropped
  - Constraint `book_free_le_total` dropped
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, copies_total, copies_free, isbn)
- `branch` (INSERT default branch)
- `loan` (no reads)
- `member` (no reads)

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`)
- `book_copy`: 1 + 1 = 2 rows per existing book copy (total rows = sum of `copies_total` across all books)
- `loan`: 1 column added (no data written)
- `member`: 1 column added, 1 column renamed, `home_branch_id` set to 1 for NULLs
- `book`: columns and constraint dropped

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Table `book` (existing)
- Table `loan` (existing)
- Table `member` (existing)
- Table `book_copy` (new)
- Table `branch` (new)
