### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:28:12Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Adds branch-based lending inventory, assigns existing book copies to the main branch, and associates loans and members with copies and branches.

#### Purpose
- Introduces library branches and per-copy inventory with barcode and availability status.
- Migrates existing book copy counts into `book_copy` and removes the per-book counters.

#### Type
- PostgreSQL schema migration; creates tables and an index, alters existing tables.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key (unnamed constraint).
  - `name VARCHAR(100) NOT NULL` — unique (unnamed constraint).
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key (unnamed constraint).
  - `book_id BIGINT NOT NULL` — foreign key to `book(book_id)` (unnamed constraint).
  - `branch_id SMALLINT NOT NULL` — foreign key to `branch(branch_id)` (unnamed constraint).
  - `barcode VARCHAR(32) NOT NULL` — unique (unnamed constraint).
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'` — check: `status IN ('AVAILABLE', 'ON_LOAN', 'LOST')` (unnamed constraint).
- `loan`: adds `copy_id BIGINT`, foreign key to `book_copy(copy_id)` (unnamed constraint).
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT`, foreign key to `branch(branch_id)` (unnamed constraint).
- `book`: drops constraint `book_free_le_total` and columns `copies_free`, `copies_total`.
- Index: `idx_book_copy_book` on `book_copy(book_id)`; default method (B-tree).

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` — copy generation and status assignment.
- `member.home_branch_id` — update predicate.

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: inserts one row per generated copy, for each `book` row and `generate_series(1, copies_total)`; `branch_id` set to `1`, barcode composed from `isbn` and series number, status derived from `copies_free`.
- `loan`: adds `copy_id`.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id`; sets it to `1` where NULL.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book`, `loan`, `member`
- `generate_series`
