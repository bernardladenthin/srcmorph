### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:35:47Z
- G: 
- A: 0.0.0
- X: file
---
> PostgreSQL migration v2 for a lending library: adds branch and book_copy tables, moves per-book copy counts into per-copy rows, links loans to copies, and retires the book copy counters.

#### Purpose
- Introduces per-branch inventory: `branch` table and per-physical-copy `book_copy` rows.
- Replaces aggregate `book.copies_total` / `book.copies_free` counters with copy-level status, and attaches `loan` to a specific `book_copy`.

#### Type
- SQL migration script (DDL + DML), PostgreSQL dialect (`SMALLSERIAL`, `BIGSERIAL`, `LATERAL`, `generate_series`), wrapped in a single `BEGIN` / `COMMIT` transaction.

#### Schema
- `branch` (created)
  - `branch_id SMALLSERIAL` PK
  - `name VARCHAR(100)` NOT NULL, UNIQUE
  - `city VARCHAR(100)` NOT NULL
- `book_copy` (created)
  - `copy_id BIGSERIAL` PK
  - `book_id BIGINT` NOT NULL, FK -> `book.book_id`
  - `branch_id SMALLINT` NOT NULL, FK -> `branch.branch_id`
  - `barcode VARCHAR(32)` NOT NULL, UNIQUE
  - `status VARCHAR(12)` NOT NULL, DEFAULT `'AVAILABLE'`, CHECK in (`AVAILABLE`, `ON_LOAN`, `LOST`)
- `loan` (altered)
  - added `copy_id BIGINT` FK -> `book_copy.copy_id`
- `member` (altered)
  - renamed `joined_on` -> `member_since`
  - added `home_branch_id SMALLINT` FK -> `branch.branch_id`
- `book` (altered)
  - dropped constraint `book_free_le_total`
  - dropped columns `copies_free`, `copies_total`
- Indexes
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` (backfill source for `book_copy`)
- `generate_series(1, book.copies_total)` via CROSS JOIN LATERAL
- `member.home_branch_id` (IS NULL filter in UPDATE)

#### Writes
- `branch` INSERT: row (`Main`, `Springfield`)
- `book_copy` INSERT from `book`: `book_id`, `branch_id = 1`, `barcode = isbn || '-' || n`, `status` = AVAILABLE if n <= copies_free else ON_LOAN
- `member.home_branch_id` UPDATE to 1 where NULL
- `loan.copy_id` ADD COLUMN (no backfill in this script)
- `member.joined_on` renamed to `member_since`
- `book` DROP CONSTRAINT `book_free_le_total`, DROP COLUMN `copies_free`, DROP COLUMN `copies_total`

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`
- `book_copy.book_id` derived from `book.book_id` (backfill join)

#### Dependencies
- Tables: `branch`, `book_copy`, `book`, `loan`, `member`
- Constraints: `book_free_le_total`
- Columns: `book.isbn`, `book.copies_total`, `book.copies_free`, `member.joined_on`, `member.member_since`, `member.home_branch_id`, `loan.copy_id`
- Functions: `generate_series`
- Index: `idx_book_copy_book`
