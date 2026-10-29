### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T08:53:33Z
- G: 
- A: 0.0.0
- X: file
---
> This SQL migration file models a library system's branch structure and copy management, adding branches, redistributing copies per branch, retiring per-book counters, and updating member and loan references.

#### Purpose
- Creates the `branch` table and populates it with a default "Main" branch.
- Migrates `book_copy` data to associate each copy with a branch, replacing global copy counters.
- Renames `member.joined_on` to `member_since` and adds a `home_branch_id` foreign key.
- Removes `copies_free` and `copies_total` columns from `book` and adds an index on `book_copy.book_id`.

#### Type
PostgreSQL migration script (DDL/DML)

#### Schema

**branch**
- `branch_id` SMALLSERIAL PRIMARY KEY
- `name` VARCHAR(100) NOT NULL UNIQUE
- `city` VARCHAR(100) NOT NULL

**book_copy**
- `copy_id` BIGSERIAL PRIMARY KEY
- `book_id` BIGINT NOT NULL REFERENCES book (book_id)
- `branch_id` SMALLINT NOT NULL REFERENCES branch (branch_id)
- `barcode` VARCHAR(32) NOT NULL UNIQUE
- `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))

**book** (altered)
- Dropped columns: `copies_free`, `copies_total`
- Dropped constraint: `book_free_le_total`

**member** (altered)
- Renamed column: `joined_on` → `member_since`
- Added column: `home_branch_id` SMALLINT REFERENCES branch (branch_id)

**Indexes**
- `idx_book_copy_book` on book_copy (book_id)

#### Reads
- `book` (via `book_id` in INSERT ... SELECT)
- `branch` (via `branch_id` in INSERT ... SELECT and REFERENCES clauses)
- `book_copy` (via `copy_id` REFERENCES in ALTER TABLE loan)

#### Writes
- `branch` (INSERT)
- `book_copy` (INSERT ... SELECT)
- `loan` (ADD COLUMN copy_id, ADD FOREIGN KEY REFERENCES book_copy)
- `member` (RENAME COLUMN, ADD COLUMN home_branch_id, UPDATE home_branch_id = 1)
- `book` (DROP CONSTRAINT, DROP COLUMN copies_free, DROP COLUMN copies_total)
- `book_copy` (CREATE INDEX idx_book_copy_book)

#### Relationships
- `book_copy.book_id` → `book.book_id` (FK REFERENCES)
- `book_copy.branch_id` → `branch.branch_id` (FK REFERENCES)
- `loan.copy_id` → `book_copy.copy_id` (FK REFERENCES, added via ALTER TABLE)
- `member.home_branch_id` → `branch.branch_id` (FK REFERENCES, added via ALTER TABLE)

#### Routine logic
- Inserts a default branch row ('Main', 'Springfield').
- Populates `book_copy` by crossing each book with a generate_series up to `copies_total`, assigning barcodes via `isbn || '-' || n` and status based on `copies_free`.
- Adds `copy_id` to `loan` referencing `book_copy`.
- Renames `member.joined_on` to `member_since`.
- Adds `member.home_branch_id` with a default value of 1 for existing rows.
- Removes `book.copies_free` and `book.copies_total` columns and their constraint.
- Creates index `idx_book_copy_book` on `book_copy(book_id)`.

#### Dependencies
- `book` (referenced by `book_copy.book_id` FK, and columns dropped)
- `branch` (referenced by `book_copy.branch_id` FK, `member.home_branch_id` FK, and INSERT)
- `loan` (ALTER TABLE to add copy_id FK)
- `member` (RENAME COLUMN, ADD COLUMN, UPDATE)
- `generate_series` (LATERAL function used in INSERT)
