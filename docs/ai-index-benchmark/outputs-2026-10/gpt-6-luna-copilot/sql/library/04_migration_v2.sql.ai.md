### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:36:14Z
- G: 
- A: 0.0.0
- X: file
---
> Adds library branches, assigns existing book copies to the main branch, links loans and members to branch-specific data, and removes book-level copy counters.

#### Purpose
- Migrate lending-library inventory from book-level copy counts to individual branch-specific copies.
- Add branch assignment for loans and members.

#### Type
- Schema migration (PostgreSQL; DDL/DML)

#### Schema
- `branch`: `branch_id SMALLSERIAL` (PK), `name VARCHAR(100)` (NOT NULL, UNIQUE), `city VARCHAR(100)` (NOT NULL).
- `book_copy`: `copy_id BIGSERIAL` (PK), `book_id BIGINT` (NOT NULL, FK), `branch_id SMALLINT` (NOT NULL, FK), `barcode VARCHAR(32)` (NOT NULL, UNIQUE), `status VARCHAR(12)` (NOT NULL, DEFAULT `'AVAILABLE'`, CHECK: `AVAILABLE`, `ON_LOAN`, `LOST`).
- `loan`: adds `copy_id BIGINT` (FK to `book_copy.copy_id`).
- `member`: renames `joined_on` to `member_since`; adds `home_branch_id SMALLINT` (FK to `branch.branch_id`).
- `book`: drops `copies_free`, `copies_total`, and constraint `book_free_le_total`.
- Index: `idx_book_copy_book` on `book_copy(book_id)`.

#### Reads
- `book`: `book_id`, `isbn`, `copies_total`, `copies_free` (copy migration query).

#### Writes
- `branch`: inserts `('Main', 'Springfield')`.
- `book_copy`: inserts `book_id`, `branch_id`, `barcode`, `status`; one row per generated copy, assigned to branch `1`.
- `loan`: adds `copy_id`.
- `member`: renames `joined_on`; adds `home_branch_id`; sets null `home_branch_id` values to `1`.
- `book`: drops copy-count columns and `book_free_le_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`

#### Dependencies
- Tables: `book`, `loan`, `member`
- Function: `generate_series`
