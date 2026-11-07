### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:52:10Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Adds branch-aware copy inventory to a lending library, assigns existing copies to the main branch, links loans and members to copies and branches, and removes per-book copy counters.

#### Purpose
- Migrate library inventory from book-level copy counts to individual, branch-specific copy records.
- Add member home-branch and loan copy references; rename the member join-date column.

#### Type
- PostgreSQL migration script; transaction (`BEGIN` / `COMMIT`).
- Creates tables `branch` and `book_copy`.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key (`branch_pkey`, declared inline).
  - `name VARCHAR(100) NOT NULL UNIQUE` — unique constraint (`branch_name_key`, declared inline).
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key (`book_copy_pkey`, declared inline).
  - `book_id BIGINT NOT NULL` — FK to `book(book_id)` (`book_copy_book_id_fkey`, declared inline).
  - `branch_id SMALLINT NOT NULL` — FK to `branch(branch_id)` (`book_copy_branch_id_fkey`, declared inline).
  - `barcode VARCHAR(32) NOT NULL UNIQUE` — unique constraint (`book_copy_barcode_key`, declared inline).
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))` — check constraint (`book_copy_status_check`, declared inline).
- `loan`: adds `copy_id BIGINT`, FK to `book_copy(copy_id)` (`loan_copy_id_fkey`, declared inline in `ALTER TABLE`).
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT`, FK to `branch(branch_id)` (`member_home_branch_id_fkey`, declared inline in `ALTER TABLE`).
- `book`: drops constraint `book_free_le_total`; drops columns `copies_free`, `copies_total`.
- Index: `idx_book_copy_book` on `book_copy(book_id)`, default PostgreSQL index method and operator class.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` (copy-generation query).
- `member`: `home_branch_id` (NULL filter in update).

#### Writes
- `branch`: inserts 1 row — `Main`, `Springfield`.
- `book_copy`: inserts one row per generated copy; total equals the sum of `book.copies_total` across selected books. Branch is set to `1`; barcode is derived from `isbn` and series number; status is derived from `copies_free`.
- `loan`: adds `copy_id`.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id`; updates NULL `home_branch_id` values to `1`.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`.
- Function: `generate_series`.
