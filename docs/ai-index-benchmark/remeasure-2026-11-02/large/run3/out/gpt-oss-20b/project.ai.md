### srcmorph-modelcard
- H: 1.0
- C: F1E8209B
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:11:07Z
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
The srcmorph‑modelcard project unifies error handling, product catalog management, and payments ledger analytics. Centralized error catalogs in java and java/errors provide fast numeric lookup for billing, storage, network, report, import, auth, and ledger lock failures. Product data is defined in structured XML catalogs (other and other/catalog) that list items, prices, and per‑warehouse inventory for an e‑commerce platform. A PostgreSQL ledger schema (sql and sql/ledger) seeds realistic test data and exposes a daily transfer volume view, enabling analytics across the platform.

#### Packages
- java/errors — A central catalogue of platform error codes with numeric IDs, messages, and fast lookup by code.
- java — A centralized catalogue of platform error codes with fast numeric lookup for billing, storage, network, report, import, auth, and ledger lock failures.
- other/catalog — A catalog XML file that defines product listings, prices, and warehouse inventory levels for an e‑commerce platform.
- other — A structured XML catalog that defines products, prices, and per‑warehouse inventory for an e‑commerce platform.
- . — A unified platform for managing error codes, product catalog data, and a payments ledger with analytics.
- sql/ledger — A PostgreSQL data‑dump that defines a payments ledger schema, seeds it with realistic test data, and exposes a daily transfer volume view.
- sql — A PostgreSQL data‑dump that creates a fully populated payments ledger schema, seeds it with realistic test data, and exposes a daily transfer volume view for analytics.
