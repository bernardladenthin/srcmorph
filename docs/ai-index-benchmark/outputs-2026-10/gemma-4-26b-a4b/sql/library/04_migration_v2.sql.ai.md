### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:50:50Z
- G: 
- A: 0.0.0
- X: file
---
> Evolves the lending library schema to support multi-branch operations by introducing branches, relocating book copies to specific branches, and migrating book inventory tracking from book-level counters to individual copy records.

#### Purpose
* Introduces a branch management system.
* Normalizes book inventory by moving copy counts from the `book` table to a new `book_copy` table.
* Links members to a home branch.

#### Type
PostgreSQL DDL (Migration script).

#### Schema
**branch**
* `branch_id` SMALLSERIAL (PK)
* `name` VARCHAR(100) (NOT NULL, UNIQUE)
* `city` VARCHAR(100) (NOT NULL)

**book_copy**
* `copy_id` BIGSERIAL (PK)
* `book_id` BIGINT (NOT NULL, FK)
* `branch_id` SMALLINT (NOT NULL, FK)
* `barcode` VARCHAR(32) (NOT NULL, UNIQUE)
* `status` VARCHAR(12) (NOT NULL, DEFAULT 'AVAILABLE', CHECK status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))
* INDEX `idx_book_copy_book` ON `book_id`

#### Reads
* `book.book_id`
* `book.isbn`
* `book.copies_free`
* `book.copies_total`
* `book.copies_le_total` (constraint)

#### Writes
* `branch` (INSERT)
* `book_copy` (INSERT)
* `loan` (ALTER/ADD COLUMN `copy_id`)
* `member` (RENAME COLUMN `joined_on` to `member_since`, ADD COLUMN `home_branch_id`, UPDATE `home_branch_id`)
* `book` (DROP CONSTRAINT `book_free_le_total`, DROP COLUMN `copies_free`, DROP COLUMN `copies_total`)

#### Relationships
* `book_copy.book_id` -> `book.book_id`
* `book_copy.branch_id` -> `branch.branch_id`
* `loan.copy_id` -> `book_copy.copy_id`
* `member.home_branch_id` -> `branch.branch_id`

#### Routine logic
* **Inventory Migration**: Generates `book_copy` rows for every existing book using `generate_series`.
* **Status Logic**: Assigns 'AVAILABLE' status if the series index `n` is less than or equal to `book.copies_free`, otherwise 'ON_LOAN'.
* **Barcode Generation**: Constructs barcodes using the pattern `isbn-n`.
* **Member Defaulting**: Sets all existing members' `home_branch_id` to the 'Main' branch (ID 1).

#### Dependencies
* `book`
* `loan`
* `member`
