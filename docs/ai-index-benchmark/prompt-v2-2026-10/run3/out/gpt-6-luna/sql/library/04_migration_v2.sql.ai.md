### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:59:18Z
- G: 
- A: 0.0.0
- X: file
---
> Adds branch-based lending inventory, assigns existing book copies to the main branch, links loans and members to copies and branches, and retires book-level copy counters.

#### Purpose
- Introduces branch and copy records for library inventory, initializing copies from existing `book` counters.
- Adds branch and copy references to `member` and `loan`; removes obsolete copy counters from `book`.

#### Type
- PostgreSQL schema migration script; transactional DDL and DML.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key; constraint declared inline, unnamed.
  - `name VARCHAR(100) NOT NULL` — unique; constraint declared inline, unnamed.
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key; constraint declared inline, unnamed.
  - `book_id BIGINT NOT NULL` — references `book(book_id)`; FK declared inline, unnamed.
  - `branch_id SMALLINT NOT NULL` — references `branch(branch_id)`; FK declared inline, unnamed.
  - `barcode VARCHAR(32) NOT NULL` — unique; constraint declared inline, unnamed.
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'` — check permits `AVAILABLE`, `ON_LOAN`, `LOST`; constraints declared inline, unnamed.
  - Index `idx_book_copy_book` on `(book_id)`.
- `loan`
  - Adds `copy_id BIGINT` referencing `book_copy(copy_id)`; FK declared inline, unnamed.
- `member`
  - Renames `joined_on` to `member_since`.
  - Adds `home_branch_id SMALLINT` referencing `branch(branch_id)`; FK declared inline, unnamed.
- `book`
  - Drops constraint `book_free_le_total`.
  - Drops columns `copies_free`, `copies_total`.

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` — generate copy rows and determine initial copy status.
- `member.home_branch_id` — checked for NULL by the update.

#### Writes
- `branch` — inserts exactly 1 row: `('Main', 'Springfield')`.
- `book_copy` — inserts one row per generated series value for each `book`; total is the sum of `book.copies_total` across source rows. Sets `branch_id = 1`, barcode from `isbn` and series number, and status based on `copies_free`.
- `loan` — adds `copy_id`.
- `member` — renames `joined_on` to `member_since`; adds `home_branch_id`; sets NULL `home_branch_id` values to `1`.
- `book` — drops `book_free_le_total`, `copies_free`, and `copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`.
- PostgreSQL function: `generate_series`.
