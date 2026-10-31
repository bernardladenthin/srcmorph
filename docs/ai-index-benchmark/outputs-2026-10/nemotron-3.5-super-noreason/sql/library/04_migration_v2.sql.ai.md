### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:02:41Z
- G: 
- A: 0.0.0
- X: file
---
> Models physical book copies assigned to library branches, retires per-book copy counters, and links loans and members to copies and branches.

#### Purpose
- Migrates the lending library schema from per-book copy counters to physical copies tracked per branch.
- Populates one book_copy row per existing copy at the main branch and assigns all members to the main branch as home branch.

#### Type
PostgreSQL DDL/DML migration script executed inside a transaction.

#### Schema
- branch: branch_id SMALLSERIAL PRIMARY KEY, name VARCHAR(100) NOT NULL UNIQUE, city VARCHAR(100) NOT NULL
- book_copy: copy_id BIGSERIAL PRIMARY KEY, book_id BIGINT NOT NULL REFERENCES book (book_id), branch_id SMALLINT NOT NULL REFERENCES branch (branch_id), barcode VARCHAR(32) NOT NULL UNIQUE, status VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE' CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))
- loan: adds copy_id BIGINT REFERENCES book_copy (copy_id)
- member: renames joined_on to member_since, adds home_branch_id SMALLINT REFERENCES branch (branch_id)
- book: drops constraint book_free_le_total, drops columns copies_free and copies_total
- Index: idx_book_copy_book on book_copy (book_id)

#### Reads
- book (book_id, isbn, copies_free, copies_total)
- generate_series (1, b.copies_total)
- member (home_branch_id)

#### Writes
- branch (name, city) INSERT
- book_copy (book_id, branch_id, barcode, status) INSERT
- loan (copy_id) ALTER ADD COLUMN
- member (joined_on renamed to member_since, home_branch_id added, home_branch_id set to 1)
- book (copies_free dropped, copies_total dropped, constraint book_free_le_total dropped)

#### Relationships
- book_copy.book_id -> book.book_id
- book_copy.branch_id -> branch.branch_id
- loan.copy_id -> book_copy.copy_id
- member.home_branch_id -> branch.branch_id

#### Routine logic
- INSERT into branch with name 'Main' and city 'Springfield'
- INSERT into book_copy selecting each book, branch_id 1, barcode built from isbn and series number, status AVAILABLE when series number <= copies_free else ON_LOAN
- UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL

#### Dependencies
- book
- loan
- member
- branch
- book_copy
- generate_series
