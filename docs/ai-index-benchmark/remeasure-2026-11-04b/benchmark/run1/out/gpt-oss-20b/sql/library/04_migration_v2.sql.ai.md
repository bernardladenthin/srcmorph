### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:54:06Z
- G: 
- A: 0.0.0
- X: file
- P: 6E189EB5
---
> Migrates the library schema to version 2: adds branches, creates a book‑copy table per branch, moves copy data, and updates member and loan tables.

#### Purpose
- Introduce branch hierarchy and per‑branch book copies.
- Replace per‑book copy counters with a dedicated `book_copy` table.
- Update member and loan tables to reference branches and copies.

#### Type
- PostgreSQL migration script (DDL).

#### Schema
- **branch**  
  - `branch_id SMALLSERIAL PRIMARY KEY`  
  - `name VARCHAR(100) NOT NULL UNIQUE`  
  - `city VARCHAR(100) NOT NULL`  
  - Index: `idx_branch_name` (implicit from UNIQUE on `name`)

- **book_copy**  
  - `copy_id BIGSERIAL PRIMARY KEY`  
  - `book_id BIGINT NOT NULL REFERENCES book(book_id)`  
  - `branch_id SMALLINT NOT NULL REFERENCES branch(branch_id)`  
  - `barcode VARCHAR(32) NOT NULL UNIQUE`  
  - `status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','ON_LOAN','LOST'))`  
  - Index: `idx_book_copy_book` on `book_id`

- **loan** (altered)  
  - Added column `copy_id BIGINT REFERENCES book_copy(copy_id)`

- **member** (altered)  
  - Renamed column `joined_on` → `member_since`  
  - Added column `home_branch_id SMALLINT REFERENCES branch(branch_id)`  
  - Updated existing rows: `home_branch_id = 1` where NULL

- **book** (altered)  
  - Dropped constraint `book_free_le_total`  
  - Dropped columns `copies_free`, `copies_total`

#### Reads
- `book.book_id`, `book.isbn`, `book.copies_free`, `book.copies_total` (in `INSERT INTO book_copy` SELECT)
- `branch.branch_id` (implicit via foreign key)
- `loan` and `member` tables (for ALTER operations)

#### Writes
- `branch`: 1 row inserted (`Main`, `Springfield`)
- `book_copy`:  
  - Family: 1 INSERT statement inserting `b.copies_total` rows per book  
  - Total rows: sum of `copies_total` across all books (exact count not specified)
- `member`: 1 UPDATE statement setting `home_branch_id = 1` where NULL
- `loan`: 1 ALTER TABLE adding column (no data written)
- `book`: 1 ALTER TABLE dropping columns and constraint

#### Relationships
- `book_copy.book_id → book.book_id`
- `book_copy.branch_id → branch.branch_id`
- `loan.copy_id → book_copy.copy_id`
- `member.home_branch_id → branch.branch_id`

#### Dependencies
- Existing tables: `book`, `loan`, `member`
- New table: `branch`, `book_copy`
- Existing constraint: `book_free_le_total` (removed)
- Foreign key references to `book`, `branch`, `book_copy`  

---
