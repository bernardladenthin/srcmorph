### ai
- H: 1.0
- C: 7299836A
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:18:15Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A unified catalog of immutable platform error codes and an XML product inventory definition for an e‑commerce system.

#### Purpose
- Centralize platform error code definitions for consistent error handling across multiple subsystems.
- Provide a static XML catalog of product inventory, prices, and stock levels for an e‑commerce platform.

#### Responsibilities
- **Error catalog**: define immutable error codes, enable reverse lookup by numeric code, expose message and number.
- **Inventory catalog**: declare categories, products, prices, and warehouse‑specific stock; support optional recall information.

#### Key units
- **`PlatformErrorCode` enum** – immutable constants (`int number`, `String message`), static `Map<Integer, PlatformErrorCode> BY_NUMBER`, methods `getNumber()`, `getMessage()`, `byNumber(int)`.
- **XML catalog** – root element `<catalog>` (namespace `urn:example:catalog`), child `<category>` (attributes `id`, `name`), `<product>` (attribute `sku`, child `<title>`, `<price>`, `<inv:stock>`, optional `<inv:recall>`).  
  - `<inv:stock>` (namespace `urn:example:inventory`) has `warehouse` attribute and quantity value.  
  - `<inv:recall>` has `batch` attribute and text “supplier recall”.

#### Data flow
- **Error handling**: static initializer populates `BY_NUMBER`; callers use `byNumber(int)` → `PlatformErrorCode` → `getNumber()` / `getMessage()` for reporting.
- **Inventory**: static XML data is read by the e‑commerce system to populate product listings, pricing, and per‑warehouse stock availability.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap` for error catalog.
- XML namespaces: `urn:example:catalog` for catalog elements, `urn:example:inventory` for inventory elements.

#### Cross-cutting
- Immutable, thread‑safe design of `PlatformErrorCode`.
- Consistent namespace usage (`inv:`) and attribute naming (`id`, `name`, `sku`, `warehouse`, `batch`) across inventory XML.
- Fixed currency (EUR) in `<price>` elements.
