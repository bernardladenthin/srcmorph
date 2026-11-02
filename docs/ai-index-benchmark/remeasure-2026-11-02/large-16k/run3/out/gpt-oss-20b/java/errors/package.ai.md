### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:40:07Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A centralized enumeration that maps platform error numbers to descriptive messages and provides fast lookup by numeric ID.

#### Purpose
- Central repository of platform error codes for billing, storage, network, report, import, auth, and ledger lock failures.

#### Responsibilities
- Define error code constants with numeric IDs and descriptions.
- Provide efficient lookup of error codes by ID.
- Expose immutable mapping of IDs to enum instances.

#### Key units
- **`PlatformErrorCode` enum** – holds constants like `BILLING_ERROR(1001, "Billing failure")`; implements `forId(int)` and `getIdMap()` for lookup.
- **`ID_MAP`** – static immutable `Map<Integer, PlatformErrorCode>` built in a static block.

#### Data flow
1. On class load, static block iterates over `values()` to populate `ID_MAP`.
2. `forId(id)` queries `ID_MAP` to return the matching enum or throw `IllegalArgumentException`.
3. `getIdMap()` returns the immutable map for external use.

#### Dependencies
- `java.util.HashMap`
- `java.util.Map`

#### Cross-cutting
- Thread‑safe: `ID_MAP` is immutable after initialization, allowing concurrent reads.
- Error handling: `forId` throws `IllegalArgumentException` for unknown IDs, ensuring callers handle missing codes explicitly.
