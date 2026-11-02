### srcmorph-modelcard
- H: 1.0
- C: 7F1CE502
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:39:00Z
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
The project centers on a unified data‑management layer that catalogs platform error codes, e‑commerce product inventory, and payment ledger records, exposing lookup APIs, XML schemas, and SQL views for reporting. The error‑code subsystem provides a centralized catalogue mapping numeric IDs to human‑readable messages with reverse lookup support. The e‑commerce subsystem stores product listings, pricing, inventory, and recall information in a structured XML catalog. The payments subsystem seeds a PostgreSQL ledger with customers, accounts, holds, and transfer transactions, and exposes a view for daily transfer volume.

#### Packages
- java/errors — A centralized catalogue of platform error codes that maps numeric IDs to human‑readable messages and supports reverse lookup.
- java — A centralized catalogue of platform error codes that maps numeric IDs to human‑readable messages and supports reverse lookup.
- other/catalog — A structured XML catalog that stores product listings, pricing, inventory, and recall information for an e‑commerce system.
- other — A structured XML catalog that stores product listings, pricing, inventory, and recall information for an e‑commerce system.
- . — A unified data‑management layer that catalogs platform error codes, e‑commerce product inventory, and payment ledger records, exposing lookup APIs, XML schemas, and SQL views for reporting.
- sql/ledger — A PostgreSQL data‑dump that seeds a payments ledger with customers, accounts, holds, and a large volume of transfer transactions, and exposes a view for daily transfer volume.
- sql — A PostgreSQL data‑dump that seeds a payments ledger with customers, accounts, holds, and a large volume of transfer transactions, and exposes a view for daily transfer volume.
