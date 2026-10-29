### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:58:37Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library schema migration v2: introduces branch-level inventory tracking by adding a `branch` table and a `book_copy` table, migrating existing book copies to the main branch, linking loans to specific copies, and retiring per-book aggregate copy counters.

#### Purpose
- Migrate the lending library schema from a single-location model to a multi-branch model.
- Replace aggregate `copies_total` / `copies_free` columns on `book` with granular `book_copy` rows per branch.

#### Type
- SQL migration script (PostgreSQL dialect) containing DDL, DML, and DDL alterations within a transaction.

#### Schema
**Table: `branch`**
- `branch_id SMALLSERIAL` — PRIMARY KEY
- `name VARCHAR(100)` — NOT NULL, UNIQUE
- `city VARCHAR(100)` — NOT NULL

**Table: `book_copy`**
- `copy_id BIGSERIAL` — PRIMARY KEY
- `book_id BIGINT` — NOT NULL, FOREIGN KEY → `book(book_id)`
- `branch_id SMALLINT` — NOT NULL, FOREIGN KEY → `branch(branch_id)`
- `barcode VARCHAR(32)` — NOT NULL, UNIQUE
- `status VARCHAR(12)` — NOT NULL, DEFAULT `'AVAILABLE'`, CHECK (`status IN ('AVAILABLE', 'ON_LOAN', 'LOST')`)

**Altered Table: `loan`**
- Added column: `copy_id BIGINT` — FOREIGN KEY → `book_copy(copy_id)`

**Altered Table: `member`**
- Renamed column: `joined_on` → `member_since`
- Added column: `home_branch_id SMALLINT` — FOREIGN KEY → `branch(branch_id)`

**Altered Table: `book`**
- Dropped constraint: `book_free_le_total`
- Dropped column: `copies_free`
- Dropped column: `copies_total`

**Index**
- `idx_book_copy_book` on `book_copy(book_id)`

#### Reads
- `book` table: `book_id`, `isbn`, `copies_free`, `copies_total` (used in the `INSERT INTO book_copy` migration query)
- `generate_series` (PostgreSQL function) for row generation

#### Writes
- `branch`: INSERT initial row `('Main', 'Springfield')`
- `book_copy`: INSERT rows generated from `book` data
- `loan`: ALTER TABLE ADD COLUMN `copy_id`
- `member`: ALTER TABLE RENAME COLUMN, ADD COLUMN `home_branch_id`, UPDATE `home_branch_id = 1`
- `book`: ALTER TABLE DROP CONSTRAINT, DROP COLUMN `copies_free`, DROP COLUMN `copies_total`

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book` (pre-existing table, read and altered)
- `loan` (pre-existing table, altered)
- `member` (pre-existing table, altered)
- `generate_series` (PostgreSQL built-in function)
