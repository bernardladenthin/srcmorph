### ai
- H: 1.0
- C: 7299836A
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:39:43Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
---
> A unified repository for platform error codes and an XML‑based e‑commerce catalog schema.

#### Purpose
- Centralise platform error codes for consistent use across subsystems.
- Provide a structured XML definition for product inventory, pricing, and recall status.

#### Responsibilities
- **Error catalog**: define, store, and lookup error codes with numeric IDs and messages.
- **Catalog schema**: describe product hierarchy, inventory per warehouse, pricing, and recall information in a single XML document.

#### Key units
- **`PlatformErrorCode` enum** – immutable constants with `int code`, `String message`; lookup via `byNumber(int)`.
- **XML schema elements** – `catalog`, `category`, `product`, `inv:stock`, `inv:recall` with defined namespaces and attributes.

#### Data flow
- Error codes are loaded into a static map (`BY_NUMBER`) at class initialization; `byNumber` retrieves a code by its integer key.
- E‑commerce systems parse the XML catalog to build product listings, compute per‑warehouse inventory, and flag recalled items for compliance.

#### Dependencies
- Java: `java.util.Map`, `java.util.HashMap`, standard `Enum` API.
- XML: namespaces `urn:example:catalog` and `http://example.com/inventory`.

#### Cross-cutting
- Thread‑safe, immutable design for error codes.
- Consistent use of the `inv` namespace for all inventory‑related elements.
- Prices standardized to EUR (`currency="EUR"`).
