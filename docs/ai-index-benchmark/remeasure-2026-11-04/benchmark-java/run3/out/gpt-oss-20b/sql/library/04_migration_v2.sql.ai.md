### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:48:44Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Adds library branches, creates a book_copy table linking books to branches, and updates member and loan tables to reference branches and copies.

#### Purpose
- Introduce branch dimension for library locations.
- Replace per-book copy counters with explicit book_copy records.
- Link loans to specific copies and members to home branches.

#### Type
- Table: `branch`
- Table: `book_copy`
- Table alterations: `loan`, `member`, `book`
- Index: `idx_book_copy_book`

#### Schema
- **branch**
  - `branch_id SMALLSERIAL PRIMARY KEY`
  - `name VARCHAR(100) NOT NULL UNIQUE`
  - `city VARCHAR(100) NOT NULL`
- **book_copy**
  - `copy_id BIGSERIAL PRIMARY KEY`
  - `book_id BIGINT NOT NULL REFERENCES book (book_id)`
  - `branch_id SMALLINT NOT NULL REFERENCES branch (branch_id)`
  - `barcode VARCHAR(32) NOT NULL UNIQUE`
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`
- **loan**
  - `copy_id BIGINT REFERENCES book_copy (copy_id)` (new column)
- **member**
  - `joined_on` renamed to `member_since`
  - `home_branch_id SMALLINT REFERENCES branch (branch_id)` (new column, default 1)
- **book**
  - `copies_free` and `copies_total` columns dropped
  - Constraint `book_free_le_total` dropped
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` table: `book_id`, `isbn`, `copies_free`, `copies_total`
- `member` table: `home_branch_id` (before update)

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`)
- `book_copy`: 1 × `b.copies_total` rows inserted per book (family: one INSERT, total rows = sum of `copies_total` across all books)
- `loan`: new column `copy_id` added (no data written)
- `member`: column `home_branch_id` added; 1 row updated per member where null (family: one UPDATE, total rows = count of members with null `home_branch_id`)
- `book`: columns and constraint dropped (no data written)

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- `book` table (existing)
- `loan` table (existing)
- `member` table (existing)
- `book_copy` table (new)
- `branch` table (new)

#### Routine logic
- None (DDL only).
