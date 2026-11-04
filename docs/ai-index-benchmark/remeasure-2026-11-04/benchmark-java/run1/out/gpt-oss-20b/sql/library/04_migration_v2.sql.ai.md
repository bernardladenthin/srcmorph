### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:17:11Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Adds library branches, creates a book_copy table per branch, and updates loan and member tables to reference copies and branches.

#### Purpose
- Introduces branch hierarchy for a lending library.
- Normalizes book copies to a dedicated table with branch association.
- Updates member and loan schemas to reflect branch data.

#### Type
- Table: `branch`
- Table: `book_copy`
- Table: `loan` (altered)
- Table: `member` (altered)
- Index: `idx_book_copy_book`

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
- **loan**
  - `copy_id BIGINT REFERENCES book_copy(copy_id)` (new column)
- **member**
  - `member_since TIMESTAMP` (renamed from `joined_on`)
  - `home_branch_id SMALLINT REFERENCES branch(branch_id)` (new column)

- **idx_book_copy_book**
  - `book_copy` on `book_id`

#### Reads
- `book` (SELECT in INSERT into `book_copy`)
- `branch` (INSERT into `branch`)

#### Writes
- `branch`: 1 row (`INSERT INTO branch VALUES ('Main','Springfield')`)
- `book_copy`: 1 INSERT statement generating `b.copies_total` rows per book (family: `INSERT INTO book_copy ... SELECT ... FROM book ... CROSS JOIN LATERAL generate_series(...)`)
- `loan`: 1 column added (no data written)
- `member`: 1 column added and updated (`UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL`)

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Table `book` (referenced by `book_copy` and in INSERT SELECT)
- Table `loan` (altered)
- Table `member` (altered)
- Table `branch` (created and referenced)
- Table `book_copy` (created and referenced)

#### Routine logic
- None (only DDL and data manipulation statements).
