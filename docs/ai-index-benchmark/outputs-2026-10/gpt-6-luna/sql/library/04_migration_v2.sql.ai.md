### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-28T21:21:24Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates a lending library to track physical book copies by branch and assign members a home branch.

#### Purpose
- Adds branch and per-copy inventory, associates loans with copies, and moves member join-date and book availability data to the new schema.
- Seeds a Main branch and migrates existing book copies to it.

#### Type
- PostgreSQL migration script; transactional DDL/DML.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — PK
  - `name VARCHAR(100)` — NOT NULL, UNIQUE
  - `city VARCHAR(100)` — NOT NULL
- `book_copy`
  - `copy_id BIGSERIAL` — PK
  - `book_id BIGINT` — NOT NULL, FK to `book(book_id)`
  - `branch_id SMALLINT` — NOT NULL, FK to `branch(branch_id)`
  - `barcode VARCHAR(32)` — NOT NULL, UNIQUE
  - `status VARCHAR(12)` — NOT NULL, DEFAULT `'AVAILABLE'`, CHECK in `AVAILABLE`, `ON_LOAN`, `LOST`
  - Index: `idx_book_copy_book (book_id)`
- `loan`: adds nullable `copy_id BIGINT`, FK to `book_copy(copy_id)`.
- `member`: renames `joined_on` to `member_since`; adds nullable `home_branch_id SMALLINT`, FK to `branch(branch_id)`.
- `book`: drops constraint `book_free_le_total` and columns `copies_free`, `copies_total`.

#### Reads
- `book`: reads `book_id`, `isbn`, `copies_total`, `copies_free` to generate copy rows and their initial statuses.
- `member`: reads `home_branch_id` to select rows where it is NULL.

#### Writes
- `branch`: inserts the `Main` branch in `Springfield`.
- `book_copy`: inserts one row per generated copy; sets `book_id`, `branch_id`, `barcode`, and `status`.
- `loan`: adds the `copy_id` column and FK.
- `member`: renames `joined_on`, adds `home_branch_id`, and sets NULL `home_branch_id` values to `1`.
- `book`: drops `book_free_le_total`, `copies_free`, and `copies_total`.
- Creates index `idx_book_copy_book` on `book_copy(book_id)`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`
