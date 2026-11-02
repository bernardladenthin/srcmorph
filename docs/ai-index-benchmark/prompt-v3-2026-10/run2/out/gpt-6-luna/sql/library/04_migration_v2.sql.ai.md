### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:12:12Z
- G: 
- A: 0.0.0
- X: file
---
> Adds branch-aware lending inventory by creating library branches and book copies, linking loans and members to copies and branches, and retiring book-level copy counters.

#### Purpose
- Migrates the lending-library schema to track individual copies at branches.
- Seeds a main branch and generates copy records from existing book inventory.

#### Type
- PostgreSQL migration script.

#### Schema
- `branch`: `branch_id SMALLSERIAL` primary key (inline, unnamed); `name VARCHAR(100) NOT NULL UNIQUE` (inline, unnamed); `city VARCHAR(100) NOT NULL`.
- `book_copy`: `copy_id BIGSERIAL` primary key (inline, unnamed); `book_id BIGINT NOT NULL` FK to `book(book_id)` (inline, unnamed); `branch_id SMALLINT NOT NULL` FK to `branch(branch_id)` (inline, unnamed); `barcode VARCHAR(32) NOT NULL UNIQUE` (inline, unnamed); `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))` (inline, unnamed).
- `loan`: adds `copy_id BIGINT` FK to `book_copy(copy_id)` (unnamed).
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT` FK to `branch(branch_id)` (unnamed).
- `book`: drops constraint `book_free_le_total`; drops columns `copies_free` and `copies_total`.
- Index `idx_book_copy_book`: on `book_copy(book_id)`, default method (B-tree).

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` in the copy-generation query.
- `member`: existing `home_branch_id` values in the update predicate.

#### Writes
- `branch`: one inserted row (`Main`, `Springfield`).
- `book_copy`: one `INSERT ... SELECT`; inserts `SUM(book.copies_total)` rows across books (one per generated copy).
- `loan`: adds `copy_id` column.
- `member`: renames `joined_on`; adds `home_branch_id`; updates rows where `home_branch_id IS NULL`, setting it to `1`.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.
- `book_copy`: creates index `idx_book_copy_book`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`.
- Built-in function: `generate_series`.
