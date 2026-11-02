### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:10:40Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A central catalogue of platform error codes with numeric IDs, messages, and fast lookup by code.

#### Purpose
- Store and expose platform-wide error codes for billing, storage, network, report, import, auth, and ledger lock failures.
- Provide efficient lookup of an error code by its numeric identifier.

#### Responsibilities
- Define a comprehensive enum of error codes.
- Build a static lookup map for constant‑time retrieval.
- Expose getters for numeric code and message.

#### Key units
- **`PlatformErrorCode` enum** – each constant holds an integer code and a descriptive message; implements `Enum`.
- **`BY_NUMBER` map** – `Map<Integer, PlatformErrorCode>` populated in a static block for O(1) lookup.
- **`byNumber(int)` method** – returns the enum instance for a given code or `null` if not found.

#### Data flow
1. Class initialization iterates over `values()` to populate `BY_NUMBER`.
2. Clients call `PlatformErrorCode.byNumber(code)` to retrieve the corresponding enum.
3. The enum provides `getNumber()` and `getMessage()` for downstream use.

#### Dependencies
- Java collections: `java.util.Map`, `java.util.HashMap`.
- Standard `java.lang.Enum`.

#### Cross-cutting
- Thread‑safe: static map is immutable after initialization.
- No exception handling beyond returning `null` for unknown codes.
