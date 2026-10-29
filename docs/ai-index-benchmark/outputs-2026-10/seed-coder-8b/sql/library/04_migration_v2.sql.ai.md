### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:19:29Z
- G: 
- A: 0.0.0
- X: file
---
> Manages lending library data: creates branches, tracks book copies, updates schema, and maintains relationships between books, members, and branches.

#### Purpose
- Adds branch management to the lending library system
- Moves book copy tracking to a per-branch model
- Updates member and book schemas
- Retires per-book copy counters

#### Type
Procedure / SQL script (PostgreSQL)

#### Schema
##### branch
- branch_id SMALLSERIAL PRIMARY KEY
- name VARCHAR(100) NOT NULL UNIQUE
- city VARCHAR(100) NOT NULL

##### book_copy
- copy_id BIGSERIAL PRIMARY KEY
- book_id BIGINT NOT NULL
- branch_id SMALLINT NOT NULL
- barcode VARCHAR(32) NOT NULL UNIQUE
- status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'

#### Writes
- branch (INSERT)
- book_copy (INSERT, UPDATE)
- loan (ALTER COLUMN)
- member (RENAME COLUMN, ALTER COLUMN, UPDATE)
- book (ALTER TABLE, DROP CONSTRAINT, DROP COLUMN)

#### Relationships
- book.book_id -> book_copy.book_id
- branch.branch_id -> book_copy.branch_id
- member.home_branch_id -> branch.branch_id

#### Routine logic
- Creates new branch table
- Inserts initial main branch
- Creates book_copy table
- Migrates existing book copies to new model
- Updates loan table to include copy_id
- Renames and updates member table
- Alters book table schema
- Creates index on book_copy.book_id

#### Dependencies
- book table
- loan table
- member table
