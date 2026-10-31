### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:50:26Z
- G: 
- A: 0.0.0
- X: file
---
> Models physical library copies per branch, links each loan to a specific copy, and replaces per-book aggregate copy counters with branch-scoped copy inventory.

#### Purpose
- Adds `branch` table and `book_copy` table to track individual copies at specific branches.
- Materializes existing `book.copies_total` / `book.copies_free` into concrete `book_copy` rows, links `loan` to `book_copy`, and retires the per-book copy counters.

#### Type
PostgreSQL DDL/DML migration script (tables, indexes, ALTER TABLE, DML).

#### Schema
branch
- branch_id SMALLSERIAL PRIMARY KEY
- name VARCHAR(100) NOT NULL UNIQUE
- city VARCHAR(100) NOT NULL

book_copy
- copy_id BIGSERIAL PRIMARY KEY
- book_id BIGINT NOT NULL REFERENCES book (book_id)
- branch_id SMALLINT NOT NULL REFERENCES branch (branch_id)
- barcode VARCHAR(32) NOT NULL UNIQUE
- status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))

Index
- idx_book_copy_book ON book_copy (book_id)

#### Reads
- book.book_id, book.copies_total, book.copies_free, book.isbn (source for book_copy row generation)
- branch.branch_id (referenced by book_copy and member.home_branch_id)

#### Writes
- branch (INSERT name, city)
- book_copy (INSERT book_id, branch_id, barcode, status)
- loan (ADD COLUMN copy_id BIGINT REFERENCES book_copy (copy_id))
- member (RENAME COLUMN joined_on TO member_since; ADD COLUMN home_branch_id SMALLINT REFERENCES branch (branch_id); UPDATE home_branch_id = 1 WHERE home_branch_id IS NULL)
- book (DROP CONSTRAINT book_free_le_total; DROP COLUMN copies_free; DROP COLUMN copies_total)

#### Relationships
- book_copy.book_id -> book.book_id
- book_copy.branch_id -> branch.branch_id
- loan.copy_id -> book_copy.copy_id
- member.home_branch_id -> branch.branch_id

#### Dependencies
- book
- loan
- member
- branch
- book_copy
