### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:08:19Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library schema migration v2: introduces branch and book_copy tables to track physical copies per location, links loans to specific copies, renames member join date, and removes legacy per-book copy counters.

#### Purpose
- Migrate the lending library schema from a book-level copy count model to a granular per-copy model with branch assignment.
- Backfill existing book copies into the new `book_copy` table and update `loan` and `member` tables to reference the new structures.

#### Type
- SQL migration script (PostgreSQL dialect) containing DDL and DML statements within a transaction.

#### Schema
- **branch**
    - `branch_id SMALLSERIAL` PRIMARY KEY
    - `name VARCHAR(100)` NOT NULL UNIQUE
    - `city VARCHAR(100)` NOT NULL
- **book_copy**
    - `copy_id BIGSERIAL` PRIMARY KEY
    - `book_id BIGINT` NOT NULL, FOREIGN KEY -> `book.book_id`
    - `branch_id SMALLINT` NOT NULL, FOREIGN KEY -> `branch.branch_id`
    - `barcode VARCHAR(32)` NOT NULL UNIQUE
    - `status VARCHAR(12)` NOT NULL DEFAULT 'AVAILABLE', CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))
- **loan** (altered)
    - Added `copy_id BIGINT`, FOREIGN KEY -> `book_copy.copy_id`
- **member** (altered)
    - Renamed `joined_on` to `member_since`
    - Added `home_branch_id SMALLINT`, FOREIGN KEY -> `branch.branch_id`
- **book** (altered)
    - Dropped constraint `book_free_le_total`
    - Dropped column `copies_free`
    - Dropped column `copies_total`
- **Index**
    - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book`: `book_id`, `isbn`, `copies_free`, `copies_total` (used in backfill INSERT)
- `member`: `home_branch_id` (used in UPDATE backfill)

#### Writes
- `branch`: INSERT initial row ('Main', 'Springfield')
- `book_copy`: INSERT backfilled rows from `book`
- `loan`: ALTER TABLE ADD COLUMN `copy_id`
- `member`: ALTER TABLE RENAME COLUMN `joined_on` -> `member_since`; ALTER TABLE ADD COLUMN `home_branch_id`; UPDATE `home_branch_id`
- `book`: ALTER TABLE DROP CONSTRAINT, DROP COLUMN `copies_free`, DROP COLUMN `copies_total`

#### Relationships
- `book_copy.book_id` -> `book.book_id`
- `book_copy.branch_id` -> `branch.branch_id`
- `loan.copy_id` -> `book_copy.copy_id`
- `member.home_branch_id` -> `branch.branch_id`

#### Routine logic
- Wrapped in `BEGIN` / `COMMIT` transaction.
- Creates `branch` table and seeds default branch.
- Creates `book_copy` table with status constraints.
- Backfills `book_copy` by cross-joining `book` with `generate_series(1, copies_total)`, assigning barcodes as `isbn-n` and status based on `copies_free`.
- Adds `copy_id` to `loan` table.
- Renames `member.joined_on` to `member_since` and adds `home_branch_id`, backfilling to branch 1.
- Removes legacy copy counter columns and constraint from `book`.
- Creates index on `book_copy.book_id`.

#### Dependencies
- `book` (existing table, referenced for backfill and FK)
- `branch` (new table, referenced by `book_copy` and `member`)
- `book_copy` (new table, referenced by `loan`)
- `loan` (existing table, altered)
- `member` (existing table, altered)
- `generate_series` (PostgreSQL built-in function)
