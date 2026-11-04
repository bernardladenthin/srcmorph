### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:54:36Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Adds library branches, creates a book_copy table linking books to branches, and updates member and loan tables to reference branches and copies.

#### Purpose
- Introduce branch management and per‑copy tracking.
- Migrate existing book copy data into the new book_copy table.
- Update member and loan schemas to reference branches and copies.

#### Type
- Table: `branch`
- Table: `book_copy`
- Table: `loan` (ALTER)
- Table: `member` (ALTER)
- Table: `book` (ALTER)
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
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','ON_LOAN','LOST'))`
- **loan**
  - `copy_id BIGINT REFERENCES book_copy (copy_id)` (new column)
- **member**
  - `joined_on` renamed to `member_since`
  - `home_branch_id SMALLINT REFERENCES branch (branch_id)` (new column)
- **book**
  - `copies_free` and `copies_total` columns dropped; constraint `book_free_le_total` removed.
- **Index**
  - `idx_book_copy_book` on `book_copy (book_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_free, copies_total)
- `branch` (INSERT default branch)
- `loan` (none)
- `member` (none)

#### Writes
- `branch`: 1 row (`Main`, `Springfield`)
- `book_copy`: 1 + 1 + … + 1 rows per existing copy (total equals sum of `copies_total` across all books)
- `loan`: adds column `copy_id` (no data written)
- `member`: adds column `home_branch_id` and updates existing rows to `1` where null
- `book`: drops columns and constraint (no data written)

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
- Create `branch` table and insert default branch.
- Create `book_copy` table with constraints and status check.
- Populate `book_copy` by cross‑joining each book with a series from 1 to `copies_total`; assign barcode as `isbn` + '-' + series number; set status based on `copies_free`.
- Alter `loan` to reference `book_copy`.
- Rename `member.joined_on` to `member_since`; add `home_branch_id` and set default to main branch.
- Remove copy counters from `book` and drop related constraint.
- Create index on `book_copy.book_id`.
