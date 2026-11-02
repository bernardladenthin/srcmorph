### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:37:57Z
- G: 
- A: 0.0.0
- X: file
---
> Adds branch-based copy inventory to a lending library, assigns existing copies and members to the main branch, and removes per-book copy counters.

#### Purpose
- Introduces branches and individual book-copy records with branch location, barcode, and availability status.
- Migrates existing copies and member branch assignments; replaces `book.copies_free` and `book.copies_total` with copy records.

#### Type
- PostgreSQL schema migration

#### Schema
- `branch`: `branch_id SMALLSERIAL` (primary key), `name VARCHAR(100) NOT NULL UNIQUE`, `city VARCHAR(100) NOT NULL`
- `book_copy`: `copy_id BIGSERIAL` (primary key), `book_id BIGINT NOT NULL` (references `book.book_id`), `branch_id SMALLINT NOT NULL` (references `branch.branch_id`), `barcode VARCHAR(32) NOT NULL UNIQUE`, `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`
- `loan`: adds `copy_id BIGINT` referencing `book_copy.copy_id`
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT` referencing `branch.branch_id`
- `book`: drops constraint `book_free_le_total`; drops columns `copies_free` and `copies_total`
- Index `idx_book_copy_book`: on `book_copy (book_id)`; method and operator class not specified

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` — copy migration query
- `generate_series(1, b.copies_total)` — generates copy numbers
- `member.home_branch_id` — update condition

#### Writes
- `branch`: one `INSERT ... VALUES` tuple (1 row)
- `book_copy`: one `INSERT ... SELECT`; row count depends on `SUM(book.copies_total)` across books
- `loan`: schema change adds `copy_id`; no data rows written
- `member`: updates `home_branch_id` to `1` for rows where it is `NULL`; row count depends on existing data
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`
- Function: `generate_series`
