### ai
- H: 1.0
- C: 2787D469
- D: 2026-11-01T22:06:01Z
- T: 2026-11-02T00:09:19Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A PostgreSQL‑based financial ledger package that models customer accounts, account transfers, and daily transaction volumes, providing searchable read‑only queries by customer, account, IBAN, and transaction volume.

## Purpose
- Provides a PostgreSQL schema and SQL queries for managing customer accounts, transfers, and transaction volumes.

## Responsibilities
- `CREATE TABLE` statements for `customer_accounts`, `account_transfers`, and `daily_transaction_volumes`
- SQL queries for retrieving data by customer, account, IBAN, and transaction volume

## Key units
- `CREATE TABLE customer_accounts` (SQL)
- `CREATE TABLE account_transfers` (SQL)
- `CREATE TABLE daily_transaction_volumes` (SQL)
- SQL queries for retrieving data by customer, account, IBAN, and transaction volume

## Data flow
1. Data is inserted into `customer_accounts`, `account_transfers`, and `daily_transaction_volumes` tables.
2. SQL queries are executed against these tables to retrieve specific data based on customer, account, IBAN, or transaction volume.

## Dependencies
- PostgreSQL database

## Cross-cutting
- Immutable SQL schema
- No external dependencies or modules
- No recurring patterns or shared base types/interfaces
