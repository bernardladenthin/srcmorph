### srcmorph-modelcard
- H: 1.0
- C: B8D7E006
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:33:09Z
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
The platform unifies error handling, product inventory, and financial ledger management across its subsystems. Centralized numeric error codes are catalogued in java and java/errors, ensuring consistent billing, storage, network, report, import, and authentication error reporting. Product inventory is exposed through structured XML catalogs in other and other/catalog, providing SKU, title, price, and per‑warehouse stock for lookup and reporting. The PostgreSQL ledger system in sql and sql/ledger stores customers, bank accounts, inter‑account transfers, and a pre‑aggregated daily volume view, tying financial data to the other subsystems.

#### Packages
- java/errors — A centralized catalogue of platform error codes used across the billing, storage, network, report, import, and authentication subsystems.
- java — A unified catalogue of numeric error codes and messages used across the platform’s subsystems.
- other/catalog — A structured XML catalog that lists products by category, including SKU, title, price in EUR, and per‑warehouse inventory stock.
- other — A structured XML catalog that exposes product inventory by category, SKU, and warehouse stock for lookup and reporting.
- . — A unified platform for managing error codes, product inventory, and financial ledger data across multiple subsystems.
- sql/ledger — A PostgreSQL ledger system that stores customers, their bank accounts, and inter‑account transfers, plus a pre‑aggregated daily volume view.
- sql — A PostgreSQL ledger system that stores customers, their bank accounts, and inter‑account transfers, plus a pre‑aggregated daily volume view.
