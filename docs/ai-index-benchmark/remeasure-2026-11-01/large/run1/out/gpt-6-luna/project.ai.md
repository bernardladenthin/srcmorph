### srcmorph-modelcard
- H: 1.0
- C: 57AC0F17
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:34:34Z
- G: 
- A: 0.0.0
- X: project
- F: [java/errors](java/errors/package.ai.md)
- F: [java](java/package.ai.md)
- F: [other/catalog](other/catalog/package.ai.md)
- F: [other](other/package.ai.md)
- F: [.](package.ai.md)
- F: [sql/ledger](sql/ledger/package.ai.md)
- F: [sql](sql/package.ai.md)
---
> Project index of srcmorph-modelcard: one line per package; leads here, clickable links in the header F list.

#### Overview
The project combines platform error-code lookups with a versioned product catalog covering category listings, EUR prices, warehouse inventory, and supplier recalls. Its SQL ledger subsystem stores customers, bank accounts, and transfers, supports fraud-review holds, and aggregates transfer volume by day.

#### Packages
- java/errors — Provides stable numeric identifiers and human-readable messages for platform billing, storage, network, reporting, import, authentication, and ledger-lock errors.
- java — Provides a searchable catalogue of platform error codes and their human-readable messages across billing, storage, networking, reporting, imports, authentication, and ledger locking.
- other/catalog — Provides a versioned product catalog with category-grouped listings, EUR prices, warehouse inventory, and supplier-recall information.
- other — Maintains a versioned product catalog with category-based listings, EUR pricing, warehouse stock, and supplier-recall details.
- . — Defines platform error identifiers alongside product catalog, inventory, and customer banking-ledger data for operational lookup and reporting.
- sql/ledger — Stores customer and bank-account records, captures account-to-account transfers, flags accounts for fraud review, and aggregates transfer volume by day.
- sql — Maintains customer bank accounts and transfer records, supports fraud-review holds, and reports transfer totals by day.
