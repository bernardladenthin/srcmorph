### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:19:17Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, creates a per‑branch book copy table, and updates member and loan tables to reference copies and branches.

#### Purpose
- Introduce branch hierarchy for a lending library.
- Replace per‑book copy counters with explicit copy records.
- Update member and loan schemas to track branch and copy associations.

#### Type
- Table: `branch`
- Table: `book_copy`
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
  - `copies_free` column dropped
  - `copies_total` column dropped
  - Constraint `book_free_le_total` dropped
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_free, copies_total)
- `branch` (INSERT default branch)
- `loan` (none)
- `member` (UPDATE home_branch_id)
- `book_copy` (none)

#### Writes
- `branch`: 1 row (`INSERT INTO branch VALUES ('Main', 'Springfield')`)
- `book_copy`: 1 INSERT statement generating one row per existing copy; total rows equal sum of `copies_total` across all books.
- `loan`: 1 column added (`copy_id`), no data changes.
- `member`: 1 column added (`home_branch_id`), 1 UPDATE sets `home_branch_id = 1` where NULL.
- `book`: columns and constraint dropped, no data changes.

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

#### Routine logic
- None (pure DDL/DML).
