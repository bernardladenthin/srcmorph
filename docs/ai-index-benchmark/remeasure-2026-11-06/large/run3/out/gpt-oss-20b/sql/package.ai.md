### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:21:02Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: 43E15205
- F: [ledger/](ledger/package.ai.md)
---
> A PostgreSQL data‑load package that seeds a payments ledger with customers, accounts, transfers, and holds, and exposes a daily transfer volume view.

#### Purpose
- Populate core ledger tables with bulk test data.  
- Provide a daily volume aggregation view.

#### Responsibilities
- **Data population**: bulk INSERTs for customers, accounts, transfers, and holds.  
- **Aggregation**: compute daily transfer volume.  
- **Indexing**: create performance indexes on common query columns.

#### Key units
- **Tables**: `ledger_customer`, `ledger_account`, `ledger_transfer`, `ledger_hold`.  
- **View**: `ledger_daily_volume`.  
- **Indexes**: `idx_transfer_from`, `idx_transfer_booked`, `idx_hold_account`.

#### Data flow
1. Insert customers → `ledger_customer`.  
2. Insert accounts referencing customers → `ledger_account`.  
3. Insert transfers referencing accounts → `ledger_transfer`.  
4. Insert holds referencing accounts → `ledger_hold`.  
5. View aggregates `ledger_transfer.amount_cents` per day → `ledger_daily_volume`.

#### Dependencies
- `ledger_account` FK to `ledger_customer`.  
- `ledger_transfer` FK to `ledger_account` (both `from_account` and `to_account`).  
- `ledger_hold` FK to `ledger_account`.  
- `ledger_daily_volume` depends on `ledger_transfer`.

#### Cross-cutting
- Consistent foreign‑key enforcement across tables.  
- Indexes target transfer source, booking timestamp, and hold account lookups.  
- Bulk INSERTs used for efficient data loading.
