### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:16:06Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates the library schema to support multiple branches, introduces a book_copy table, and updates member and loan tables accordingly.

#### Purpose
- Add branch support and per‑copy tracking.
- Replace per‑book copy counters with explicit book_copy rows.
- Update member and loan tables to reference branches and copies.

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
  - Index: `idx_book_copy_book` on `book_id`
- **loan** (altered)
  - Added `copy_id BIGINT REFERENCES book_copy(copy_id)`
- **member** (altered)
  - Renamed `joined_on` → `member_since`
  - Added `home_branch_id SMALLINT REFERENCES branch(branch_id)`
  - Updated existing rows to `home_branch_id = 1`

#### Reads
- `book` (SELECT book_id, copies_total, copies_free, isbn)
- `branch` (INSERT default row)
- `loan` (no reads; only column addition)
- `member` (no reads; only column rename and update)

#### Writes
- `branch` (INSERT new row)
- `book_copy` (INSERT rows for each existing copy)
- `loan` (ADD column)
- `member` (RENAME column, ADD column, UPDATE rows)
- `book` (DROP constraints and columns)

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Existing `book` table (used for generating book_copy rows).
- Existing `loan` and `member` tables (altered).
- `book_copy` index `idx_book_copy_book`.

---
