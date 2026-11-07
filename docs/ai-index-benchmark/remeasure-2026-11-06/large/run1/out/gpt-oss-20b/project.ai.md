### srcmorph-modelcard
- H: 1.0
- C: 9C6B6222
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:09:57Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: project
- P: 8CA89A71
- M: 2FA139DC
- K: A1D19F2E
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
The core of srcmorph‑modelcard is a unified system that centralizes error handling, retail catalog management, and payment ledger operations across multiple domains. A shared error catalog in the java package provides consistent platform‑wide error codes, while the other catalog packages expose a single XML file that groups products by category, detailing SKU, title, price, and per‑warehouse inventory levels. The sql ledger package implements a PostgreSQL payments ledger that records customers, accounts, transfers, fraud holds, and aggregates daily transfer volume, tying financial transactions to the product inventory data. Together, these subsystems enable cohesive error management, inventory tracking, and payment processing within the application.

#### Packages
- java/errors — A centralized catalog of platform error codes used throughout the application for consistent error handling.
- java — A centralized catalog of platform error codes used throughout the application for consistent error handling.
- other/catalog — A retail catalog XML file that stores product inventory data, grouping items by category and detailing each product’s SKU, title, EUR price, and per‑warehouse inventory levels.
- other — A retail system’s product catalog and inventory data are stored in a single XML file, grouping items by category and detailing SKU, title, price, and per‑warehouse stock levels.
- . — A unified system for managing error codes, retail product catalogs, and payment ledger data across a multi‑domain application.
- sql/ledger — A PostgreSQL payments ledger that records customers, accounts, transfers and fraud holds, and aggregates daily transfer volume.
- sql — A PostgreSQL payments ledger that records customers, accounts, transfers and fraud holds, and aggregates daily transfer volume.
