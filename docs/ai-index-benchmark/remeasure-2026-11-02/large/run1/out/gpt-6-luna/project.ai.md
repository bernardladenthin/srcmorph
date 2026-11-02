### srcmorph-modelcard
- H: 1.0
- C: 6B8F88E0
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:37:02Z
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
The project combines platform reference data for stable error codes and product catalogs, including EUR pricing, warehouse stock, and supplier-recall details. Its SQL ledger subsystem maintains customer accounts and transfers, supports fraud-review holds, and summarizes transfer volumes by booking date.

#### Packages
- java/errors — Provides a stable numeric catalogue of platform failure codes and messages for billing, storage, network, reporting, import, authentication, and ledger-lock domains.
- java — Provides a centralized catalogue for identifying platform failures by stable numeric code and descriptive message.
- other/catalog — Provides a categorized product catalog with EUR pricing, warehouse stock levels, and supplier-recall records for selected categories.
- other — Organizes a 5,000-item product catalog with category-based EUR pricing, warehouse inventory, and supplier-recall details for selected products.
- . — Provides reference data for platform errors and products alongside customer-account, transfer, fraud-hold, and daily-volume records.
- sql/ledger — Maintains customer bank-account records, account-to-account transfers, fraud-review holds, and date-based transfer-volume summaries.
- sql — Maintains customer bank accounts and inter-account transfers, supports fraud-review holds, and summarizes transfer volumes by booking date.
