### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:32:53Z
- G: 
- A: 0.0.0
- X: file
---
> This migration adds branch and book_copy tables to move inventory from per-book counters to per-copy rows and retires the old free/total copy columns.

#### Type
- Migration script, PostgreSQL dialect.
- Uses transactional DDL (`BEGIN` / `COMMIT`).

#### Schema
- `branch` table:
  - `branch_id SMALLSERIAL PRIMARY KEY`
  - `name VARCHAR(100) NOT NULL UNIQUE`
  - `city VARCHAR(100) NOT NULL`
- `book_copy` table:
  - `copy_id BIGSERIAL PRIMARY KEY`
  - `book_id BIGINT NOT NULL REFERENCES book (book_id)`
  - `branch_id SMALLINT NOT NULL REFERENCES branch (branch_id)`
  - `barcode VARCHAR(32) NOT NULL UNIQUE`
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`
  - `idx_book_copy_book` index on `book_id`
- `loan` table:
  - adds `copy_id BIGINT REFERENCES book_copy (copy_id)`
- `member` table:
  - renames `joined_on` to `member_since`
  - adds `home_branch_id SMALLINT REFERENCES branch (branch_id)`
- `book` table:
  - drops `copies_free`
  - drops `copies_total`
  - drops constraint `book_free_le_total`

#### Reads
- `book` columns `book_id`, `isbn`, `copies_free`, `copies_total` in the `INSERT ... SELECT` backfill.
- `generate_series(1, b.copies_total)` via `CROSS JOIN LATERAL`.

#### Writes
- `INSERT INTO branch` seeds main branch `('Main', 'Springfield')`.
- `INSERT INTO book_copy` backfills one row per existing book copy.
- `ALTER TABLE loan ADD COLUMN copy_id`.
- `ALTER TABLE member ADD COLUMN home_branch_id` and `UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL`.
- `ALTER TABLE member RENAME COLUMN joined_on TO member_since`.
- `ALTER TABLE book DROP CONSTRAINT book_free_le_total`, `DROP COLUMN copies_free`, `DROP COLUMN copies_total`.
- `CREATE INDEX idx_book_copy_book` on `book_copy`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Routine logic
- Backfill logic: for each book, generate rows `1..copies_total`; barcode = `isbn || '-' || g.n`; status = `'AVAILABLE'` when `g.n <= copies_free`, else `'ON_LOAN'`; all copies assigned to branch id `1` (Main).
- Backfill sets `home_branch_id = 1` for members with NULL.

#### Dependencies
- Tables created: `branch`, `book_copy`.
- Tables altered: `loan`, `member`, `book`.
- Tables read: `book`.
- Constraint dropped: `book_free_le_total`.
- Index created: `idx_book_copy_book`.
