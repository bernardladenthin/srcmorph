### java/errors
- H: 1.0
- C: D12AEEF3
- D: 2026-11-06T00:01:11Z
- T: 2026-11-06T00:09:20Z
- G: 1.5.0-SNAPSHOT
- A: 0.0.0
- X: package
- P: 38DD9D87
- M: 2FA139DC
- K: 2EB4463A
- F: [PlatformErrorCode.java](PlatformErrorCode.java.ai.md)
---
> A centralized catalog of platform error codes used throughout the application for consistent error handling.

#### Purpose
- Provide a single source of truth for numeric error codes and their messages.
- Enable lookup of error details by code number.

#### Responsibilities
- Define all platform error codes as enum constants.
- Offer a thread‑safe lookup mechanism (`byNumber`) to retrieve an enum by its numeric ID.
- Expose getters for the numeric ID and human‑readable message.

#### Key units
- **`PlatformErrorCode` enum** – holds constants like `BILLING_00001`, `STORAGE_00002`, `LEDGER_LOCK_02701`, `BILLING_03800`; each has a numeric ID and message.
- **`BY_NUMBER` map** – static `HashMap<Integer, PlatformErrorCode>` populated in a static block for O(1) lookup.
- **`byNumber(int)`** – static method returning the enum constant for a given number or `null`.

#### Data flow
1. Application code references a `PlatformErrorCode` constant or calls `byNumber(code)`.
2. `byNumber` retrieves the enum from `BY_NUMBER`.
3. The caller obtains the numeric ID via `getNumber()` or the message via `getMessage()`.

#### Dependencies
- `java.util.Map`, `java.util.HashMap`, `java.lang.Enum`.

#### Cross-cutting
- Immutable enum design ensures thread safety.
- No external libraries or configuration; all data is hard‑coded within the enum.
