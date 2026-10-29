### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:46:24Z
- G: 
- A: 0.0.0
- X: file
---
> This migration updates the lending library schema to support branch-based copy inventory and member home branches in PostgreSQL.

#### Purpose
- Adds `branch` records and per-branch `book_copy` inventory to model copies across library branches.
- Migrates existing copy data, renames `member.joined_on` to `member.member_since`, and removes redundant per-book copy counters.

#### Type
- SQL object kind: PostgreSQL migration script.
- DDL/DML operations: `CREATE TABLE`, `INSERT INTO`, `ALTER TABLE`, `UPDATE`, `DROP COLUMN`, `CREATE INDEX`, transaction `BEGIN`/`COMMIT`.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` PK
  - `name VARCHAR(100) NOT NULL UNIQUE`
  - `city VARCHAR(100) NOT NULL`
  - Indexes: none declared beyond PK.
- `book_copy`
  - `copy_id BIGSERIAL` PK
  - `book_id BIGINT NOT NULL` FK -> `book(book_id)`
  - `branch_id SMALLINT NOT NULL` FK -> `branch(branch_id)`
  - `barcode VARCHAR(32) NOT NULL UNIQUE`
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`
  - Indexes: `idx_book_copy_book` on `book_copy(book_id)`
- `loan`
  - `copy_id BIGINT` FK -> `book_copy(copy_id)` added by `ALTER TABLE loan ADD COLUMN copy_id BIGINT REFERENCES book_copy (copy_id);`
- `member`
  - `joined_on` renamed to `member_since` by `ALTER TABLE member RENAME COLUMN joined_on TO member_since;`
  - `home_branch_id SMALLINT` FK -> `branch(branch_id)` added by `ALTER TABLE member ADD COLUMN home_branch_id SMALLINT REFERENCES branch (branch_id);`
  - `UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL;`
- `book`
  - `book_free_le_total` constraint dropped
  - `copies_free` column dropped
  - `copies_total` column dropped

#### Reads
- `book` read in `INSERT INTO book_copy ... SELECT ... FROM book b ...`
  - `book.book_id`
  - `book.isbn`
  - `book.copies_total`
  - `book.copies_free`
- `member` read in `UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL;`
  - `member.home_branch_id`
- `generate_series(1, b.copies_total)` used as a source via `CROSS JOIN LATERAL`

#### Writes
- `branch`
  - `INSERT INTO branch (name, city) VALUES ('Main', 'Springfield');`
- `book_copy`
  - `INSERT INTO book_copy (book_id, branch_id, barcode, status) SELECT ...`
  - `status` set to `'AVAILABLE'` when `g.n <= b.copies_free`, else `'ON_LOAN'`
- `loan`
  - `ALTER TABLE loan ADD COLUMN copy_id BIGINT REFERENCES book_copy (copy_id);`
- `member`
  - `ALTER TABLE member RENAME COLUMN joined_on TO member_since;`
  - `ALTER TABLE member ADD COLUMN home_branch_id SMALLINT REFERENCES branch (branch_id);`
  - `UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL;`
- `book`
  - `ALTER TABLE book DROP CONSTRAINT book_free_le_total;`
  - `ALTER TABLE book DROP COLUMN copies_free;`
  - `ALTER TABLE book DROP COLUMN copies_total;`
- `book_copy`
  - `CREATE INDEX idx_book_copy_book ON book_copy (book_id);`

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`
- `book_copy` rows are populated from `book` via:
  - `book.book_id -> book_copy.book_id`
  - `book.isbn || '-' || g.n -> book_copy.barcode`
  - `1 -> book_copy.branch_id`

#### Dependencies
- `branch`
- `book`
- `book_copy`
- `loan`
- `member`
- `generate_series`
- `book_free_le_total` constraint
- `idx_book_copy_book`
- `book.isbn`
- `book.copies_total`
- `book.copies_free`
- `book.book_id`
- `branch.branch_id`
- `book_copy.copy_id`
- `book_copy.book_id`
- `book_copy.branch_id`
- `member.home_branch_id`
- `member.member_since`
