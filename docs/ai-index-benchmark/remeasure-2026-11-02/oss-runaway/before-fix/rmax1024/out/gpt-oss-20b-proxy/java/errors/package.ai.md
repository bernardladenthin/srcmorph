### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:18:03Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A centralized, immutable catalog of platform error codes used across the billing, storage, network, report, import, auth, and ledger‑lock subsystems.

#### Purpose
- Provide a single source of truth for platform error codes.
- Enable consistent error handling and messaging across the system.

#### Responsibilities
- Define and expose a comprehensive set of error codes.
- Offer efficient reverse lookup from numeric code to enum constant.
- Serve as the foundation for error reporting in multiple subsystems.

#### Key units
- **`PlatformErrorCode` enum** – immutable constants each with a unique `int number` and `String message`; includes static `Map<Integer, PlatformErrorCode> BY_NUMBER` for reverse lookup; public methods `getNumber()`, `getMessage()`, and `byNumber(int)`.

#### Data flow
1. At class load, static initializer iterates over all enum constants, populating `BY_NUMBER`.
2. Clients request an error code by number via `byNumber(int)`.
3. The enum returns the corresponding constant, from which callers can retrieve the numeric code and human‑readable message.

#### Dependencies
- Java standard library: `java.util.Map`, `java.util.HashMap`.

#### Cross-cutting
- Immutable design: no mutators, thread‑safe static initialization.
- Consistent API across subsystems: same enum used for billing, storage, network, report, import, auth, and ledger‑lock error handling.
