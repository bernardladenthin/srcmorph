### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:21:56Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates lending-library inventory to branch-based copy tracking, assigns existing copies and members to the main branch, and removes book-level copy counters.

#### Purpose
- Add branch and individual-copy inventory with copy status and barcode.
- Link loans to copies and members to home branches; retire `book.copies_free` and `book.copies_total`.

#### Type
- PostgreSQL schema migration

#### Schema
- `branch`: `branch_id SMALLSERIAL` (PK), `name VARCHAR(100)` (NOT NULL, UNIQUE), `city VARCHAR(100)` (NOT NULL).
- `book_copy`: `copy_id BIGSERIAL` (PK), `book_id BIGINT` (NOT NULL, FK), `branch_id SMALLINT` (NOT NULL, FK), `barcode VARCHAR(32)` (NOT NULL, UNIQUE), `status VARCHAR(12)` (NOT NULL, DEFAULT `'AVAILABLE'`, CHECK in `('AVAILABLE', 'ON_LOAN', 'LOST')`).
- `loan`: adds nullable `copy_id BIGINT` (FK to `book_copy.copy_id`).
- `member`: renames `joined_on` to `member_since`; adds nullable `home_branch_id SMALLINT` (FK to `branch.branch_id`), then sets null values to `1`.
- `book`: drops constraint `book_free_le_total` and columns `copies_free`, `copies_total`.
- Index: `idx_book_copy_book` on `book_copy(book_id)`.

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` to generate one `book_copy` row per existing copy.
- `member.home_branch_id` to identify null values for the backfill.

#### Writes
- Inserts `branch` row `('Main', 'Springfield')`.
- Inserts `book_copy` rows with generated barcodes (`isbn || '-' || copy number`); status is `AVAILABLE` when copy number is at most `copies_free`, otherwise `ON_LOAN`.
- Alters `loan`, `member`, and `book` as described in Schema.
- Updates `member.home_branch_id` to `1` where null.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Routine logic
- Runs all migration changes in a transaction (`BEGIN` / `COMMIT).
- Uses `generate_series(1, b.copies_total)` to create copy rows for existing books.

#### Dependencies
- Tables: `book`, `loan`, `member`
- Function: `generate_series`
