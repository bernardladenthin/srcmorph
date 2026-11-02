### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:32:04Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Adds library branches, moves book copies to branch-specific rows, and updates member and loan tables to reference branches and copies.

#### Purpose
- Introduce branch dimension for library locations.
- Relocate book copy tracking from book table to dedicated `book_copy` table.
- Update member and loan tables to reference branches and copies.

#### Type
- Table: `branch`, `book_copy`
- Altered tables: `loan`, `member`, `book`
- Index: `idx_book_copy_book`

#### Schema
- **branch**
  - `branch_id SMALLSERIAL PK`
  - `name VARCHAR(100) NOT NULL UNIQUE`
  - `city VARCHAR(100) NOT NULL`
- **book_copy**
  - `copy_id BIGSERIAL PK`
  - `book_id BIGINT NOT NULL FK → book(book_id)`
  - `branch_id SMALLINT NOT NULL FK → branch(branch_id)`
  - `barcode VARCHAR(32) NOT NULL UNIQUE`
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','ON_LOAN','LOST'))`
- **loan**
  - `copy_id BIGINT FK → book_copy(copy_id)` (new column)
- **member**
  - `member_since DATE` (renamed from `joined_on`)
  - `home_branch_id SMALLINT FK → branch(branch_id)` (new column)
- **book**
  - `copies_free` and `copies_total` columns dropped; constraint `book_free_le_total` removed.
- **Index**
  - `idx_book_copy_book` on `book_copy(book_id)`

#### Reads
- `book` (SELECT book_id, copies_total, copies_free, isbn)
- `branch` (INSERT default row)
- `loan` (no reads)
- `member` (UPDATE home_branch_id)

#### Writes
- `branch`: 1 row (`Main`, `Springfield`)
- `book_copy`: 1 + 1 = 2 INSERTs (first inserts one row per existing copy; second inserts one row per book)
- `member`: 1 UPDATE (sets `home_branch_id` to 1 where NULL)
- `book`: 1 UPDATE (drops columns and constraint)

#### Relationships
- `book_copy.book_id → book.book_id`
- `book_copy.branch_id → branch.branch_id`
- `loan.copy_id → book_copy.copy_id`
- `member.home_branch_id → branch.branch_id`

#### Dependencies
- Existing table `book`
- Existing table `loan`
- Existing table `member`
- Existing table `book_copy` (created in this file)
- Existing index `idx_book_copy_book` (created in this file)
