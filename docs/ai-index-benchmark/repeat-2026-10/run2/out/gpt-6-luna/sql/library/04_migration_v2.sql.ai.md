### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:50:27Z
- G: 
- A: 0.0.0
- X: file
---
> Adds branch-based lending by assigning existing book copies to a main branch and linking loans and members to branch-aware records.

#### Purpose
- Introduces library branches and individual, branch-assigned book copies.
- Migrates existing copy counts into `book_copy`, adds loan and member references, and removes per-book copy counters.

#### Type
- PostgreSQL schema migration script

#### Schema
- `branch`
  - `branch_id SMALLSERIAL` — PK
  - `name VARCHAR(100)` — NOT NULL, UNIQUE
  - `city VARCHAR(100)` — NOT NULL
- `book_copy`
  - `copy_id BIGSERIAL` — PK
  - `book_id BIGINT` — NOT NULL, FK to `book.book_id`
  - `branch_id SMALLINT` — NOT NULL, FK to `branch.branch_id`
  - `barcode VARCHAR(32)` — NOT NULL, UNIQUE
  - `status VARCHAR(12)` — NOT NULL, DEFAULT `'AVAILABLE'`, CHECK in `('AVAILABLE', 'ON_LOAN', 'LOST')`
- Existing table changes:
  - `loan.copy_id BIGINT` — nullable FK to `book_copy.copy_id`
  - `member.joined_on` renamed to `member.member_since`
  - `member.home_branch_id SMALLINT` — FK to `branch.branch_id`
  - `book.copies_free` and `book.copies_total` dropped; constraint `book_free_le_total` dropped
- Index: `idx_book_copy_book` on `book_copy(book_id)`

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_total`, `book.copies_free` — used to generate one `book_copy` row per existing copy and determine its status.
- `generate_series(1, b.copies_total)` — generates copy numbers.

#### Writes
- Inserts `branch('Main', 'Springfield')`.
- Inserts `book_copy` rows for existing copies; branch ID `1`, barcode `<isbn>-<copy number>`, status `AVAILABLE` when copy number is at most `copies_free`, otherwise `ON_LOAN`.
- Alters `loan`, `member`, and `book` as detailed in Schema.
- Updates `member.home_branch_id` to `1` where it is NULL.
- Creates index `idx_book_copy_book`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`
- PostgreSQL function: `generate_series`
