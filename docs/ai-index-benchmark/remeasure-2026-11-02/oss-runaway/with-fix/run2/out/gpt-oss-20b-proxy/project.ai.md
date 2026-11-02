### srcmorph-modelcard
- H: 1.0
- C: E37EEE62
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:35:24Z
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
The project centers on a unified data‑access layer that maps platform error codes, e‑commerce catalog inventory, and payment‑ledger records into immutable, queryable structures for consistent business logic and analytics. Central error handling is provided by a Java package that defines immutable mappings of numeric error codes to descriptive messages, ensuring consistent error handling across the system. The e‑commerce catalog is organized in a structured XML format, grouping products by category and including SKU, title, EUR price, and warehouse‑specific stock levels. Ledger data is stored in a PostgreSQL database, recording customers, accounts, transfers, and fraud holds, and exposes a daily volume view for analytics.

#### Packages
- java/errors — Centralizes platform error codes, mapping numeric identifiers to descriptive messages for consistent error handling across the system.
- java — Provides a centralized, immutable mapping of platform error codes to descriptive messages for consistent error handling across the system.
- other/catalog — A structured XML catalog that organizes e‑commerce products by category, listing SKU, title, EUR price, and warehouse‑specific stock levels.
- other — A structured XML catalog that organizes e‑commerce products by category, listing SKU, title, EUR price, and warehouse‑specific stock levels.
- . — A unified data‑access layer that maps platform error codes, e‑commerce catalog inventory, and payment‑ledger records into immutable, queryable structures for consistent business logic and analytics.
- sql/ledger — A PostgreSQL ledger that stores customers, accounts, transfers, and fraud holds, and exposes a daily volume view.
- sql — A PostgreSQL ledger that records customers, accounts, transfers, and fraud holds, and provides daily transfer volume analytics.
