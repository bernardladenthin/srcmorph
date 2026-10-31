### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:29:42Z
- G: 
- A: 0.0.0
- X: file
---
> Lending library schema migration v2 adds branch and book_copy tables, moves per-book copy counters into book_copy, and retires book copies_free and copies_total columns.

#### Purpose
- Adds branch and book_copy tables for branch-scoped book copies
- Moves per-book copy counters from book to book_copy
- Renames member joined_on to member_since and adds home_branch_id
- Adds copy_id to loan for copy-level lending

#### Type
SQL migration script (PostgreSQL)

#### Schema
- branch branch_id SMALLSERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE, city VARCHAR(100) NOT NULL
- book_copy copy_id BIGSERIAL PRIMARY KEY, book_id BIGINT NOT NULL REFERENCES book (book_id), branch_id SMALLINT NOT NULL REFERENCES branch (branch_id), barcode VARCHAR(32) NOT NULL UNIQUE, status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))
- loan copy_id BIGINT REFERENCES book_copy (copy_id)
- member member_since VARCHAR (renamed from joined_on), home_branch_id SMALLINT REFERENCES branch (branch_id)
- book book_id BIGINT PRIMARY KEY (implied), copies_free and copies_total dropped

#### Reads
- book book_id, copies_total, copies_free, isbn
- branch branch_id
- generate_series(n)
- member joined_on (renamed)

#### Writes
- branch branch_id, name, city
- book_copy copy_id, book_id, branch_id, barcode, status
- loan copy_id
- member home_branch_id, member_since

#### Relationships
- book_copy.book_id -> book.book_id
- book_copy.branch_id -> branch.branch_id
- loan.copy_id -> book_copy.copy_id
- member.home_branch_id -> branch.branch_id

#### Routine logic
- branch insert: Main Springfield
- book_copy insert: one row per existing copy at main branch using generate_series(1, b.copies_total)
- status derived: AVAILABLE if g.n <= b.copies_free else ON_LOAN
- member home_branch_id set to 1 where NULL

#### Dependencies
- book, branch, loan, member, generate_series
