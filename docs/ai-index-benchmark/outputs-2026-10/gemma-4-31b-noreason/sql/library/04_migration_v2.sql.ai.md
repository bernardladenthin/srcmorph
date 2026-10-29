### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T09:53:27Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a library branch system, migrates book copy tracking from aggregate counters to individual records, and updates member and loan associations.

#### Purpose
* Introduce physical library branches and assign book copies to specific locations.
* Transition from book-level copy counts to a granular `book_copy` entity with unique barcodes.
* Link loans and members to specific branches.

#### Type
DDL/DML (PostgreSQL).

#### Schema
* table `branch`:
    * `branch_id SMALLSERIAL` (PK)
    * `name VARCHAR(100)` (NOT NULL, UNIQUE)
    * `city VARCHAR(100)` (NOT NULL)
* table `book_copy`:
    * `copy_id BIGSERIAL` (PK)
    * `book_id BIGINT` (NOT NULL, FK)
    * `branch_id SMALLINT` (NOT NULL, FK)
    * `barcode VARCHAR(32)` (NOT NULL, UNIQUE)
    * `status VARCHAR(12)` (NOT NULL, DEFAULT 'AVAILABLE', CHECK: 'AVAILABLE', 'ON_LOAN', 'LOST')
    * index `idx_book_copy_book` on `book_id`

#### Reads
* `book`: `book_id`, `isbn`, `copies_free`, `copies_total` (used to populate `book_copy`).

#### Writes
* `branch`: INSERT (initial seed data).
* `book_copy`: INSERT (generated from `book` data).
* `loan`: ADD COLUMN `copy_id`.
* `member`: RENAME COLUMN `joined_on` to `member_since`, ADD COLUMN `home_branch_id`, UPDATE `home_branch_id`.
* `book`: DROP CONSTRAINT `book_free_le_total`, DROP COLUMN `copies_free`, DROP COLUMN `copies_total`.

#### Relationships
* `book_copy.book_id` -> `book.book_id`
* `book_copy.branch_id` -> `branch.branch_id`
* `loan.copy_id` -> `book_copy.copy_id`
* `member.home_branch_id` -> `branch.branch_id`

#### Dependencies
* table `book`
* table `loan`
* table `member`
