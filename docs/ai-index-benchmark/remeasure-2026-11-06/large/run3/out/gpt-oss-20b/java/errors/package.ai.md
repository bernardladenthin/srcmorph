### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:20:44Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: 92E2EA26
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A centralized catalog of numeric error codes and messages for consistent platform‑wide error handling.

#### Purpose
- Provides a single source of truth for error codes across billing, storage, network, report, import, auth, and ledger lock domains.
- Enables lookup of error details by numeric identifier.

#### Responsibilities
- **Error cataloging** – defines thousands of `PlatformErrorCode` enum constants with code/message pairs.
- **Lookup service** – offers static `byNumber(int)` to retrieve a code by its numeric key.
- **Metadata exposure** – exposes `getNumber()` and `getMessage()` for consumers.

#### Key units
- `public enum PlatformErrorCode`  
  - Fields: `int code`, `String message`.  
  - Static map `BY_NUMBER` for reverse lookup.  
  - Methods: `getNumber()`, `getMessage()`, `byNumber(int)`.

#### Data flow
1. **Initialization** – static block populates `BY_NUMBER` with all enum constants.  
2. **Lookup** – callers invoke `PlatformErrorCode.byNumber(code)` to obtain the enum instance.  
3. **Usage** – consumers read `getNumber()` and `getMessage()` for logging or user display.

#### Dependencies
- Java collections: `java.util.Map`, `java.util.HashMap`.  
- Standard `java.lang.Enum`.

#### Cross-cutting
- Immutable enum instances; thread‑safe after static initialization.  
- No exception handling beyond returning `null` for unknown codes.  
- Consistent naming convention (`<DOMAIN>_<NNNNN>`) across all constants.
