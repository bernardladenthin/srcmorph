### srcmorph-modelcard
- H: 1.0
- C: 90E4E5FA
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:07:51Z
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
The project combines Java platform error references, a category-organized product catalog with EUR pricing, warehouse inventory, and supplier recalls, and shared customer banking and transfer reference data. Its SQL ledger schema models customer-owned accounts, inter-account transfers, account holds, and daily booked transfer totals.

#### Packages
- java/errors — Provides stable, numeric identifiers and human-readable messages for platform failures across billing, storage, networking, reporting, imports, authentication, and ledger locking.
- java — Provides stable numeric error identifiers and human-readable messages for platform failures across billing, storage, networking, reporting, imports, authentication, and ledger locking.
- other/catalog — Provides a category-organized product catalog with EUR prices, warehouse stock levels, and supplier-recall records.
- other — Organizes a 40-category product catalog with EUR pricing, warehouse inventory, and supplier-recall details for selected products.
- . — Provides shared platform reference data for error handling, product inventory, and customer banking and transfer records.
- sql/ledger — Maintains customer-owned bank accounts, records inter-account transfers and account holds, and summarizes booked transfer volume by day.
- sql — Stores customer bank accounts, account-to-account transfers and holds, and daily booked transfer totals.
