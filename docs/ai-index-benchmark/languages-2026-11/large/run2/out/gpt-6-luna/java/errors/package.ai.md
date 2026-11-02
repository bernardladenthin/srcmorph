### java/errors
- H: 1.0
- C: BBE675B9
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:17:03Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> Provides a stable numeric catalogue for identifying and describing platform failures across billing, storage, networking, reporting, imports, and authentication.

#### Purpose
- Maps platform error identifiers to numeric codes and human-readable messages.
- Supports lookup of an error by numeric code.

#### Responsibilities
- Maintain the generated `PlatformErrorCode` catalogue of 6,000 errors across `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, and `AUTH` categories.
- Index catalogue entries by code for lookup.

#### Key units
- `PlatformErrorCode` — enum associating each error constant with its numeric code and message; exposes `getNumber()`, `getMessage()`, and `byNumber(int)`.

#### Data flow
- Enum constants define codes 1–6000 and their messages; class initialization populates `BY_NUMBER` from `values()`.
- `byNumber(int)` returns the indexed enum value, or `null` if the code is unknown.

#### Dependencies
- Java collections: `HashMap` and `Map`.

#### Cross-cutting
- Enum constants and fields are immutable; `BY_NUMBER` is populated during class initialization and is not subsequently modified.
- Unknown codes produce `null`; the catalogue defines no explicit exception handling.
