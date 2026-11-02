### srcmorph-modelcard
- H: 1.0
- C: 3B8AD627
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:52:37Z
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
The project combines platform error catalogs, product and warehouse inventory reference data, and customer-account ledger records. The SQL ledger subsystem stores accounts, holds, and transfers and reports daily transfer volume, while the Java packages provide stable numeric error identifiers and descriptions.

#### Packages
- java/errors — Provides a stable numeric catalog of platform errors and their human-readable descriptions for callers to identify and interpret failures.
- java — Provides a stable numeric catalog of platform failures so callers can identify errors and retrieve their descriptions.
- other/catalog — Maintains a categorized EUR-priced product catalog with warehouse stock levels and supplier recall notices for selected products.
- other — Organizes EUR-priced products by category and records warehouse inventory and selected supplier recall notices.
- . — Provides platform error identification, product and inventory reference data, and customer-account ledger records with transfer reporting.
- sql/ledger — Stores customer-owned bank accounts, account holds, and monetary transfers, and summarizes daily transfer volume.
- sql — Maintains customer bank accounts, account holds, and transfers, with daily reporting of transfer volume.
