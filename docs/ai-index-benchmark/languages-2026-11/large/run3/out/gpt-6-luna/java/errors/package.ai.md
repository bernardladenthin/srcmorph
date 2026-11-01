### java/errors
- H: 1.0
- C: BBE675B9
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T02:25:54Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> Provides a stable numeric catalog of platform error codes and messages for billing, storage, network, reporting, import, and authentication failures.

#### Purpose
- Defines 6,000 numbered error constants, indexed from 1 to 6000.
- Supports retrieving an error definition by numeric code.

#### Responsibilities
- Catalog error definitions across the `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, and `AUTH` categories.
- Provide each definition’s numeric code and message, with lookup by code.

#### Key units
- `PlatformErrorCode` — enum holding each error’s number and message; exposes `getNumber()`, `getMessage()`, and `byNumber(int)`.

#### Data flow
- Enum initialization indexes all constants in `BY_NUMBER`; `byNumber(int)` returns the matching constant or `null` when no code matches.

#### Dependencies
- Uses `java.util.HashMap` and `java.util.Map` for the number-to-enum lookup.

#### Cross-cutting
- Enum constants and their fields are immutable; `BY_NUMBER` is populated during static initialization and read afterward.
