### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:49:58Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates a lending-library schema to track individual book copies by branch and assign members a home branch.

#### Purpose
- Adds library branches and branch-specific copy inventory; migrates existing book copy counts into individual copy records.
- Adds loan-to-copy and member-to-branch references, renames the member join-date column, and removes book-level copy counters.

#### Type
- PostgreSQL schema migration script

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key (unnamed constraint)
  - `name VARCHAR(100) NOT NULL` — unique (unnamed constraint)
  - `city VARCHAR(100) NOT NULL`
- `book_copy`
  - `copy_id BIGSERIAL` — primary key (unnamed constraint)
  - `book_id BIGINT NOT NULL` — FK to `book.book_id` (unnamed constraint)
  - `branch_id SMALLINT NOT NULL` — FK to `branch.branch_id` (unnamed constraint)
  - `barcode VARCHAR(32) NOT NULL` — unique (unnamed constraint)
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'` — CHECK: `status IN ('AVAILABLE', 'ON_LOAN', 'LOST')` (unnamed constraint)
- Altered columns and constraints:
  - `loan.copy_id BIGINT` — FK to `book_copy.copy_id` (unnamed constraint)
  - `member.joined_on` renamed to `member.member_since`
  - `member.home_branch_id SMALLINT` — FK to `branch.branch_id` (unnamed constraint)
  - `book.book_free_le_total` constraint dropped; `book.copies_free` and `book.copies_total` columns dropped
- Index: `idx_book_copy_book` on `book_copy (book_id)`, default method (B-tree)

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` — copy migration query.
- `member`: `home_branch_id` — NULL check in update.

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: one `INSERT ... SELECT`; produces one row per generated value from `1` through each `book.copies_total` (row count depends on existing `book` data).
- `member.home_branch_id`: updated to `1` where NULL.
- Schema changes: creates `branch` and `book_copy`; alters `loan` and `member`; drops constraint and columns from `book`; creates `idx_book_copy_book`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`
- Migration query joins each `book` row to generated series values using `CROSS JOIN LATERAL`.

#### Dependencies
- Tables: `book`, `loan`, `member`
- Function: `generate_series`
