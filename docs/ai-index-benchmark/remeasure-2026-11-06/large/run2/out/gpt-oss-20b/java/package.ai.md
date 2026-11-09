### java
- H: 1.0
- C: 244739E7
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:14:58Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: 1860F4FD
- F: [errors/](errors/package.ai.md)
---
> A centralized, type‑safe catalogue of platform error codes that can be looked up by numeric ID and used across all domains.

#### Purpose
- Centralises platform error codes for all domains (auth, billing, storage, network, report, import).  
- Enables lookup of an error code by its numeric value.  
- Provides immutable, thread‑safe error constants.

#### Responsibilities
- Define a comprehensive enum of error codes with numeric IDs and messages.  
- Offer a fast lookup mechanism (`byNumber`) for reverse mapping from code to enum.  
- Expose read‑only accessors (`getNumber`, `getMessage`) for client code.

#### Key units
- **`PlatformErrorCode` enum** – holds constants like `AUTH_03942`, `BILLING_03943`, …, `IMPORT_06000`.  
  - Fields: `number`, `message`, static `BY_NUMBER` map.  
  - Constructor: `PlatformErrorCode(int number, String message)`.  
  - Methods: `getNumber()`, `getMessage()`, `byNumber(int)`.

#### Data flow
1. Enum constants are instantiated at class load time.  
2. Static block populates `BY_NUMBER` map with each constant keyed by its numeric ID.  
3. Clients call `PlatformErrorCode.byNumber(code)` to retrieve the corresponding enum, then use `getMessage()` or `getNumber()` as needed.

#### Dependencies
- Java standard library: `java.util.Map`, `java.util.HashMap`, `java.lang.Enum`.

#### Cross-cutting
- Thread safety: enum constants and the lookup map are immutable after initialization.  
- No external configuration or database; all data is compile‑time constants.
