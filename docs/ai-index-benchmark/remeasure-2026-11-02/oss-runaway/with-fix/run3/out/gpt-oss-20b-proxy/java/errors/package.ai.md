### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:38:07Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A centralized catalogue of platform error codes that maps numeric IDs to human‑readable messages and supports reverse lookup.

#### Purpose
- Store and expose a comprehensive list of platform error codes.
- Enable efficient lookup of an error code’s description by its numeric ID.

#### Responsibilities
- **Error code definition** – enumerate thousands of error codes as enum constants.
- **Reverse lookup** – provide a static method to retrieve an enum instance from a numeric code.
- **Metadata exposure** – expose numeric code and message via getters.

#### Key units
- `PlatformErrorCode` (enum) – holds `int number`, `String message`; static `Map<Integer, PlatformErrorCode> BY_NUMBER` for reverse lookup; methods: `getNumber()`, `getMessage()`, `byNumber(int)`.

#### Data flow
1. Class loading triggers static block that populates `BY_NUMBER` by iterating over `values()`.
2. Client calls `PlatformErrorCode.byNumber(code)` → map lookup → returns enum instance or `null`.
3. Client accesses `getNumber()` or `getMessage()` on the returned instance.

#### Dependencies
- Java standard collections: `java.util.Map`, `java.util.HashMap`.
- Java enum infrastructure.

#### Cross-cutting
- Immutable enum instances ensure thread safety.
- No external libraries or configuration; all data is hard‑coded in the enum.
