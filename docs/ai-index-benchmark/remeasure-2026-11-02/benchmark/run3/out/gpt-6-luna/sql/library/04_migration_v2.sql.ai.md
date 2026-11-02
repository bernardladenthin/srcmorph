### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:58:42Z
- G: 
- A: 0.0.0
- X: file
- P: A6004777
---
> Migrates a lending library to track physical book copies by branch, assign copies to loans, and replace book-level copy counts with branch-level inventory.

#### Purpose
- Adds library branches and individual copy records, placing existing copies at the main branch.
- Adds loan and member branch references, renames the member join-date column, and removes book-level copy counters.

#### Type
- PostgreSQL schema migration script

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key
  - `name VARCHAR(100) NOT NULL UNIQUE`
  - `city VARCHAR(100) NOT NULL`
- `book_copy`
  - `copy_id BIGSERIAL` — primary key
  - `book_id BIGINT NOT NULL` — foreign key to `book(book_id)`
  - `branch_id SMALLINT NOT NULL` — foreign key to `branch(branch_id)`
  - `barcode VARCHAR(32) NOT NULL UNIQUE`
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'` — check: `status IN ('AVAILABLE', 'ON_LOAN', 'LOST')`
- `loan`: adds `copy_id BIGINT`, foreign key to `book_copy(copy_id)`.
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT`, foreign key to `branch(branch_id)`.
- `book`: drops constraint `book_free_le_total`; drops columns `copies_free`, `copies_total`.
- Index: `idx_book_copy_book` on `book_copy(book_id)`, default method (B-tree).

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` — copy-generation query.
- `generate_series(1, b.copies_total)` — generates one candidate copy per existing copy count.

#### Writes
- `branch`: 1 row — `('Main', 'Springfield')`.
- `book_copy`: inserts rows from `book`, one per generated series value; barcode is `isbn || '-' || n`, status depends on `copies_free`.
- `loan`: adds `copy_id` column.
- `member`: renames `joined_on`; adds `home_branch_id`; updates all rows where it is `NULL` to `1`.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`
- Function: `generate_series`
