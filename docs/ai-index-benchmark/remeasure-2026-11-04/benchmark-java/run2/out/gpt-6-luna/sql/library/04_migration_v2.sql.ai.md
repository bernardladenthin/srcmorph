### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:30:01Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Adds branch-aware physical book copies to the lending library, assigns existing copies and members to the main branch, and retires book-level copy counters.

#### Purpose
- Introduces branches and per-copy inventory with barcode and availability status.
- Migrates existing copy and member data to the main branch; adds a copy reference to loans and removes book-level copy counters.

#### Type
- PostgreSQL schema migration.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key (unnamed constraint).
  - `name VARCHAR(100) NOT NULL` — UNIQUE (unnamed constraint).
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key (unnamed constraint).
  - `book_id BIGINT NOT NULL` — FK to `book.book_id` (unnamed constraint).
  - `branch_id SMALLINT NOT NULL` — FK to `branch.branch_id` (unnamed constraint).
  - `barcode VARCHAR(32) NOT NULL` — UNIQUE (unnamed constraint).
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'` — CHECK `status IN ('AVAILABLE', 'ON_LOAN', 'LOST')` (unnamed constraint).
- `loan`: adds `copy_id BIGINT`, FK to `book_copy.copy_id` (unnamed constraint).
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT`, FK to `branch.branch_id` (unnamed constraint).
- `book`: drops constraint `book_free_le_total`; drops columns `copies_free`, `copies_total`.
- Index `idx_book_copy_book`: on `book_copy (book_id)`, default method and operator class.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` in the copy migration query.
- `member`: `home_branch_id` in the update predicate.

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: 1 `INSERT ... SELECT`; creates one row per generated copy for each book, with barcode from `isbn` and sequence number, and status determined by `copies_free`.
- `loan`: adds `copy_id` column.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id`; updates rows where it is NULL to `1`.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`.
- PostgreSQL function: `generate_series`.
