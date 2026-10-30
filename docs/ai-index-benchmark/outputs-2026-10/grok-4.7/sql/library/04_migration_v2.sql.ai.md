### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:43:42Z
- G: 
- A: 0.0.0
- X: file
---
The user wants the SQL migration file indexed according to a specific format.

The migration creates a branch table with a Main branch in Springfield. It introduces a book_copy table and indexes it by book_id. Existing copies move from the book table into book_copy. The loan table gains a copy_id column. Member.joined_on is renamed to member_since, and home_branch_id is added with a default of 1. The per-book copy counters and their constraint are dropped.

The output will follow the exact format requested. It will be marked as a PostgreSQL migration script with DDL and DML operations. Schema sections will list the tables created and altered. Reads will include the book table columns copies_free and copies_total, plus member.home_branch_id.

Writes target branch, book_copy, loan, member, and book. Key relationships include book_copy to book and branch, loan.copy_id to book_copy, and member.home_branch_id to branch. Dependencies include the existing book, loan, member tables, plus generate_series. Routine logic is omitted since this is a script rather than a procedure or trigger.

The branch table has a unique name and city. The book_copy table adds barcode, status, and a check constraint on AVAILABLE, ON_LOAN, or LOST.> Lending-library migration that introduces branches, tracks each physical book copy at a branch, and retires per-book copy counters.

#### Purpose
- PostgreSQL schema migration v2 for a lending library: add branches, store one row per physical copy, and point loans and members at branch/copy identity.
- Backfill existing books into `book_copy` at the Main branch, then drop the old copy counters on `book`.

#### Type
- Migration script (DDL plus DML): `CREATE TABLE`, `INSERT`, `ALTER TABLE`, `UPDATE`, `CREATE INDEX`. Dialect: PostgreSQL (`SMALLSERIAL`, `BIGSERIAL`, `LATERAL`, `generate_series`).

#### Schema
- `branch`: `branch_id SMALLSERIAL` PRIMARY KEY; `name VARCHAR(100)` NOT NULL UNIQUE; `city VARCHAR(100)` NOT NULL.
- `book_copy`: `copy_id BIGSERIAL` PRIMARY KEY; `book_id BIGINT` NOT NULL REFERENCES `book (book_id)`; `branch_id SMALLINT` NOT NULL REFERENCES `branch (branch_id)`; `barcode VARCHAR(32)` NOT NULL UNIQUE; `status VARCHAR(12)` NOT NULL DEFAULT `'AVAILABLE'` CHECK (`status` IN (`'AVAILABLE'`, `'ON_LOAN'`, `'LOST'`)).
- Index `idx_book_copy_book` on `book_copy (book_id)`.
- `loan.copy_id BIGINT` REFERENCES `book_copy (copy_id)` (added; nullability not stated).
- `member.joined_on` renamed to `member_since`; `member.home_branch_id SMALLINT` REFERENCES `branch (branch_id)` (added).
- Dropped from `book`: constraint `book_free_le_total`, columns `copies_free`, `copies_total`.

#### Reads
- `book`: `book_id`, `isbn`, `copies_free`, `copies_total` (backfill `SELECT` with `generate_series(1, copies_total)`).
- `member`: `home_branch_id` (filter `WHERE home_branch_id IS NULL`).

#### Writes
- `INSERT` `branch` (`name`, `city`): `('Main', 'Springfield')`.
- `INSERT` `book_copy` (`book_id`, `branch_id`, `barcode`, `status`): one row per existing copy, `branch_id` 1, barcode `isbn || '-' || n`, status `AVAILABLE` when `n <= copies_free` else `ON_LOAN`.
- `ALTER` `loan` add `copy_id`; `ALTER` `member` rename `joined_on` to `member_since` and add `home_branch_id`.
- `UPDATE` `member` set `home_branch_id = 1` where null.
- `ALTER` `book` drop `book_free_le_total`, `copies_free`, `copies_total`.

#### Relationships
- `book_copy.book_id -> book.book_id`
- `book_copy.branch_id -> branch.branch_id`
- `loan.copy_id -> book_copy.copy_id`
- `member.home_branch_id -> branch.branch_id`
- Backfill joins each `book` row to series `g(n)` and assigns branch id `1` (Main).

#### Dependencies
- Tables: `book`, `loan`, `member`, `branch`, `book_copy`.
- Constraint: `book_free_le_total`.
- Function: `generate_series`.
- Index: `idx_book_copy_book`.
