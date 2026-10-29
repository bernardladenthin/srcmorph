### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T22:43:47Z
- G: 
- A: 0.0.0
- X: file
---
> Lending-library schema migration that introduces per-branch book copies, links loans to specific copies, assigns members a home branch, and retires the old per-book copy counters.

#### Purpose
- Migrate the lending library schema from a single-location model (copies tracked as counters on `book`) to a multi-branch model with explicit `book_copy` rows.
- Backfill existing copies into the new `book_copy` table and wire `loan` and `member` to the new structure.

#### Type
- Migration script (DDL + DML), PostgreSQL dialect.

#### Schema

**branch**
- `branch_id SMALLSERIAL` — PK
- `name VARCHAR(100)` — NOT NULL, UNIQUE
- `city VARCHAR(100)` — NOT NULL

**book_copy**
- `copy_id BIGSERIAL` — PK
- `book_id BIGINT` — NOT NULL, FK → `book(book_id)`
- `branch_id SMALLINT` — NOT NULL, FK → `branch(branch_id)`
- `barcode VARCHAR(32)` — NOT NULL, UNIQUE
- `status VARCHAR(12)` — NOT NULL, DEFAULT `'AVAILABLE'`, CHECK IN (`'AVAILABLE'`, `'ON_LOAN'`, `'LOST'`)

**loan** (altered)
- `copy_id BIGINT` — added, FK → `book_copy(copy_id)`

**member** (altered)
- `joined_on` → renamed to `member_since`
- `home_branch_id SMALLINT` — added, FK → `branch(branch_id)`

**book** (altered)
- `copies_free` — dropped
- `copies_total` — dropped
- constraint `book_free_le_total` — dropped

#### Reads
- `book` — `book_id`, `isbn`, `copies_free`, `copies_total` (backfill query)

#### Writes
- `branch` — INSERT seed row
- `book_copy` — INSERT backfill from `book`
- `loan` — ALTER ADD COLUMN `copy_id`
- `member` — ALTER RENAME COLUMN, ALTER ADD COLUMN `home_branch_id`, UPDATE `home_branch_id`
- `book` — ALTER DROP CONSTRAINT, ALTER DROP COLUMN ×2

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book` (pre-existing table, read and altered)
- `loan` (pre-existing table, altered)
- `member` (pre-existing table, altered)
- `branch` (created in this script)
- `book_copy` (created in this script)
- `idx_book_copy_book` — index on `book_copy(book_id)`
