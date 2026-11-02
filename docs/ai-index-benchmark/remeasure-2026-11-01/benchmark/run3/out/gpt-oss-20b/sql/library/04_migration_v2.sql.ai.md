### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:06:45Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, assigns each book copy to a branch, and updates member and loan tables to reference the new branch and copy structure.

#### Purpose
- Introduce branch hierarchy for the library.
- Relocate book copy tracking from book-level counters to a dedicated `book_copy` table.
- Update member and loan tables to reference branches and copies.

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
  - `copy_id BIGINT REFERENCES book_copy (copy_id)`
- **member** (altered)
  - `member_since TIMESTAMP` (renamed from `joined_on`)
  - `home_branch_id SMALLINT REFERENCES branch (branch_id)`
- **book** (altered)
  - `copies_free` and `copies_total` columns dropped; constraint `book_free_le_total` removed.
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_free, copies_total)
- `branch` (INSERT default branch)
- `loan` (no reads in this migration)
- `member` (UPDATE home_branch_id)

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`)
- `book_copy`: 1 + 1 = 2 rows per existing book copy (total rows = sum of `copies_total` across all books)
- `member`: 1 column added, 1 column renamed, 1 column updated (home_branch_id set to 1 where NULL)
- `book`: columns and constraint dropped

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
