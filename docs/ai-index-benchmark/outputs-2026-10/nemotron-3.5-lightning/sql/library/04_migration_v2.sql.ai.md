### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T23:06:43Z
- G: 
- A: 0.0.0
- X: file
---
> This migration models a library system's branch structure, copy distribution, and member-branch association while retiring legacy per-book copy counters.

#### Purpose
- Creates branch infrastructure and redistributes book copies per branch, retiring global copy counters.
- Migrates copy ownership to branch-level tracking with new columns, references, and index.

#### Type
PostgreSQL migration (DDL/DML).

#### Schema
`branch`: `branch_id SMALLSERIAL PRIMARY KEY`, `name VARCHAR(100) NOT NULL UNIQUE`, `city VARCHAR(100) NOT NULL`. Primary key: `branch_id`. Unique constraint on `name`.
`book_copy`: `copy_id BIGSERIAL PRIMARY KEY`, `book_id BIGINT NOT NULL REFERENCES book (book_id)`, `branch_id SMALLINT NOT NULL REFERENCES branch (branch_id)`, `barcode VARCHAR(32) NOT NULL UNIQUE`, `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`. Primary key: `copy_id`. Foreign keys: `book_id -> book.book_id`, `branch_id -> branch.branch_id`. Unique on `barcode`. Check on `status`. Default on `status`. Index on `book_id` (created later in file).

#### Reads
- `INSERT INTO book_copy ... SELECT ... FROM book b CROSS JOIN LATERAL generate_series(1, b.copies_total) AS g(n)` reads `book` columns `book_id`, `copies_total`; reads `generate_series`.
- `ALTER TABLE loan ADD COLUMN copy_id BIGINT REFERENCES book_copy (copy_id)` reads `book_copy` column `copy_id` for FK definition.
- `ALTER TABLE member ADD COLUMN home_branch_id SMALLINT REFERENCES branch (branch_id)` reads `branch` column `branch_id`.
- `UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL` reads `member` column `home_branch_id`.

#### Writes
- `CREATE TABLE branch` writes `branch` columns `branch_id`, `name`, `city`.
- `CREATE TABLE book_copy` writes `book_copy` columns `copy_id`, `book_id`, `branch_id`, `barcode`, `status`.
- `INSERT INTO branch (name, city) VALUES ('Main', 'Springfield')` writes `branch` row.
- `INSERT INTO book_copy ...` writes `book_copy` rows from `book` and `generate_series`.
- `ALTER TABLE loan ADD COLUMN copy_id` adds column to `loan`.
- `ALTER TABLE member RENAME COLUMN joined_on TO member_since` renames column in `member`.
- `ALTER TABLE member ADD COLUMN home_branch_id` adds column to `member`.
- `UPDATE member SET home_branch_id = 1` updates `member` row.
- `ALTER TABLE book DROP CONSTRAINT book_free_le_total, DROP COLUMN copies_free, DROP COLUMN copies_total` modifies `book`.
- `CREATE INDEX idx_book_copy_book ON book_copy (book_id)` creates index on `book_copy`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book` (table, referenced by `book_copy.book_id`, used in `INSERT ... SELECT`)
- `branch` (table, referenced by `book_copy.branch_id`, `member.home_branch_id`)
- `generate_series` (function, used in `CROSS JOIN LATERAL`)
- Sequences: `branch_branch_id_seq` (implied by SMALLSERIAL), `book_copy_copy_id_seq` (implied by BIGSERIAL)
