### srcmorph-modelcard
- H: 1.0
- C: A37EECBF
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:13:16Z
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
The project combines Java error-code catalogues with shared platform reference data and product catalogs covering EUR pricing, warehouse inventory, and supplier recalls. Its SQL ledger subsystem models customer-owned bank accounts, transfers, and holds, and reports daily transfer volumes; seeded ledger records are also provided.

#### Packages
- java/errors — Provides a stable numeric error-code catalogue for platform billing, storage, networking, reporting, imports, authentication, and ledger-lock failures.
- java — Provides a stable numeric error-code catalogue for platform failures, with lookup from numeric codes to their corresponding error identifiers.
- other/catalog — Defines a versioned product catalog with categorized product details, EUR prices, warehouse inventory, and supplier recall information.
- other — Defines a categorized product catalog with EUR pricing, warehouse stock, and optional supplier recall details.
- . — Provides shared platform reference data for error handling, product inventory, and customer banking activity.
- sql/ledger — Defines customer-owned bank accounts, money transfers, and account holds, with daily transfer-volume reporting and seeded ledger records.
- sql — Stores customer-owned bank accounts, account-to-account transfers, and holds, with daily transfer-volume reporting.
