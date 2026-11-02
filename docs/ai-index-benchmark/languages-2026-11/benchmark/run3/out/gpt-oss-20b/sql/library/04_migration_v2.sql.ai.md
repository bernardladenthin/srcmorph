### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:05:15Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, moves book copies to branch-specific records, and updates member and loan tables to reference branches and copies.

#### Purpose
- Introduces branch hierarchy for a lending library.
- Normalizes book copy data to include branch and barcode.
- Updates member and loan schemas to reference branches and copies.

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
  - `copy_id BIGINT REFERENCES book_copy (copy_id)` (new column)
- **member**
  - `member_since DATE` (renamed from `joined_on`)
  - `home_branch_id SMALLINT REFERENCES branch (branch_id)` (new column)
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, copies_total, copies_free, isbn)
- `branch` (INSERT default branch)
- `book_copy` (none)

#### Writes
- `branch`: 1 row (`INSERT INTO branch VALUES ('Main', 'Springfield')`)
- `book_copy`: 1 + N rows per existing book copy (one per `copies_total` per book)
- `loan`: adds column `copy_id` (no data written)
- `member`: renames column, adds `home_branch_id`, updates existing rows to `1`
- `book`: drops columns and constraint (no data written)

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
