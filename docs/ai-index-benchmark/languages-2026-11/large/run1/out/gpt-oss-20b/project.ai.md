### srcmorph-modelcard
- H: 1.0
- C: 6738EB8D
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T01:09:56Z
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
The core of srcmorph‑modelcard is a unified infrastructure layer that centralizes error handling, stores an e‑commerce product catalog, and manages a PostgreSQL payments ledger with daily volume aggregation. The error subsystem exposes a single, immutable catalogue of platform error codes for consistent lookup across the system. The catalog subsystem provides a structured XML catalog of product listings, pricing, and per‑warehouse inventory data. The ledger subsystem implements a PostgreSQL database that records customers, accounts, and transfer transactions while exposing a daily volume aggregation view for reporting.

#### Packages
- java/errors — A centralized catalogue of platform error codes, enabling efficient lookup and consistent error handling across the system.
- java — A centralized, immutable catalogue of platform error codes for consistent lookup and handling across the system.
- other/catalog — A structured XML catalog of e‑commerce products with per‑warehouse inventory data.
- other — A structured XML catalog that stores e‑commerce product listings, pricing, and per‑warehouse inventory levels.
- . — A unified infrastructure layer that centralizes error codes, stores an e‑commerce product catalog, and manages a PostgreSQL payment ledger with daily volume aggregation.
- sql/ledger — A PostgreSQL payments ledger storing customers, accounts, and transfer records with a daily volume view.
- sql — A PostgreSQL payments ledger that stores customers, accounts, and transfer records while providing a daily volume aggregation view.
