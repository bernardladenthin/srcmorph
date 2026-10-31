### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:16:25Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library branch expansion migrating per-book copy counts into per-branch individual book copies with loan linkage.

#### Purpose
- Schema migration v2 for lending library: introduce library branches and per-branch physical copies.
- Retire aggregate `book.copies_total` / `copies_free` counters in favor of `book_copy` rows and rename `member.joined_on` to `member_since`.

#### Type
- Migration script with table, index, column alterations, PostgreSQL dialect.

#### Schema
- `branch` table: `branch_id SMALLSERIAL` PK, `name VARCHAR(100)` NOT NULL UNIQUE, `city VARCHAR(100)` NOT NULL.
- `book_copy` table: `copy_id BIGSERIAL` PK, `book_id BIGINT` NOT NULL FK, `branch_id SMALLINT` NOT NULL FK, `barcode VARCHAR(32)` NOT NULL UNIQUE, `status VARCHAR(12)` NOT NULL DEFAULT 'AVAILABLE' CHECK IN ('AVAILABLE','ON_LOAN','LOST').
- Altered `loan`: added `copy_id BIGINT` FK -> `book_copy.copy_id`.
- Altered `member`: renamed `joined_on` to `member_since`, added `home_branch_id SMALLINT` FK -> `branch.branch_id`.
- Altered `book`: dropped constraint `book_free_le_total`, dropped columns `copies_free`, `copies_total`.
- Index `idx_book_copy_book` on `book_copy(book_id)`.

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` via SELECT + CROSS JOIN LATERAL for copy generation.
- `generate_series(1, b.copies_total)` as `g(n)` for per-copy row expansion.
- `member.home_branch_id` in UPDATE WHERE IS NULL filter.

#### Writes
- `branch.name`, `branch.city` via INSERT ('Main','Springfield').
- `book_copy.book_id`, `book_copy.branch_id`, `book_copy.barcode`, `book_copy.status` via INSERT SELECT.
- `member.home_branch_id` via UPDATE SET to 1 where NULL.
- DDL writes: CREATE TABLE `branch`, `book_copy`; ALTER TABLE `loan`, `member`, `book`; CREATE INDEX `idx_book_copy_book`.

#### Relationships
- `book_copy.book_id` -> `book.book_id`.
- `book_copy.branch_id` -> `branch.branch_id`.
- `loan.copy_id` -> `book_copy.copy_id`.
- `member.home_branch_id` -> `branch.branch_id`.

#### Dependencies
- Tables `book`, `branch`, `book_copy`, `loan`, `member`.
- Function `generate_series`, constraint `book_free_le_total`, index `idx_book_copy_book`.
