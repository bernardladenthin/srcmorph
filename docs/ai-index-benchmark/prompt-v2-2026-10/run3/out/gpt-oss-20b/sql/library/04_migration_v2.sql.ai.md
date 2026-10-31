### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:08:27Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, creates a book_copy table linking books to branches, and updates member and loan tables to reference branches and copies.

#### Purpose
- Introduce branch dimension for library locations.
- Create book_copy records per physical copy, linked to branch.
- Update loan and member tables to reference copies and branches.
- Remove per-book copy counters.

#### Type
- SQL migration script (PostgreSQL).

#### Schema
- **branch**
  - `branch_id SMALLSERIAL PRIMARY KEY`
  - `name VARCHAR(100) NOT NULL UNIQUE`
  - `city VARCHAR(100) NOT NULL`
  - Index: `branch_pkey` (implicit PK)
- **book_copy**
  - `copy_id BIGSERIAL PRIMARY KEY`
  - `book_id BIGINT NOT NULL REFERENCES book (book_id)`
  - `branch_id SMALLINT NOT NULL REFERENCES branch (branch_id)`
  - `barcode VARCHAR(32) NOT NULL UNIQUE`
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`
  - Index: `idx_book_copy_book` on `book_id`
- **loan** (altered)
  - Adds `copy_id BIGINT REFERENCES book_copy (copy_id)`
- **member** (altered)
  - Renames `joined_on` → `member_since`
  - Adds `home_branch_id SMALLINT REFERENCES branch (branch_id)`
  - Sets default `home_branch_id = 1` for existing rows
- **book** (altered)
  - Drops constraint `book_free_le_total`
  - Drops columns `copies_free`, `copies_total`

#### Reads
- `book` (SELECT book_id, isbn, copies_free, copies_total)
- `branch` (INSERT default row, SELECT branch_id 1)
- `book_copy` (INSERT via SELECT from book)
- `member` (UPDATE home_branch_id where NULL)

#### Writes
- `branch`: INSERT 1 row (`Main`, `Springfield`)
- `book_copy`: INSERT one row per existing book copy (total rows = sum of `copies_total` across books)
- `loan`: ALTER TABLE adds column `copy_id`
- `member`: ALTER TABLE renames column, adds column, UPDATE sets `home_branch_id = 1` for NULLs
- `book`: ALTER TABLE drops constraint and columns

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Routine logic
- Create `branch` table and insert default branch.
- Create `book_copy` table with foreign keys to `book` and `branch`.
- Populate `book_copy` by generating series for each book’s total copies, assigning barcodes and status based on `copies_free`.
- Alter `loan` to reference `book_copy`.
- Rename `member.joined_on` to `member_since`; add `home_branch_id` and set default to main branch.
- Remove per-book copy counters from `book` and drop related constraint.
- Create index on `book_copy.book_id`.

#### Dependencies
- `book` table (existing)
- `loan` table (existing)
- `member` table (existing)
- `book_copy` references `book` and `branch`
- `loan` references `book_copy`
- `member` references `branch`
