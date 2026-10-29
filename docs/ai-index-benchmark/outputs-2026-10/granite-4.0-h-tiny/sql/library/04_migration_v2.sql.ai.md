### 04_migration_v2.sql
- H: 1.0
- C: E2357141
- D: 2026-10-28T21:08:39Z
- T: 2026-10-29T00:08:55Z
- G: 
- A: 0.0.0
- X: file
---
```sql
-- SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
--
-- SPDX-License-Identifier: Apache-2.0
--
-- Lending library: schema migration v2 (PostgreSQL). Adds branches, moves copies per branch,
-- renames a column and retires the per-book copy counters.

BEGIN;

CREATE TABLE branch (
    branch_id SMALLSERIAL PRIMARY KEY,
    name      VARCHAR(100) NOT NULL UNIQUE,
    city      VARCHAR(100) NOT NULL
);

INSERT INTO branch (name, city) VALUES ('Main', 'Springfield');

CREATE TABLE book_copy (
    copy_id   BIGSERIAL PRIMARY KEY,
    book_id   BIGINT NOT NULL REFERENCES book (book_id),
    branch_id SMALLINT NOT NULL REFERENCES branch (branch_id),
    barcode   VARCHAR(32) NOT NULL UNIQUE,
    status    VARCHAR(12) NOT NULL DEFAULT 'AVAILABLE'
              CHECK (status IN ('AVAILABLE', 'ON_LOAN', 'LOST'))
);

-- One row per existing copy, all at the main branch.
INSERT INTO book_copy (book_id, branch_id, barcode, status)
SELECT b.book_id,
       1,
       b.isbn || '-' || g.n,
       CASE WHEN g.n <= b.copies_free THEN 'AVAILABLE' ELSE 'ON_LOAN' END
FROM book b
CROSS JOIN LATERAL generate_series(1, b.copies_total) AS g(n);

ALTER TABLE loan ADD COLUMN copy_id BIGINT REFERENCES book_copy (copy_id);
ALTER TABLE member RENAME COLUMN joined_on TO member_since;
ALTER TABLE member ADD COLUMN home_branch_id SMALLINT REFERENCES branch (branch_id);
UPDATE member SET home_branch_id = 1 WHERE home_branch_id IS NULL;

ALTER TABLE book DROP CONSTRAINT book_free_le_total;
ALTER TABLE book DROP COLUMN copies_free;
ALTER TABLE book DROP COLUMN copies_total;

CREATE INDEX idx_book_copy_book ON book_copy (book_id);

COMMIT;
```
