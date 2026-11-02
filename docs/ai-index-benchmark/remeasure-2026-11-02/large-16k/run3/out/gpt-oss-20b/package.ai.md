### ai
- H: 1.0
- C: 7299836A
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:40:27Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A unified repository for platform error codes and e‑commerce catalog data, enabling consistent error handling and product inventory management across services.  

#### Purpose  
- Centralize platform error codes for fast, thread‑safe lookup.  
- Provide a static XML catalog of products, prices, and per‑warehouse inventory for read‑only consumption.  

#### Responsibilities  
- **Error Registry**: Define and expose a comprehensive set of error constants; build an immutable ID‑to‑enum map; provide lookup utilities.  
- **Catalog Management**: Store product metadata, pricing, inventory, and recall flags in a single XML file; expose data for downstream services.  

#### Key units  
- **`PlatformErrorCode` enum** – error constants (e.g., `BILLING_ERROR(1001, "Billing failure")`), `forId(int)` lookup, `getIdMap()` accessor.  
- **`ID_MAP`** – static immutable `Map<Integer, PlatformErrorCode>` built at class load.  
- **`catalog.xml`** – root `<catalog>` (namespace `urn:example:catalog`) containing `<category>` and `<product>` elements; inventory via `<inv:stock>` (namespace `urn:example:inventory`); optional `<inv:recall>`.  

#### Data flow  
1. **Error Registry**  
   - Class loading populates `ID_MAP` from `values()`.  
   - `forId(id)` retrieves enum or throws `IllegalArgumentException`.  
   - `getIdMap()` returns the immutable map for external use.  

2. **Catalog**  
   - Application start loads `catalog.xml`.  
   - Services parse `<product>` entries to obtain SKU, title, price, and warehouse stock.  
   - Data is served read‑only to product display, pricing, and stock‑check services.  

#### Dependencies  
- Java collections (`HashMap`, `Map`) for error mapping.  
- XML namespaces `urn:example:catalog` and `urn:example:inventory` for catalog structure.  

#### Cross-cutting  
- Immutable, thread‑safe error map ensures concurrent reads without synchronization.  
- Uniform XML schema: all prices use `currency="EUR"`; inventory and recall data are consistently namespaced.  
- Error lookup enforces explicit handling via `IllegalArgumentException` for unknown IDs.
