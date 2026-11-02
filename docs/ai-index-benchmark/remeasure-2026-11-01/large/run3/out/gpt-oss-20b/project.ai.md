### srcmorph-modelcard
- H: 1.0
- C: C054C852
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T15:16:34Z
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
The project provides a unified platform that centralizes error handling, product cataloging, and financial ledger management for a multi‑service e‑commerce ecosystem. A core error catalogue maps numeric identifiers to descriptive messages, ensuring consistent handling across services. The product catalog is defined in structured XML, detailing categories, SKUs, titles, prices in EUR, and per‑warehouse inventory levels. A PostgreSQL schema stores customers, accounts, transfers, and holds, and exposes a daily transfer volume view to support financial reporting.

#### Packages
- java/errors — A central catalogue of platform error codes that maps numeric identifiers to descriptive messages for consistent error handling across the system.
- java — A centralized catalogue of platform error codes that maps numeric identifiers to descriptive messages for consistent error handling across the system.
- other/catalog — A structured XML catalog of e‑commerce products, detailing categories, SKUs, titles, prices in EUR, and per‑warehouse inventory levels.
- other — A structured XML catalog that defines e‑commerce products, their categories, prices in EUR, and per‑warehouse inventory levels.
- . — A unified platform for error handling, product cataloging, and financial ledger management across a multi‑service e‑commerce ecosystem.
- sql/ledger — A PostgreSQL schema that records customers, accounts, transfers, and holds, and exposes a daily transfer volume view.
- sql — A PostgreSQL schema that stores customers, accounts, transfers, and holds, and provides a daily transfer volume view.
