### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:40:59Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Adds library branches, creates a book_copy table linking books to branches, and updates member and loan tables to reference branches and copies.

#### Purpose
- Introduces branch management and per‑branch book copies.
- Migrates existing book copy data into the new structure.
- Updates member and loan tables to reference branches and copies.

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
  - `copy_id BIGINT REFERENCES book_copy (copy_id)` (new column)
- **member**
  - `member_since TIMESTAMP` (renamed from `joined_on`)
  - `home_branch_id SMALLINT REFERENCES branch (branch_id)` (new column)
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_total, copies_free)
- `branch` (INSERT default branch)
- `member` (UPDATE home_branch_id where NULL)

#### Writes
- `branch`: 1 row (`Main`, `Springfield`)
- `book_copy`: 1 + 1 = 2 INSERT statements
  - First INSERT: 1 row (default branch)
  - Second INSERT: `b.copies_total` rows per book (family)
- `member`: 1 UPDATE (set `home_branch_id` to 1 where NULL)
- `loan`: 1 ALTER TABLE (adds column)

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book` table (existing)
- `loan` table (existing)
- `member` table (existing)

---
