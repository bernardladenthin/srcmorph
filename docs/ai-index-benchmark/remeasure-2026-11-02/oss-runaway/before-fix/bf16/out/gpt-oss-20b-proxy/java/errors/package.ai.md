### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T08:15:19Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A centralized catalog of platform error codes with fast numeric lookup.

#### Purpose
- Centralize all platform error codes in a single enum.
- Provide O(1) retrieval of an error by its numeric identifier.

#### Responsibilities
- Define error code constants with numeric IDs and messages.
- Build and expose a lookup map for reverse lookup.
- Offer accessor methods for code number and message.

#### Key units
- **`PlatformErrorCode` enum** – holds constants, numeric ID, message, and a static `Map<Integer, PlatformErrorCode>` for lookup.
- **`byNumber(int)`** – static method returning the enum instance for a given code or `null`.

#### Data flow
1. Enum constants are instantiated with an integer and a string.
2. Static block iterates over `values()` to populate `BY_NUMBER`.
3. Clients call `byNumber(code)` to obtain the corresponding enum instance.
4. Clients access `getNumber()` or `getMessage()` on the instance.

#### Dependencies
- `java.util.Map` and `java.util.HashMap` for the lookup map.

#### Cross-cutting
- Immutable enum instances; thread‑safe static map after initialization.
- No external libraries or complex error handling beyond returning `null` for unknown codes.
