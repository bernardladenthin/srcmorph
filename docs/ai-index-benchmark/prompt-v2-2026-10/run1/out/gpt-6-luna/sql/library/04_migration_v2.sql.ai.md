### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:32:42Z
- G: 
- A: 0.0.0
- X: file
---
> Adds branch-aware lending by creating library branches and book copies, assigning existing copies to the main branch, and linking loans and members to copies and home branches.

#### Purpose
- Migrate the lending-library schema to track physical book copies by branch.
- Replace per-book copy counters with `book_copy` records and add member home-branch and loan copy references.

#### Type
- PostgreSQL schema migration (DDL/DML), wrapped in a transaction.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key (unnamed constraint).
  - `name VARCHAR(100) NOT NULL` — unique (unnamed constraint).
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key (unnamed constraint).
  - `book_id BIGINT NOT NULL` — FK to `book(book_id)` (unnamed constraint).
  - `branch_id SMALLINT NOT NULL` — FK to `branch(branch_id)` (unnamed constraint).
  - `barcode VARCHAR(32) NOT NULL` — unique (unnamed constraint).
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'` — check `status IN ('AVAILABLE', 'ON_LOAN', 'LOST')` (unnamed constraint).
  - Index `idx_book_copy_book` on `book_copy(book_id)`.
- `loan`
  - Adds `copy_id BIGINT` — FK to `book_copy(copy_id)` (unnamed constraint).
- `member`
  - Renames `joined_on` to `member_since`.
  - Adds `home_branch_id SMALLINT` — FK to `branch(branch_id)` (unnamed constraint).
- `book`
  - Drops constraint `book_free_le_total`.
  - Drops columns `copies_free` and `copies_total`.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` (copy generation and status assignment).
- `member`: `home_branch_id` (UPDATE predicate).

#### Writes
- `branch`: inserts exactly 1 row: `name='Main'`, `city='Springfield'`.
- `book_copy`: inserts one row per generated `g.n` from 1 through each `book.copies_total`; total equals the sum of `copies_total` across `book` rows. Sets `book_id`, `branch_id=1`, `barcode=isbn || '-' || g.n`, and status to `AVAILABLE` when `g.n <= copies_free`, otherwise `ON_LOAN`.
- `loan`: adds `copy_id`.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id`; sets null `home_branch_id` values to `1`.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`.
- PostgreSQL function: `generate_series`.
