### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:37:15Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Migrates a lending library from book-level copy counters to individually tracked, branch-assigned copies and adds branch affiliation for members.

#### Purpose
- Add library branches and per-copy barcode, branch, and availability tracking.
- Link loans to copies, assign members a home branch, and retire book-level copy counters.

#### Type
- PostgreSQL migration script; creates tables and an index, alters existing tables, and inserts/updates data.

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — primary key.
  - `name VARCHAR(100) NOT NULL UNIQUE`.
  - `city VARCHAR(100) NOT NULL`.
- `book_copy`
  - `copy_id BIGSERIAL` — primary key.
  - `book_id BIGINT NOT NULL` — foreign key to `book(book_id)`.
  - `branch_id SMALLINT NOT NULL` — foreign key to `branch(branch_id)`.
  - `barcode VARCHAR(32) NOT NULL UNIQUE`.
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`.
- `loan`: adds nullable `copy_id BIGINT`, foreign key to `book_copy(copy_id)`.
- `member`: renames `joined_on` to `member_since`; adds nullable `home_branch_id SMALLINT`, foreign key to `branch(branch_id)`.
- `book`: drops constraint `book_free_le_total`; drops columns `copies_free` and `copies_total`.
- Index `idx_book_copy_book`: on `book_copy(book_id)`.

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` — used to generate copy rows and derive their status.
- `generate_series(1, b.copies_total)` — generates copy sequence values.

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: inserts one row per generated copy for each book; barcode uses `isbn` and sequence number; status is `AVAILABLE` when sequence number is at most `copies_free`, otherwise `ON_LOAN`.
- `member.home_branch_id`: sets null values to `1`.
- Schema writes: adds `loan.copy_id`, renames `member.joined_on`, adds `member.home_branch_id`, removes `book_free_le_total`, `book.copies_free`, and `book.copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book`, `loan`, `member`, `generate_series`.
