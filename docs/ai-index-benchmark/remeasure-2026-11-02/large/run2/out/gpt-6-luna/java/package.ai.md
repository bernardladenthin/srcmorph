### java
- H: 1.0
- C: 244739E7
- D: 2026-11-01T23:23:14Z
- T: 2026-11-01T23:52:14Z
- G: 
- A: 0.0.0
- X: package
- F: [errors/](errors/package.ai.md)
---
> Provides a stable numeric catalog of platform failures so callers can identify errors and retrieve their descriptions.

#### Purpose
- Defines named error codes and human-readable messages across billing, storage, networking, reporting, imports, authentication, and ledger locking.
- Enables lookup of a catalog entry by numeric code.

#### Responsibilities
- Maintain and index the `PlatformErrorCode` catalog, including `LEDGER_LOCK` codes in the 2701–3300 range.

#### Key units
- `PlatformErrorCode` — enum of numeric codes and messages; exposes `getNumber()`, `getMessage()`, and `byNumber(int)`.

#### Data flow
- Enum constants are indexed by number in `BY_NUMBER` during static initialization.
- `byNumber(int)` returns the matching enum value, or `null` when no code matches.

#### Dependencies
- `java.util.HashMap` and `java.util.Map` — store the number-to-enum lookup.

#### Cross-cutting
- Enum values are immutable after initialization; the private `BY_NUMBER` index is populated once and not modified afterward.
