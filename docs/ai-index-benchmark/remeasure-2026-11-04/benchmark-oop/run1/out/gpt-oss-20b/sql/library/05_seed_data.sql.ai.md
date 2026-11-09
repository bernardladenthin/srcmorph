### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:26:18Z
- G: 
- A: 0.0.0
- X: file
- P: D53C8943
---
> Seeds the lending library database with authors, books, members, and loan records.

#### Purpose
- Populate core tables with initial data for testing and benchmarking.

#### Type
- PostgreSQL data insertion script (DDL not present).

#### Writes
- `author`: 15 rows  
- `book`: 150 rows  
- `member`: 50 rows  
- `loan`: 150 rows  

#### Dependencies
- None (only inserts into existing tables).
