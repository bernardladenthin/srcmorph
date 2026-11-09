### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:48:59Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Adds branch-specific copy inventory to a lending library, associates loans and members with copies and branches, and retires book-level copy counters.

#### Purpose
- Migrate the library schema to track individual book copies by branch.
- Assign members a home branch and replace book copy counters with copy records.

#### Type
- PostgreSQL schema migration; creates tables and an index, alters existing tables, and inserts and updates data.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key; unnamed constraint.
  - `name VARCHAR(100) NOT NULL` — unique; unnamed constraint.
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key; unnamed constraint.
  - `book_id BIGINT NOT NULL` — FK to `book(book_id)`.
  - `branch_id SMALLINT NOT NULL` — FK to `branch(branch_id)`.
  - `barcode VARCHAR(32) NOT NULL` — unique; unnamed constraint.
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'` — CHECK `status IN ('AVAILABLE', 'ON_LOAN', 'LOST')`; unnamed constraint.
- `loan`: adds `copy_id BIGINT`, FK to `book_copy(copy_id)`.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT`, FK to `branch(branch_id)`.
- `book`: drops constraint `book_free_le_total`; drops columns `copies_free` and `copies_total`.
- Index `idx_book_copy_book` on `book_copy(book_id)`; default method and operator class.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` (copy-generation query).

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: `INSERT ... SELECT`; one row per generated copy for each `book` row, with barcode derived from `isbn` and series number and status derived from `copies_free`.
- `loan`: adds `copy_id` column.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id`; updates `home_branch_id = 1` where NULL.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.
- `book_copy`: creates index `idx_book_copy_book`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`
- PostgreSQL function: `generate_series`
