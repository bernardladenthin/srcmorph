### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:23:21Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library branch expansion replacing per-book copy counters with individual per-branch book copies and copy-level loans.

#### Purpose
- PostgreSQL schema migration v2 for lending library branching.
- Retires `book.copies_total` / `copies_free` counters in favor of `book_copy` rows per `branch`.

#### Type
- DDL/DML migration script, PostgreSQL dialect: `TABLE`, `INDEX`, `ALTER TABLE`, `INSERT ... SELECT`, `UPDATE`.

#### Schema
- `branch`: `branch_id SMALLSERIAL PRIMARY KEY`, `name VARCHAR(100) NOT NULL UNIQUE`, `city VARCHAR(100) NOT NULL`.
- `book_copy`: `copy_id BIGSERIAL PRIMARY KEY`, `book_id BIGINT NOT NULL REFERENCES book`, `branch_id SMALLINT NOT NULL REFERENCES branch`, `barcode VARCHAR(32) NOT NULL UNIQUE`, `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK(status IN ('AVAILABLE','ON_LOAN','LOST'))`.
- `idx_book_copy_book` index on `book_copy(book_id)`.
- `loan` altered: `ADD COLUMN copy_id BIGINT REFERENCES book_copy(copy_id)`.
- `member` altered: `RENAME COLUMN joined_on TO member_since`, `ADD COLUMN home_branch_id SMALLINT REFERENCES branch(branch_id)`.
- `book` altered: `DROP CONSTRAINT book_free_le_total`, `DROP COLUMN copies_free`, `DROP COLUMN copies_total`.

#### Reads
- `book.book_id, book.isbn, book.copies_total, book.copies_free` via `FROM book b CROSS JOIN LATERAL generate_series(1, b.copies_total)`.
- `member.home_branch_id` in `UPDATE member ... WHERE home_branch_id IS NULL`.

#### Writes
- `branch(name, city)`: `INSERT ('Main','Springfield')` with `branch_id` defaulting to `1`.
- `book_copy(book_id, branch_id, barcode, status)`: `INSERT ... SELECT`, `branch_id=1`, `barcode = isbn || '-' || n`, `status = AVAILABLE/ON_LOAN`.
- `member.home_branch_id`: `UPDATE SET home_branch_id=1 WHERE IS NULL`.
- `loan.copy_id`, `member.member_since`, `member.home_branch_id`: schema `ADD COLUMN` / `RENAME COLUMN`.
- `book.copies_free, book.copies_total`: schema `DROP COLUMN`.

#### Relationships
- `book_copy.book_id -> book.book_id`.
- `book_copy.branch_id -> branch.branch_id`.
- `loan.copy_id -> book_copy.copy_id`.
- `member.home_branch_id -> branch.branch_id`.

#### Dependencies
- Tables `book`, `branch`, `book_copy`, `loan`, `member`.
- Function `generate_series`.
- Constraint `book_free_le_total`, index `idx_book_copy_book`.
