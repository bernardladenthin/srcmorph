### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:48:33Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Adds branch locations, creates a book_copy table to track individual copies per branch, and updates loan and member tables to reference copies and branches.

#### Purpose
- Introduce branch entity and per‑copy tracking.
- Migrate existing book copy counts into book_copy rows.
- Update loan and member tables to reference copies and branches.

#### Type
- Table: `branch`
- Table: `book_copy`
- Table: `loan` (altered)
- Table: `member` (altered)
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
  - Added column `copy_id BIGINT REFERENCES book_copy (copy_id)`
- **member**
  - Renamed column `joined_on` → `member_since`
  - Added column `home_branch_id SMALLINT REFERENCES branch (branch_id)`
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_total, copies_free)
- `branch` (INSERT default branch)
- `book_copy` (none)

#### Writes
- `branch`: 1 row (`Main`, `Springfield`)
- `book_copy`: 1 + 1 = 2 INSERT statements
  - First INSERT: 1 row (default branch)
  - Second INSERT: `b.copies_total` rows per book (family)
- `loan`: 1 column added (no data written)
- `member`: 1 column added, 1 column renamed, 1 column updated (home_branch_id set to 1 where NULL)

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Table `book` (existing)
- Table `loan` (existing)
- Table `member` (existing)
- Sequence `branch.branch_id_seq` (implicit from SMALLSERIAL)
- Sequence `book_copy.copy_id_seq` (implicit from BIGSERIAL)
