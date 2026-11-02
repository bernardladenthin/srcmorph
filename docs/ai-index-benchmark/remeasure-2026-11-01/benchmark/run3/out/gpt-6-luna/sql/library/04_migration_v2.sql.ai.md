### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:01:10Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates a lending-library schema to track individual book copies by branch, assign members to home branches, and replace per-book copy counters.

#### Purpose
- Adds branch locations and individual copy inventory with barcode and loan-status tracking.
- Links loans and members to copies and branches; removes legacy copy counters from books.

#### Type
- PostgreSQL migration script.
- Creates tables: `branch`, `book_copy`.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key (unnamed constraint).
  - `name VARCHAR(100) NOT NULL UNIQUE` — unnamed unique constraint.
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key (unnamed constraint).
  - `book_id BIGINT NOT NULL` — FK to `book(book_id)` (unnamed constraint).
  - `branch_id SMALLINT NOT NULL` — FK to `branch(branch_id)` (unnamed constraint).
  - `barcode VARCHAR(32) NOT NULL UNIQUE` — unnamed unique constraint.
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'` — check `status IN ('AVAILABLE', 'ON_LOAN', 'LOST')` (unnamed constraint).
- Alterations:
  - `loan.copy_id BIGINT` — FK to `book_copy(copy_id)` (unnamed constraint).
  - `member.joined_on` renamed to `member.member_since`.
  - `member.home_branch_id SMALLINT` — FK to `branch(branch_id)` (unnamed constraint).
  - Drops `book_free_le_total`, `book.copies_free`, and `book.copies_total`.
- Index: `idx_book_copy_book` on `book_copy(book_id)`; default method B-tree.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` — used to generate copy rows and derive status.
- `member`: `home_branch_id` — read by the `IS NULL` update predicate.

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: one row per generated series value for each `book` row, from `1` through `copies_total`; count depends on existing book data.
- `loan`: adds `copy_id` column and FK.
- `member`: renames `joined_on` to `member_since`, adds `home_branch_id`, sets null `home_branch_id` values to `1`.
- `book`: drops constraint `book_free_le_total` and columns `copies_free`, `copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book`
- `loan`
- `member`
- `generate_series` (PostgreSQL set-returning function)
