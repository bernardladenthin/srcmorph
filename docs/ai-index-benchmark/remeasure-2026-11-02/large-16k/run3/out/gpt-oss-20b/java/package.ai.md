### java
- H: 1.0
- C: 244739E7
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:40:11Z
- G: 
- A: 0.0.0
- X: package
- F: [errors/](errors/package.ai.md)
---
> A centralized error‑code registry that maps numeric platform error IDs to descriptive messages for quick lookup and consistent error handling across the system.

#### Purpose
- Provide a single source of truth for platform error codes.
- Enable fast, thread‑safe retrieval of error information by numeric ID.

#### Responsibilities
- Define a comprehensive set of error constants covering billing, storage, network, report, import, auth, and ledger lock failures.
- Build and expose an immutable map of error IDs to enum instances for efficient lookup.
- Offer utility methods for retrieving errors by ID and accessing the full mapping.

#### Key units
- **`PlatformErrorCode` enum** – constants such as `BILLING_ERROR(1001, "Billing failure")`; implements `forId(int)` and `getIdMap()`.
- **`ID_MAP`** – static immutable `Map<Integer, PlatformErrorCode>` constructed in a static block.

#### Data flow
1. Class loading triggers a static block that populates `ID_MAP` from `values()`.
2. `forId(id)` queries `ID_MAP` to return the matching enum or throw `IllegalArgumentException`.
3. `getIdMap()` provides the immutable map for external consumers.

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`

#### Cross-cutting
- Thread‑safe: `ID_MAP` is immutable after initialization, allowing concurrent reads.
- Error handling: `forId` throws `IllegalArgumentException` for unknown IDs, enforcing explicit handling by callers.
