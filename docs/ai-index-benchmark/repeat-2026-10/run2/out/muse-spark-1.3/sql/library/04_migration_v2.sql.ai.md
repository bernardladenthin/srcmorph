### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:17:02Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library branch expansion migrating aggregate book copy counts into per-branch individual copy inventory.
#### Purpose
- Add `branch` library locations and per-branch `book_copy` inventory
- Retire aggregate `book.copies_total` / `copies_free` counters in favor of individual copy `status`
#### Type
- table, index migration script, PostgreSQL dialect
#### Schema
- `branch`: `branch_id SMALLSERIAL` PK, `name VARCHAR(100)` NOT NULL UNIQUE, `city VARCHAR(100)` NOT NULL
- `book_copy`: `copy_id BIGSERIAL` PK, `book_id BIGINT` NOT NULL FK, `branch_id SMALLINT` NOT NULL FK, `barcode VARCHAR(32)` NOT NULL UNIQUE, `status VARCHAR(12)` NOT NULL DEFAULT 'AVAILABLE' CHECK IN ('AVAILABLE','ON_LOAN','LOST')
- `loan` altered: ADD `copy_id BIGINT` REFERENCES `book_copy(copy_id)`
- `member` altered: RENAME `joined_on` TO `member_since`, ADD `home_branch_id SMALLINT` REFERENCES `branch(branch_id)`
- `book` altered: DROP CONSTRAINT `book_free_le_total`, DROP COLUMN `copies_free`, DROP COLUMN `copies_total`
- Index `idx_book_copy_book` ON `book_copy(book_id)`
#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` via `SELECT` + `CROSS JOIN LATERAL generate_series(1, copies_total)`
- `member.home_branch_id` via `UPDATE member WHERE home_branch_id IS NULL`
#### Writes
- `INSERT INTO branch(name, city)` VALUES ('Main','Springfield')
- `INSERT INTO book_copy(book_id, branch_id, barcode, status)` with `branch_id=1`, `barcode=isbn-n`, `status=AVAILABLE/ON_LOAN`
- `UPDATE member SET home_branch_id=1`
- DDL writes: `CREATE TABLE branch`, `CREATE TABLE book_copy`, `ALTER TABLE loan`, `ALTER TABLE member`, `ALTER TABLE book`, `CREATE INDEX idx_book_copy_book`
#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`
#### Dependencies
- tables `book`, `branch`, `book_copy`, `loan`, `member`
- function `generate_series`
- constraint `book_free_le_total`
- index `idx_book_copy_book`
