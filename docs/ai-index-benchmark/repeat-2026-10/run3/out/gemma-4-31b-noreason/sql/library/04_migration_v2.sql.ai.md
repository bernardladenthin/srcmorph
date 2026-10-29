### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:29:40Z
- G: 
- A: 0.0.0
- X: file
---
> Migrates lending library schema to support multi-branch management, individual book copy tracking, and member home branch assignments.

#### Purpose
* Introduce physical library branches and track specific book copies per branch.
* Transition from aggregate book counters to individual copy records.
* Link loans and members to specific branches and copies.

#### Type
DDL/DML (PostgreSQL)

#### Schema
* table `branch`
    * `branch_id` SMALLSERIAL (PK)
    * `name` VARCHAR(100) NOT NULL UNIQUE
    * `city` VARCHAR(100) NOT NULL
* table `book_copy`
    * `copy_id` BIGSERIAL (PK)
    * `book_id` BIGINT NOT NULL (FK)
    * `branch_id` SMALLINT NOT NULL (FK)
    * `barcode` VARCHAR(32) NOT NULL UNIQUE
    * `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' (CHECK: 'AVAILABLE', 'ON_LOAN', 'LOST')
    * Index `idx_book_copy_book` on `book_id`
* table `loan` (modified)
    * `copy_id` BIGINT (FK)
* table `member` (modified)
    * `member_since` (renamed from `joined_on`)
    * `home_branch_id` SMALLINT (FK)
* table `book` (modified)
    * Removed `copies_free`
    * Removed `copies_total`
    * Removed constraint `book_free_le_total`

#### Reads
* `book`: `book_id`, `isbn`, `copies_free`, `copies_total` (used to populate `book_copy`)

#### Writes
* `branch`: INSERT (initial seed data)
* `book_copy`: INSERT (generated from `book` data)
* `loan`: ADD COLUMN `copy_id`
* `member`: RENAME COLUMN `joined_on` to `member_since`, ADD COLUMN `home_branch_id`, UPDATE `home_branch_id`
* `book`: DROP COLUMN `copies_free`, `copies_total`, DROP CONSTRAINT `book_free_le_total`

#### Relationships
* `book_copy.book_id` -> `book.book_id`
* `book_copy.branch_id` -> `branch.branch_id`
* `loan.copy_id` -> `book_copy.copy_id`
* `member.home_branch_id` -> `branch.branch_id`

#### Dependencies
* `book` table
* `loan` table
* `member` table
* `generate_series` (PostgreSQL function)
