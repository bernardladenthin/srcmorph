### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:53:10Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, creates a book_copy table linking books to branches, and updates member and loan tables to reference branches and copies.

#### Purpose
- Introduce branch dimension for library locations.
- Normalize book copies per branch.
- Update member and loan records to reference new structures.

#### Type
- SQL migration script (PostgreSQL).

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
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))
- **loan** (altered)
  - `copy_id BIGINT REFERENCES book_copy (copy_id)`
- **member** (altered)
  - `member_since TIMESTAMP` (renamed from `joined_on`)
  - `home_branch_id SMALLINT REFERENCES branch (branch_id)`
- **book** (altered)
  - `copies_free` column removed
  - `copies_total` column removed
  - Constraint `book_free_le_total` dropped

#### Reads
- `book` table: `book_id`, `isbn`, `copies_free`, `copies_total` (in SELECT for book_copy insertion).
- `branch` table: `branch_id` (for default value 1).
- `member` table: `home_branch_id` (for UPDATE).
- `loan` table: none (only added column).

#### Writes
- `branch`: INSERT one row (`Main`, `Springfield`).
- `book_copy`: INSERT one row per existing book copy (count = sum of `copies_total` across all books).
- `member`: UPDATE `home_branch_id` to 1 where NULL.
- `loan`: ALTER TABLE adds column (no data inserted).
- `book`: ALTER TABLE drops columns and constraint.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Routine logic
- Create `branch` table and insert default branch.
- Create `book_copy` table with foreign keys to `book` and `branch`.
- Populate `book_copy` by generating series per book copy, assigning barcode as `isbn-n`, status based on `copies_free`.
- Alter `loan` to reference `book_copy`.
- Rename `member.joined_on` to `member_since`; add `home_branch_id` and set default to main branch.
- Remove per-book copy counters from `book` and drop related constraint.
- Create index `idx_book_copy_book` on `book_copy(book_id)`.

#### Dependencies
- `book` table (existing).
- `loan` table (existing).
- `member` table (existing).
