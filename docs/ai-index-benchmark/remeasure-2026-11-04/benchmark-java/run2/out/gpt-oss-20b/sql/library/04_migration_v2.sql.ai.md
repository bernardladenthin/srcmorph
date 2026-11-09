### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:33:45Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Adds library branches, creates a book_copy table per branch, moves copy data, and updates member and loan tables.

#### Purpose
- Introduce branch hierarchy and per‑branch book copies.
- Migrate existing copy counts into book_copy rows.
- Update member and loan schemas to reference branches and copies.

#### Type
- Table: `branch`
- Table: `book_copy`
- Table: `loan` (altered)
- Table: `member` (altered)
- Index: `idx_book_copy_book`

#### Schema
**branch**
- `branch_id SMALLSERIAL PRIMARY KEY`
- `name VARCHAR(100) NOT NULL UNIQUE`
- `city VARCHAR(100) NOT NULL`

**book_copy**
- `copy_id BIGSERIAL PRIMARY KEY`
- `book_id BIGINT NOT NULL REFERENCES book(book_id)`
- `branch_id SMALLINT NOT NULL REFERENCES branch(branch_id)`
- `barcode VARCHAR(32) NOT NULL UNIQUE`
- `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','ON_LOAN','LOST'))`

**loan** (altered)
- `copy_id BIGINT REFERENCES book_copy(copy_id)`

**member** (altered)
- `member_since DATE` (renamed from `joined_on`)
- `home_branch_id SMALLINT REFERENCES branch(branch_id)`

**Indexes**
- `idx_book_copy_book` ON `book_copy(book_id)`

#### Reads
- `book` (SELECT book_id, isbn, copies_free, copies_total)
- `branch` (INSERT default branch)
- `member` (UPDATE home_branch_id where NULL)

#### Writes
- `branch`: 1 row (`Main`, `Springfield`)
- `book_copy`: 1 + 1 = 2 INSERT statements
  - First INSERT: 1 row (default branch)
  - Second INSERT: `b.copies_total` rows per book (family)
- `loan`: 1 column added (no data written)
- `member`: 1 column added, 1 UPDATE setting `home_branch_id` to 1 where NULL
- `book`: columns `copies_free`, `copies_total` dropped; constraint `book_free_le_total` dropped

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Table `book` (existing)
- Table `loan` (existing)
- Table `member` (existing)
- Table `book_copy` (new)
- Table `branch` (new)
- Index `idx_book_copy_book` (new)
