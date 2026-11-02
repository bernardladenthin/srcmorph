### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:36:57Z
- G: 
- A: 0.0.0
- X: file
---
> Adds branch-based inventory to the lending library, assigns existing book copies to the main branch, and updates loans and members for branch-aware copy tracking.

#### Purpose
- Introduces library branches and per-copy inventory with barcodes and availability status.
- Migrates existing copy counts into `book_copy`, links loans and members to copies and branches, and removes book-level copy counters.

#### Type
- PostgreSQL schema migration; transaction-wrapped DDL and DML.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key (unnamed constraint).
  - `name VARCHAR(100)` — NOT NULL, UNIQUE (unnamed constraint).
  - `city VARCHAR(100)` — NOT NULL.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key (unnamed constraint).
  - `book_id BIGINT` — NOT NULL, FK to `book(book_id)` (unnamed constraint).
  - `branch_id SMALLINT` — NOT NULL, FK to `branch(branch_id)` (unnamed constraint).
  - `barcode VARCHAR(32)` — NOT NULL, UNIQUE (unnamed constraint).
  - `status VARCHAR(12)` — NOT NULL, DEFAULT `'AVAILABLE'`, CHECK in `('AVAILABLE', 'ON_LOAN', 'LOST')` (unnamed constraint).
- `loan`: adds `copy_id BIGINT`, FK to `book_copy(copy_id)` (unnamed constraint).
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT`, FK to `branch(branch_id)` (unnamed constraint).
- `book`: drops constraint `book_free_le_total`; drops columns `copies_free` and `copies_total`.
- Index `idx_book_copy_book` on `book_copy(book_id)`; default index method.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` read by the copy-migration SELECT.
- `member`: `home_branch_id` read by the UPDATE predicate.

#### Writes
- `branch`: 1 row inserted by `INSERT ... VALUES`.
- `book_copy`: inserts rows from `book` and `generate_series(1, b.copies_total)`; exact row count depends on source data.
- `loan`: adds `copy_id` column.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id`; updates `home_branch_id = 1` where it is NULL.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`.
- PostgreSQL set-returning function: `generate_series`.
