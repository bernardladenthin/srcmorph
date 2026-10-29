### 05_seed_data.sql
- H: 1.0
- C: E9A151DE
- D: 2026-10-28T21:09:11Z
- T: 2026-10-29T00:19:52Z
- G: 
- A: 0.0.0
- X: file
---
> This file seeds a PostgreSQL database with initial data for a lending library system, including authors, books, members, and loan transactions.

#### Purpose
- Populate the database with sample data
- Set up initial state for testing or development

#### Type
SQL Data Manipulation Language (DML) statements within a transaction

#### Schema
No explicit schema definition is provided in the seed file. The existing tables and their columns are assumed to be pre-defined.

#### Reads
This file does not contain any SELECT statements, so it does not read from the database.

#### Writes
The file inserts data into the following tables:
- author
- book
- member
- loan

Each table is written to multiple times, with different combinations of columns.

#### Routine logic
The file contains INSERT statements that directly add rows to the tables, with no complex logic or computations.

#### Dependencies
The seed data relies on the existence of the tables defined in the corresponding CREATE TABLE statements, which are not provided in this file.

## Critical
Only the literal SQL statements within the transaction are indexed. Table structures, constraints, or relationships are not inferred from this source.
