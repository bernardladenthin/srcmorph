### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:39:49Z
- G: 
- A: 0.0.0
- X: file
---
> Adds a branch hierarchy, creates a book_copy table linking books to branches, and updates member and loan tables to reference branches and copies.

#### Purpose
- Create `branch` table for library locations.
- Create `book_copy` table to track individual copies per branch.
- Migrate existing book copy data into `book_copy`.
- Update `loan` and `member` tables to reference copies and branches.
- Remove legacy copy counters from `book`.

#### Type
- SQL migration script (PostgreSQL).

#### Schema
**branch**
- `branch_id SMALLSERIAL PRIMARY KEY`
- `name VARCHAR(100) NOT NULL UNIQUE`
- `city VARCHAR(100) NOT NULL`

**book_copy**
- `copy_id BIGSERIAL PRIMARY KEY`
- `book_id BIGINT NOT NULL REFERENCES book (book_id)`
- `branch_id SMALLINT NOT NULL REFERENCES branch (branch_id)`
- `barcode VARCHAR(32) NOT NULL UNIQUE`
- `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))`
- Index `idx_book_copy_book` on `(book_id)`

**Alterations**
- `loan` gains `copy_id BIGINT REFERENCES book_copy (copy_id)`
- `member` column `joined_on` renamed to `member_since`
- `member` gains `home_branch_id SMALLINT REFERENCES branch (branch_id)`
- `book` loses constraint `book_free_le_total`, columns `copies_free`, `copies_total`

#### Reads
- `book` table: `book_id`, `isbn`, `copies_total`, `copies_free`
- `branch` table: none (only inserted)
- `member` table: `home_branch_id` (for UPDATE)
- `loan` table: none (only altered)

#### Writes
- Insert into `branch`: 1 row (`Main`, `Springfield`)
- Insert into `book_copy`: one row per existing copy (total rows = sum of `copies_total` across all books)
- Update `member`: set `home_branch_id` to 1 where null
- Alter `loan`, `member`, `book` tables: add/drop columns and constraints

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Routine logic
- `INSERT INTO book_copy` uses `SELECT` with `CROSS JOIN LATERAL generate_series` to create one row per copy, assigning barcode as `isbn` + '-' + copy number, status based on `copies_free`.

#### Dependencies
- `book` table (existing)
- `loan` table (existing)
- `member` table (existing)
- `book_copy` table (new)
- `branch` table (new)
