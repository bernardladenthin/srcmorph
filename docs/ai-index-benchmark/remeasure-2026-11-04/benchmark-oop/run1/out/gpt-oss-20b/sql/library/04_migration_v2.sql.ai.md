### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:26:14Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Adds branch locations, creates a book_copy table linking books to branches, and updates loan and member tables to reference copies and branches.

#### Purpose
- Introduces branch entity and book_copy table for multi‑branch inventory.
- Migrates existing book copy data into book_copy.
- Updates loan and member tables to reference copies and home branches.

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
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','ON_LOAN','LOST'))`
- **loan**
  - added column `copy_id BIGINT REFERENCES book_copy (copy_id)`
- **member**
  - renamed column `joined_on` → `member_since`
  - added column `home_branch_id SMALLINT REFERENCES branch (branch_id)`
- **idx_book_copy_book**
  - on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, copies_total, copies_free, isbn)
- `member` (UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL)

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`)
- `book_copy`: 1 INSERT statement generating `copies_total` rows per book (family: `copies_total` rows per book)
- `loan`: 1 column added (no data written)
- `member`: 1 column added, 1 UPDATE setting `home_branch_id` to 1 where NULL
- `book`: columns `copies_free`, `copies_total` dropped; constraint `book_free_le_total` dropped

#### Relationships
- `book_copy.book_id` → `book.book_id`
- `book_copy.branch_id` → `branch.branch_id`
- `loan.copy_id` → `book_copy.copy_id`
- `member.home_branch_id` → `branch.branch_id`

#### Dependencies
- `book` table (existing)
- `loan` table (existing)
- `member` table (existing)
- `book_copy` table (new)
- `branch` table (new)

---
