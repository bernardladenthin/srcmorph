### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:15:55Z
- G: 
- A: 0.0.0
- X: file
---
> Migration script for lending library schema v2: creates branch table, populates book copies per branch, renames member column, and retires per-book copy counters.

#### Purpose
- Creates branch entity and initial book copy records per existing book.
- Reassigns copies to branch-specific locations.
- Renames member.joined_on to member_since and adds home_branch_id.
- Removes book copy counters (copies_free, copies_total) and the copy_id column in loan.

#### Type
- SQL object kind: migration script (tables, indexes, DDL/DML) in PostgreSQL.

#### Schema
- branch: branch_id SMALLSERIAL PRIMARY KEY; name VARCHAR(100) NOT NULL UNIQUE; city VARCHAR(100) NOT NULL
- book_copy: copy_id BIGSERIAL PRIMARY KEY; book_id BIGINT NOT NULL REFERENCES book(book_id); branch_id SMALLINT NOT NULL REFERENCES branch(branch_id); barcode VARCHAR(32) NOT NULL UNIQUE; status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE','ON_LOAN','LOST'))
- loan: ALTER TABLE loan ADD COLUMN copy_id BIGINT REFERENCES book_copy(copy_id)
- member: ALTER TABLE member RENAME COLUMN joined_on TO member_since; ALTER TABLE member ADD COLUMN home_branch_id SMALLINT REFERENCES branch(branch_id); UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL
- book: ALTER TABLE book DROP CONSTRAINT book_free_le_total; ALTER TABLE book DROP COLUMN copies_free; ALTER TABLE book DROP COLUMN copies_total
- index: idx_book_copy_book ON book_copy (book_id)

#### Reads
- book: book_id, isbn, copies_free, copies_total
- branch: name, city
- member: joined_on, home_branch_id

#### Writes
- branch: branch_id, name, city
- book_copy: copy_id, book_id, branch_id, barcode, status
- loan: copy_id
- member: member_since, home_branch_id
- book: copies_free, copies_total (dropped)

#### Relationships
- book_copy -> book (book_id FK)
- book_copy -> branch (branch_id FK)
- loan -> book_copy (copy_id FK)
- member -> branch (home_branch_id FK)
- member.joined_on renamed to member_since

#### Dependencies
- book_copy depends on book and branch.
- loan depends on book_copy.
- member depends on branch.
