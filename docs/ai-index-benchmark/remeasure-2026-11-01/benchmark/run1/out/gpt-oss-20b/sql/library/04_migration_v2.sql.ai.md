### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:27:04Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, assigns each book copy to a branch, and updates member and loan tables to reference branches and copies.

#### Purpose
- Introduce branch hierarchy for a lending library.
- Migrate existing book copies to a new `book_copy` table with branch association.
- Update member and loan tables to reference branches and copies.

#### Type
- PostgreSQL DDL: tables, indexes, column alterations, data migration.

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
- **Indexes**
  - `idx_book_copy_book` ON `book_copy(book_id)`

#### Reads
- `book` table: `book_id`, `isbn`, `copies_free`, `copies_total` (for migration).
- `member` table: `home_branch_id` (before update).
- `loan` table: none (only altered).

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`).
- `book_copy`: one row per existing copy; total rows equal sum of `copies_total` across all books.
- `member`: `home_branch_id` set to 1 where NULL.
- `book`: columns `copies_free`, `copies_total` dropped; constraint `book_free_le_total` removed.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Existing `book` table (primary key `book_id`).
- Existing `loan` and `member` tables (altered).
- `book_copy` references `book` and `branch`.
