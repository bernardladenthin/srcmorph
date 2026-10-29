### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:50:46Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, tracks individual book copies per branch, and updates member and loan tables to reference these copies.

#### Purpose
- Introduce branch management and per-copy tracking.
- Migrate existing book copy data to new `book_copy` table.
- Update member and loan schemas for branch association.

#### Type
- PostgreSQL migration script (DDL).

#### Schema
- **branch**
  - `branch_id SMALLSERIAL PRIMARY KEY`
  - `name VARCHAR(100) NOT NULL UNIQUE`
  - `city VARCHAR(100) NOT NULL`
- **book_copy**
  - `copy_id BIGSERIAL PRIMARY KEY`
  - `book_id BIGINT NOT NULL REFERENCES book(book_id)`
  - `branch_id SMALLINT NOT NULL REFERENCES branch(branch_id)`
  - `barcode VARCHAR(32) NOT NULL UNIQUE`
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','ON_LOAN','LOST'))`
  - Index: `idx_book_copy_book (book_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_total, copies_free)
- `member` (for updating `home_branch_id`)

#### Writes
- `branch` (INSERT)
- `book_copy` (INSERT)
- `loan` (ADD COLUMN `copy_id`)
- `member` (RENAME column, ADD column, UPDATE `home_branch_id`)
- `book` (DROP constraints and columns)

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Existing tables: `book`, `loan`, `member`
- New tables: `branch`, `book_copy`
- Index: `idx_book_copy_book`

#### Routine logic
- Create `branch` table and seed with 'Main'.
- Create `book_copy` table with status constraints.
- Populate `book_copy` from existing `book` copies, assigning barcodes as `isbn-n`.
- Add `copy_id` column to `loan` referencing `book_copy`.
- Rename `member.joined_on` to `member_since`.
- Add `home_branch_id` to `member`, default to branch 1.
- Remove old copy counters from `book` and associated constraint.
