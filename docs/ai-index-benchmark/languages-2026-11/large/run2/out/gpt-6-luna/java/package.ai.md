### java
- H: 1.0
- C: DEB2D364
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:17:08Z
- G: 
- A: 0.0.0
- X: package
- F: [errors/](errors/package.ai.md)
---
> Provides a numeric catalogue for identifying platform errors and retrieving their human-readable descriptions.

#### Purpose
- Centralizes platform error codes and messages across billing, storage, networking, reporting, imports, and authentication.

#### Responsibilities
- Exposes the `errors/` catalogue of 6,000 errors, indexed by numeric code for lookup.

#### Key units
- `PlatformErrorCode` — enum mapping error constants to numeric codes and messages, with lookup by number.

#### Data flow
- Catalogue constants populate a code-to-enum index at class initialization; `byNumber(int)` returns the matching entry or `null` for an unknown code.

#### Dependencies
- The error catalogue uses Java `HashMap` and `Map` for its lookup index.

#### Cross-cutting
- Catalogue entries are immutable; the lookup index is populated during initialization and not subsequently modified.
