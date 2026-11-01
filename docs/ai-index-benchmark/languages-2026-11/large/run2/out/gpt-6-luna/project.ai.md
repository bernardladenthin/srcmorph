### srcmorph-modelcard
- H: 1.0
- C: 9458FBF8
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:17:40Z
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
The project combines a numeric platform-error catalogue with versioned product and inventory data organized by category, including EUR prices and warehouse-specific stock. Its SQL ledger stores customer-owned bank accounts and booked transfers, with transfer volume aggregated by booking date.

#### Packages
- java/errors — Provides a stable numeric catalogue for identifying and describing platform failures across billing, storage, networking, reporting, imports, and authentication.
- java — Provides a numeric catalogue for identifying platform errors and retrieving their human-readable descriptions.
- other/catalog — Organizes versioned product and inventory data by category, with EUR pricing and warehouse-specific stock quantities.
- other — Maintains a versioned product catalog with category-based product details, EUR prices, and warehouse-specific inventory quantities.
- . — Defines platform error lookup, versioned product inventory, and a customer-owned bank ledger with daily transfer reporting.
- sql/ledger — Stores customer-owned bank accounts and booked transfers, with daily aggregation of transfer volume.
- sql — Records customer-owned bank accounts and booked account transfers, and reports transfer volume by booking date.
