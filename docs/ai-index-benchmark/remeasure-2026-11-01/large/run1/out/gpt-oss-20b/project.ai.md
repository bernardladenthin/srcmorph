### srcmorph-modelcard
- H: 1.0
- C: B974A217
- D: 2026-11-01T14:09:33Z
- T: 2026-11-01T14:38:13Z
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
The project provides a unified repository that centralises error codes, product catalog data, and payment‑ledger test data for a multi‑service platform. A shared error‑code catalogue is used across billing, storage, network, report, import, auth, and ledger‑lock modules, while a static XML product catalog supplies e‑commerce services with metadata, EUR pricing, and per‑warehouse inventory. PostgreSQL data‑dumps seed a payments ledger with customers, accounts, transfers, holds, and a daily‑volume view, enabling end‑to‑end testing of the platform’s financial workflows.

#### Packages
- java/errors — A centralized catalogue of platform‑wide error codes used across billing, storage, network, report, import, auth, and ledger‑lock modules.
- java — A centralized catalogue of platform‑wide error codes used across billing, storage, network, report, import, auth, and ledger‑lock modules.
- other/catalog — A structured XML catalog that lists products by category, including pricing and per‑warehouse inventory.
- other — A static XML product catalog that supplies e‑commerce services with product metadata, pricing in EUR, and per‑warehouse inventory levels.
- . — A unified repository that centralises error codes, product catalog data, and payment‑ledger test data for a multi‑service platform.
- sql/ledger — A PostgreSQL data‑dump that seeds a payments ledger with customers, accounts, transfers, holds, and a daily‑volume view.
- sql — A PostgreSQL data‑dump that seeds a payments ledger with customers, accounts, transfers, holds, and a daily‑volume view.
