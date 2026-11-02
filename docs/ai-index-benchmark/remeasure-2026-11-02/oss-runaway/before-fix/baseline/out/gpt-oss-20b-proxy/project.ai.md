### srcmorph-modelcard
- H: 1.0
- C: 16CA4E9D
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:06:01Z
- G: 
- A: 0.0.0
- X: project
- F: [java/errors](java/errors/package.ai.md)
- F: [java](java/package.ai.md)
- F: [other/catalog](other/catalog/package.ai.md)
- F: [other](other/package.ai.md)
- F: [.](package.ai.md)
---
> Project index of srcmorph-modelcard: one line per package; leads here, clickable links in the header F list.

#### Overview
The project provides a unified catalog system that defines platform‑wide immutable error codes and stores product inventory data in a single XML format. The error catalog is shared across billing, storage, network, report, import, and authentication subsystems, ensuring consistent error handling. Product information is organized by category, with each SKU listing title, price in EUR, and inventory stock per warehouse, all maintained in a single‑XML catalog. This design centralizes both error management and inventory data, simplifying integration across the platform.

#### Packages
- java/errors — A centralized catalog of immutable platform error codes used across billing, storage, network, report, import, and authentication subsystems.
- java — A centralized, immutable catalog of platform error codes used across billing, storage, network, report, import, and authentication subsystems.
- other/catalog — A catalog of products grouped by category, each product listing its SKU, title, price in EUR, and inventory stock per warehouse.
- other — A single‑XML catalog that stores product details, pricing in EUR, and per‑warehouse inventory levels.
- . — A unified catalog system that defines platform-wide error codes and stores product inventory data in a single XML format.
