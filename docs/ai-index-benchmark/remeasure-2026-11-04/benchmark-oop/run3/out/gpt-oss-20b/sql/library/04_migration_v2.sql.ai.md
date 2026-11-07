### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:55:48Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Adds library branches, moves book copies to branch-specific rows, and updates member and loan tables to reference branches and copies.

#### Purpose
- Introduces branch dimension for library locations.
- Normalizes book copies per branch.
- Updates member and loan schemas to reference new entities.

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
  - Adds `copy_id BIGINT REFERENCES book_copy (copy_id)`
- **member** (altered)
  - Renames `joined_on` → `member_since`
  - Adds `home_branch_id SMALLINT REFERENCES branch (branch_id)`
  - Updates existing rows: `home_branch_id = 1` where NULL
- **book** (altered)
  - Drops constraint `book_free_le_total`
  - Drops columns `copies_free`, `copies_total`
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_free, copies_total)
- `branch` (INSERT default row)
- `loan` (no reads)
- `member` (no reads)

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`)
- `book_copy`: 1 INSERT with `SELECT` generating `copies_total` rows per book
- `loan`: adds column `copy_id` (no data changes)
- `member`: adds column `home_branch_id`; updates existing rows to `1`
- `book`: drops columns and constraint (no data changes)

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Table `book` (existing)
- Table `loan` (existing)
- Table `member` (existing)
- Index `idx_book_copy_book` (created on `book_copy`)

---
