### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:54:23Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, assigns each book copy to a branch, and updates member and loan tables to reference the new branch and copy structures.

#### Purpose
- Introduces branch hierarchy for the library.
- Normalizes book copies per branch.
- Updates member and loan tables to reference branches and copies.

#### Type
- SQL migration script (PostgreSQL).

#### Schema
- **branch**
  - `branch_id SMALLSERIAL PRIMARY KEY`
  - `name VARCHAR(100) NOT NULL UNIQUE`
  - `city VARCHAR(100) NOT NULL`
- **book_copy**
  - `copy_id BIGSERIAL PRIMARY KEY`
  - `book_id BIGINT NOT NULL REFERENCES book(book_id)`
  - `branch_id SMALLINT NOT NULL REFERENCES branch(branch_id)`
  - `barcode VARCHAR(32) NOT NULL UNIQUE`
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','ON_LOAN','LOST'))`
- **loan** (altered)
  - `copy_id BIGINT REFERENCES book_copy(copy_id)`
- **member** (altered)
  - `member_since TIMESTAMP` (renamed from `joined_on`)
  - `home_branch_id SMALLINT REFERENCES branch(branch_id)`

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_free`, `book.copies_total` (for populating `book_copy`).
- `member.home_branch_id` (to set default).

#### Writes
- Inserts into `branch` and `book_copy`.
- Updates `member.home_branch_id`.
- Adds columns to `loan` and `member`.
- Drops columns and constraints from `book`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Existing tables: `book`, `loan`, `member`.
- Existing constraint: `book_free_le_total` (removed).

#### Routine logic
- Create `branch` table and seed with a main branch.
- Create `book_copy` table with foreign keys to `book` and `branch`.
- Populate `book_copy` with one row per existing copy, assigning to main branch and generating barcode.
- Add `copy_id` column to `loan`.
- Rename `joined_on` to `member_since` in `member`.
- Add `home_branch_id` to `member` and set default to main branch.
- Remove obsolete copy counters from `book`.
- Create index on `book_copy.book_id`.
