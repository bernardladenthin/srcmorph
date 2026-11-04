### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:10:30Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Migrates the library schema to support multiple branches, creates a book_copy table, and updates loan and member tables accordingly.

#### Purpose
- Add branch support and per‑copy tracking.
- Replace book copy counters with explicit book_copy rows.
- Update member and loan tables for new branch relationships.

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
  - Index: `idx_book_copy_book` on `book_id`
- **loan** (altered)
  - Added column `copy_id BIGINT REFERENCES book_copy (copy_id)`
- **member** (altered)
  - Renamed column `joined_on` → `member_since`
  - Added column `home_branch_id SMALLINT REFERENCES branch (branch_id)`
  - Updated existing rows: `home_branch_id = 1` where null
- **book** (altered)
  - Dropped constraint `book_free_le_total`
  - Dropped columns `copies_free`, `copies_total`

#### Reads
- `book` (SELECT book_id, copies_total, copies_free)
- `branch` (INSERT default row)
- `book_copy` (none)

#### Writes
- `branch`: 1 row (`INSERT INTO branch VALUES ('Main', 'Springfield')`)
- `book_copy`: 1 + 1 + … rows (one per existing copy; total equals sum of `copies_total` across all books)
- `loan`: 1 new column added (no data written)
- `member`: 1 new column added, 1 update to set `home_branch_id = 1` where null
- `book`: 1 constraint dropped, 2 columns dropped

#### Relationships
- `book_copy.book_id → book.book_id`
- `book_copy.branch_id → branch.branch_id`
- `loan.copy_id → book_copy.copy_id`
- `member.home_branch_id → branch.branch_id`

#### Dependencies
- Existing tables: `book`, `loan`, `member`
- New table: `branch`, `book_copy`
- New index: `idx_book_copy_book` on `book_copy(book_id)`
- Existing constraint: `book_free_le_total` (removed)
