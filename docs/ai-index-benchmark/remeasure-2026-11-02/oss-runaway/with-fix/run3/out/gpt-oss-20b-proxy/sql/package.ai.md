### sql
- H: 1.0
- C: E8A3AA16
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:38:51Z
- G: 
- A: 0.0.0
- X: package
- F: [ledger/](ledger/package.ai.md)
---
> A PostgreSQL data‑dump that seeds a payments ledger with customers, accounts, holds, and a large volume of transfer transactions, and exposes a view for daily transfer volume.

#### Purpose
- Populate a payments ledger with realistic test data.
- Provide a materialised view (`ledger_daily_volume`) for reporting daily transfer volume.

#### Responsibilities
- **Data seeding**: Insert customers, accounts, holds, and transfers.
- **Reporting**: Generate a daily volume view from transfer data.
- **Indexing**: Create indexes to optimise transfer lookups and hold queries.

#### Key units
- **Tables**  
  - `ledger_customer` – customer metadata.  
  - `ledger_account` – bank accounts linked to customers.  
  - `ledger_transfer` – recorded fund movements between accounts.  
  - `ledger_hold` – holds on accounts with reasons.  
- **View**  
  - `ledger_daily_volume` – aggregates `ledger_transfer` by day.  
- **Indexes**  
  - `idx_transfer_from` – on `ledger_transfer.from_account`.  
  - `idx_transfer_booked` – on `ledger_transfer.booked_at`.  
  - `idx_hold_account` – on `ledger_hold.account_id`.

#### Data flow
1. **Customers** inserted → `ledger_customer`.  
2. **Accounts** created referencing customers → `ledger_account`.  
3. **Transfers** generated between accounts → `ledger_transfer`.  
4. **Holds** applied to accounts → `ledger_hold`.  
5. **View** reads `ledger_transfer` to compute daily volume.

#### Dependencies
- `ledger_account` depends on `ledger_customer`.  
- `ledger_transfer` and `ledger_hold` depend on `ledger_account`.  
- `ledger_daily_volume` reads from `ledger_transfer`.

#### Cross-cutting
- Referential integrity enforced via foreign keys.  
- Indexes target common query patterns: lookup by source account, booking date, and hold account.  
- All amounts stored in cents (`BIGINT`) with a positive check constraint.
