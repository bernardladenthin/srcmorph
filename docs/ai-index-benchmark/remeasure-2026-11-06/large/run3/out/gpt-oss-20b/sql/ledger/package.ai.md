### sql/ledger
- H: 1.0
- C: B4D3E160
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:21:00Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: F37B98E4
- F: [ledger_dump.sql](ledger_dump.sql.ai.md)
---
> A PostgreSQL data‑load script that seeds a payments ledger with customers, accounts, transfers, and holds, and exposes a daily volume view.

#### Purpose
- Seed core ledger tables (`ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`) with bulk test data.  
- Provide a `ledger_daily_volume` view aggregating transfer volume per day.

#### Responsibilities
- **Data population**: bulk INSERTs for customers, accounts, transfers, and holds.  
- **Aggregation**: daily transfer volume view.  
- **Indexing**: performance indexes on transfer source, booking time, and hold account.

#### Key units
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.  
- **View**: `ledger_daily_volume`.  
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. **Customers** → `ledger_customer`.  
2. **Accounts** reference customers → `ledger_account`.  
3. **Transfers** reference accounts (from/to) → `ledger_transfer`.  
4. **Holds** reference accounts → `ledger_hold`.  
5. **Daily volume** aggregates `ledger_transfer.amount_cents` per day → `ledger_daily_volume`.

#### Dependencies
- `ledger_account` FK to `ledger_customer`.  
- `ledger_transfer` FK to `ledger_account` (both `from_account` and `to_account`).  
- `ledger_hold` FK to `ledger_account`.  
- `ledger_daily_volume` depends on `ledger_transfer`.

#### Cross-cutting
- Consistent use of foreign keys for referential integrity.  
- Indexes target common query patterns: transfer source, booking timestamp, and hold account lookups.  
- All data is inserted in bulk via single INSERT statements for efficiency.
