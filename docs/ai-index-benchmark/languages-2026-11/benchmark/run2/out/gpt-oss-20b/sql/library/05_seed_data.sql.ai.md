### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:42:09Z
- G: 
- A: 0.0.0
- X: file
---
> Seed data for a lending library: populates authors, books, members, and loan records.

#### Purpose
- Populate initial data for a library system.

#### Type
- Data insertion script (PostgreSQL).

#### Writes
- `author`: 15 rows
- `book`: 150 rows
- `member`: 50 rows
- `loan`: 150 rows

#### Dependencies
- None (only inserts into existing tables).
