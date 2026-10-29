### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:14:25Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, creates a book_copy table linking books to branches, and updates member and loan tables to reference branches and copies.

#### Purpose
- Introduce branch dimension for library locations.
- Replace per-book copy counters with explicit book_copy rows.
- Link loans and members to specific copies and branches.

#### Type
- SQL migration script (PostgreSQL).

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
- **loan** (altered)
  - `copy_id BIGINT REFERENCES book_copy(copy_id)`
- **member** (altered)
  - `member_since TIMESTAMP` (renamed from joined_on)
  - `home_branch_id SMALLINT REFERENCES branch(branch_id)`

#### Reads
- `book` table: `book_id`, `isbn`, `copies_free`, `copies_total` (for copy generation).
- `branch` table: `branch_id` (default value 1 used).
- `member` table: `home_branch_id` (to set default).

#### Writes
- Inserts into `branch` (initial row).
- Inserts into `book_copy` (one row per existing copy).
- Updates `member` to set `home_branch_id`.
- Alters `loan` and `member` tables (adds columns, renames, drops).
- Drops constraints and columns from `book`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Routine logic
- Create `branch` table and seed with 'Main' branch.
- Create `book_copy` table with foreign keys to `book` and `branch`.
- Populate `book_copy` by cross‑joining each book with a series from 1 to `copies_total`; barcode formed as `isbn-n`; status set to AVAILABLE if `n <= copies_free` else ON_LOAN.
- Add `copy_id` column to `loan`.
- Rename `joined_on` to `member_since` in `member`.
- Add `home_branch_id` to `member` and set default to 1 where null.
- Remove copy counter columns and constraint from `book`.
- Create index on `book_copy(book_id)`.

#### Dependencies
- Existing tables: `book`, `loan`, `member`.
- Existing constraint: `book_free_le_total` on `book`.
- Sequence: implicit `branch_id` and `copy_id` serials.
