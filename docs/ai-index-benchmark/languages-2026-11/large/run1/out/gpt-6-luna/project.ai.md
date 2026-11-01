### srcmorph-modelcard
- H: 1.0
- C: 77482CA9
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T00:38:29Z
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
The project combines Java platform error-code catalogues covering core service domains and failures such as billing, storage, networking, reporting, importing, and authentication. Product catalog data organizes categorized items with EUR prices and warehouse-specific stock quantities. The SQL ledger models customer-owned bank accounts and timestamped transfers, with daily reporting of transfer volumes and booked amounts.

#### Packages
- java/errors — Defines stable numeric error identifiers and messages for platform failures across billing, storage, networking, reporting, importing, and authentication.
- java — Provides a centralized catalogue of stable numeric error identifiers and messages for platform failures across core service domains.
- other/catalog — Organizes categorized product listings with EUR pricing and warehouse-specific stock quantities.
- other — Represents categorized products with EUR prices and stock quantities tracked by warehouse.
- . — Provides platform error-code reference data, categorized product and warehouse inventory data, and customer account-transfer ledger records with daily volume reporting.
- sql/ledger — Stores customer-owned bank accounts and timestamped transfers, with a daily view of transfer volume.
- sql — Models customer-owned bank accounts and account-to-account transfers, with daily totals for booked transfer amounts.
