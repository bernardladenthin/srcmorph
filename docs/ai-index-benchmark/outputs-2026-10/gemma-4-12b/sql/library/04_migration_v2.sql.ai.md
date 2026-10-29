### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T04:05:39Z
- G: 
- A: 0.0.0
- X: file
---
> This script migrates the lending library schema to a branch-based inventory system where individual book copies are tracked per location.

#### Purpose
*   Introduces a `branch` entity to manage physical locations.
*   Transitions from aggregate copy counts to individual `book_copy` records.
*   Links loans and members to specific branches and copies.

#### Type
Table, Index, Alteration (PostgreSQL)

#### Schema
#### branch
*   branch_id SMALLSERIAL PRIMARY KEY
*   name VARCHAR(100) NOT NULL UNIQUE
*   city VARCHAR(100) NOT NULL

#### book_copy
*   copy_id BIGSERIAL PRIMARY KEY
*   book_id BIGINT NOT NULL (FK)
*   branch_id SMALLINT NOT NULL (FK)
*   barcode VARCHAR(32) NOT NULL UNIQUE
*   status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))
*   INDEX: idx_book_copy_book (book_id)

#### Writes
*   `branch`: INSERT new branch record.
*   `book_copy`: INSERT records generated from `book` counts.
*   `loan`: ALTER ADD `copy_id` column.
*   `member`: RENAME `joined_on` to `member_since`, ADD `home_branch_id` column, UPDATE `home_branch_id`.
*   `book`: DROP `book_free_le_total` constraint, DROP `copies_free` column, DROP `copies_total` column.

#### Relationships
*   book_copy.book_id -> book.book_id
*   book_copy.branch_id -> branch.branch_id
*   loan.copy_id -> book_copy.copy_id
*   member.home_branch_id -> branch.branch_id
