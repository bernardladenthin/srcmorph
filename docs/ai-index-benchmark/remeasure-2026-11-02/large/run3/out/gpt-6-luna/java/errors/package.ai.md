### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:07:23Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> Provides stable, numeric identifiers and human-readable messages for platform failures across billing, storage, networking, reporting, imports, authentication, and ledger locking.

#### Purpose
- Defines the platform’s error-code catalog and supports lookup by numeric identifier.

#### Responsibilities
- Categorizes failures with domain-prefixed codes, including `BILLING`, `STORAGE`, `NETWORK`, `REPORT`, `IMPORT`, `AUTH`, and `LEDGER_LOCK`.

#### Key units
- `PlatformErrorCode` — enum binding each error’s numeric code to its name and message; exposes `getNumber()`, `getMessage()`, and `byNumber(int)`.

#### Data flow
- Enum initialization indexes constants by number in `BY_NUMBER`; `byNumber(int)` returns the matching constant or `null` when unmapped.

#### Dependencies
- Uses `java.util.HashMap` and `java.util.Map` for numeric lookup.

#### Cross-cutting
- Enum constants and fields are immutable; the private lookup map is populated during class initialization and read without synchronization.
