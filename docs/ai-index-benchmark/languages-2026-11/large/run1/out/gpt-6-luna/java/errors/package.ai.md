### java/errors
- H: 1.0
- C: BBE675B9
- D: 2026-10-31T21:24:26Z
- T: 2026-11-01T00:38:00Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> Defines stable numeric error identifiers and messages for platform failures across billing, storage, networking, reporting, importing, and authentication.

#### Purpose
- Provides a centralized catalogue of platform error codes and their messages.
- Supports lookup of an error code by its numeric identifier.

#### Responsibilities
- Enumerates 6,000 category-prefixed errors, numbered 1–6000 across `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, and `AUTH`.

#### Key units
- `PlatformErrorCode` — enum whose constants store a numeric code and message; exposes `getNumber()`, `getMessage()`, and `byNumber(int)`.

#### Data flow
- `byNumber(int)` looks up the supplied number in the static `BY_NUMBER` map populated from enum constants during class initialization; returns `null` if no code matches.

#### Dependencies
- Uses `java.util.HashMap` and `java.util.Map` for numeric-code lookup.

#### Cross-cutting
- Enum constants and their fields are immutable; `BY_NUMBER` is populated at initialization and is not shown being modified afterward.
