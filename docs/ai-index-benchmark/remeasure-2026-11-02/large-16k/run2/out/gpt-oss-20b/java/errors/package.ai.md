### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-01T23:23:14Z
- T: 2026-11-02T00:33:49Z
- G: 
- A: 0.0.0
- X: package
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A centralized, immutable mapping of platform‑wide error codes to human‑readable messages, enabling consistent error handling across billing, storage, network, report, import, auth, and ledger lock domains.

#### Purpose
- Provide a single source of truth for all platform error codes.
- Enable consistent error handling and lookup across the platform.

#### Responsibilities
- Define all error codes as enum constants with numeric codes and messages.
- Offer fast lookup of an error code by its numeric value.
- Expose getters for the numeric code and message.

#### Key units
- **`PlatformErrorCode` enum** – holds constants like `BILLING_00001`, `STORAGE_00002`, …, `LEDGER_LOCK_02899`, `BILLING_03620`; each constant stores an `int code` and a `String message`.
- **`BY_NUMBER` map** – static `Map<Integer, PlatformErrorCode>` populated in a static block for O(1) lookup.
- **`byNumber(int)`** – static method returning the enum instance for a given code or `null` if not found.

#### Data flow
1. **Definition**: Enum constants are declared with a numeric code and message.
2. **Initialization**: Static block populates `BY_NUMBER` mapping each code to its enum instance.
3. **Lookup**: `byNumber` receives an integer code, performs a map lookup, and returns the corresponding `PlatformErrorCode` or `null`.

#### Dependencies
- `java.util.Map` and `java.util.HashMap` for the lookup map.

#### Cross-cutting
- Immutable enum design ensures thread safety.
- No external validation; assumes valid code/message pairs.
- Consistent API (`getNumber()`, `getMessage()`) across all error domains.
