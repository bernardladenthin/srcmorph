### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:42:02Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, assigns each book copy to a branch, and updates member and loan tables to reference the new branch and copy structures.

#### Purpose
- Introduce branch hierarchy for a lending library.
- Relocate book copy tracking from book-level counters to a dedicated table.
- Update member and loan schemas to reference branches and copies.

#### Type
- Table: `branch`, `book_copy`
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
- **loan**
  - `copy_id BIGINT REFERENCES book_copy (copy_id)`
- **member**
  - `member_since TIMESTAMP` (renamed from `joined_on`)
  - `home_branch_id SMALLINT REFERENCES branch (branch_id)`
- **book**
  - `copies_free` column removed
  - `copies_total` column removed
  - Constraint `book_free_le_total` removed
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_free, copies_total)
- `branch` (INSERT default branch)
- `member` (UPDATE home_branch_id where NULL)

#### Writes
- `branch`: 1 row (`INSERT INTO branch VALUES ('Main', 'Springfield')`)
- `book_copy`: 1 row per existing copy (`INSERT ... SELECT ...`), total equals sum of `copies_total` across all books.
- `loan`: 1 column added, no data written.
- `member`: 1 column added, 1 column renamed, 1 column updated (`UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL`).

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
- Index `idx_book_copy_book` (new)
