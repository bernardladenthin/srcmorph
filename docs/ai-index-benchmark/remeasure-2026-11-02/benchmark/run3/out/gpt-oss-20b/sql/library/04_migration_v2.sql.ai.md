### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:02:28Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Adds library branches, assigns each book copy to a branch, and updates member and loan tables to reference the new branch and copy structure.

#### Purpose
- Introduces branch hierarchy for a lending library.
- Migrates existing book copies to a new `book_copy` table with branch association.
- Updates member and loan tables to use branch and copy references.

#### Type
- PostgreSQL migration script (DDL only).

#### Schema
- **branch**
  - `branch_id SMALLSERIAL PRIMARY KEY`
  - `name VARCHAR(100) NOT NULL UNIQUE`
  - `city VARCHAR(100) NOT NULL`
  - Index: `idx_branch_name` (implicit from UNIQUE)
- **book_copy**
  - `copy_id BIGSERIAL PRIMARY KEY`
  - `book_id BIGINT NOT NULL REFERENCES book (book_id)`
  - `branch_id SMALLINT NOT NULL REFERENCES branch (branch_id)`
  - `barcode VARCHAR(32) NOT NULL UNIQUE`
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`
  - Index: `idx_book_copy_book` (book_id)
- **member** (altered)
  - `member_since TIMESTAMP` (renamed from `joined_on`)
  - `home_branch_id SMALLINT REFERENCES branch (branch_id)`
- **loan** (altered)
  - `copy_id BIGINT REFERENCES book_copy (copy_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_free, copies_total)
- `branch` (INSERT default branch)
- `member` (UPDATE home_branch_id)
- `loan` (no reads)

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: 1 × `b.copies_total` rows inserted per book; total rows equal sum of `copies_total` across all books.
- `member`: 1 column added, 1 column renamed, 1 column updated (set to 1 where NULL).
- `loan`: 1 column added.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Existing tables: `book`, `member`, `loan`.
- Existing constraint: `book_free_le_total` (removed).
- Existing columns: `book.copies_free`, `book.copies_total`, `member.joined_on`.

---
