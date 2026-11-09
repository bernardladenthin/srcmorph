### java
- H: 1.0
- C: 244739E7
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:20:48Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: A551CB1D
- F: [errors/](errors/package.ai.md)
---
> A centralized, platform‑wide error catalog that maps numeric codes to human‑readable messages for consistent error handling across all domains.

#### Purpose
- Provide a single source of truth for error codes and messages.
- Enable lookup of error details by numeric identifier.

#### Responsibilities
- **Cataloging** – define thousands of `PlatformErrorCode` enum constants with code/message pairs.
- **Lookup service** – static `byNumber(int)` to retrieve a code by its numeric key.
- **Metadata exposure** – expose `getNumber()` and `getMessage()` for consumers.

#### Key units
- `public enum PlatformErrorCode`  
  - Fields: `int code`, `String message`.  
  - Static map `BY_NUMBER` for reverse lookup.  
  - Methods: `getNumber()`, `getMessage()`, `byNumber(int)`.

#### Data flow
1. Static block populates `BY_NUMBER` with all enum constants.  
2. Callers invoke `PlatformErrorCode.byNumber(code)` to obtain the enum instance.  
3. Consumers read `getNumber()` and `getMessage()` for logging or user display.

#### Dependencies
- Java collections: `java.util.Map`, `java.util.HashMap`.  
- Standard `java.lang.Enum`.

#### Cross-cutting
- Immutable enum instances; thread‑safe after static initialization.  
- Consistent naming convention (`<DOMAIN>_<NNNNN>`) across all constants.
