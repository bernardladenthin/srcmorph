### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:47:31Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> Migration script that introduces a branch hierarchy, moves book copies to branches, renames a member column, adds a home branch reference, and removes per‑book copy counters.

#### Purpose
- Adds a `branch` table and moves `book_copy` rows to branches instead of per‑book counters.
- Renames `member.joined_on` to `member_since` and adds `member.home_branch_id`.
- Removes `book.copies_free` and `book.copies_total` columns.
- Creates an index on `book_copy.book_id`.

#### Type
SQL script (PostgreSQL) containing DDL and DML statements.

#### Schema
*branch*
- `branch_id` SMALLSERIAL PRIMARY KEY
- `name` VARCHAR(100) NOT NULL UNIQUE
- `city` VARCHAR(100) NOT NULL

*book_copy*
- `copy_id` BIGSERIAL PRIMARY KEY
- `book_id` BIGINT NOT NULL REFERENCES `book`(`book_id`)
- `branch_id` SMALLINT NOT NULL REFERENCES `branch`(`branch_id`)
- `barcode` VARCHAR(32) NOT NULL UNIQUE
- `status` VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (`status` IN ('AVAILABLE', 'ON_LOAN', 'LOST'))

*loan* (modified)
- `copy_id` BIGINT REFERENCES `book_copy`(`copy_id`) added

*member* (modified)
- `home_branch_id` SMALLINT NOT NULL REFERENCES `branch`(`branch_id`) added
- `joined_on` renamed to `member_since` (no column listed here; rename performed)

*book* (modified)
- `copies_free` and `copies_total` dropped

Indexes
- `idx_book_copy_book` on `book_copy.book_id`

#### Reads
- `book` (in the `INSERT` for `book_copy` and in `REFERENCES book`)
- `branch` (in `INSERT` for `branch`, referenced by `book_copy.branch_id`)
- `member` (in `UPDATE` setting `home_branch_id`)
- `loan` (in `ALTER TABLE loan ADD COLUMN copy_id`)

#### Writes
- `branch` (INSERT row `('Main', 'Springfield')`)
- `book_copy` (CREATE table, INSERT rows)
- `loan` (ALTER TABLE adding `copy_id`)
- `member` (ALTER TABLE renaming `joined_on` and adding `home_branch_id`; UPDATE setting `home_branch_id = 1`)
- `book` (ALTER TABLE dropping constraints and columns)
- `idx_book_copy_book` (CREATE INDEX)

#### Relationships
- `book_copy.book_id` → `book.book_id`
- `book_copy.branch_id` → `branch.branch_id`
- `loan.copy_id` → `book_copy.copy_id` (after addition)
- `member.home_branch_id` → `branch.branch_id`

#### Routine logic
- Creates `branch` table and inserts default `'Main'` branch.
- Creates `book_copy` table with `barcode` generated from `book.isbn` and series `1..copies_total`, status set based on `copies_free`.
- Inserts initial copies for each book using `generate_series`.
- Adds `copy_id` foreign key to `loan`.
- Renames `member.joined_on` to `member_since`.
- Adds `member.home_branch_id` foreign key, sets all members’ `home_branch_id` to `1` (main branch).
- Drops `book.copies_free` constraint and columns `copies_free`, `copies_total` from `book`.
- Creates index `idx_book_copy_book` on `book_copy.book_id`.

#### Dependencies
- `book` (existing table referenced by `book_copy` and dropped columns)
- `branch` (referenced by `book_copy` and `member`)
- `loan` (receives `copy_id`)
- `member` (receives `home_branch_id`, column renamed)
- PostgreSQL lateral `generate_series` function (used in INSERT)
